package dk.magenta.datafordeler.cpr.data.person;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.Registration;
import dk.magenta.datafordeler.core.database.RegistrationReference;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.fapi.FapiService;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.data.CprEntityManager;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.parsers.CprParser;
import dk.magenta.datafordeler.cpr.parsers.PersonParser;
import dk.magenta.datafordeler.cpr.records.PersonDataRecord;
import dk.magenta.datafordeler.cpr.records.Record;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Created by lars on 16-05-17.
 */
@Component
public class PersonEntityManager extends CprEntityManager {

    @Autowired
    private PersonEntityService personEntityService;

    @Autowired
    private PersonParser personParser;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private QueryManager queryManager;

    @Autowired
    private ObjectMapper objectMapper;

    public PersonEntityManager() {
        this.managedEntityClass = PersonEntity.class;
        this.managedEntityReferenceClass = PersonEntityReference.class;
        this.managedRegistrationClass = PersonRegistration.class;
        this.managedRegistrationReferenceClass = PersonRegistrationReference.class;
    }

    @Override
    protected String getBaseName() {
        return "person";
    }

    @Override
    public FapiService getEntityService() {
        return this.personEntityService;
    }

    @Override
    public String getSchema() {
        return PersonEntity.schema;
    }

    @Override
    public List<PersonRegistration> parseRegistration(String registrationData) throws IOException, ParseException {
        return this.parseRegistration(new ByteArrayInputStream(registrationData.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public List<PersonRegistration> parseRegistration(InputStream registrationData) throws ParseException, IOException {
        ArrayList<PersonRegistration> registrations = new ArrayList<>();
        List<Record> records = personParser.parse(registrationData, "utf-8");
        ListHashMap<Integer, PersonDataRecord> recordMap = new ListHashMap<>();
        for (Record record : records) {
            if (record instanceof PersonDataRecord) {
                PersonDataRecord personDataRecord = (PersonDataRecord) record;
                int cprNumber = personDataRecord.getCprNumber();
                recordMap.add(cprNumber, personDataRecord);
            }
        }
        System.out.println("recordMap: "+recordMap);

        Session session = sessionManager.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        for (int cprNumber : recordMap.keySet()) {
            System.out.println("cprNumber: "+cprNumber);
            List<PersonDataRecord> recordList = recordMap.get(cprNumber);
            PersonEntity entity = queryManager.getItem(session, PersonEntity.class, Collections.singletonMap("cprNumber", cprNumber));
            if (entity == null) {
                entity = new PersonEntity(UUID.randomUUID(), "test");
                entity.setCprNumber(cprNumber);
            }

            ListHashMap<String, PersonDataRecord> ajourRecords = new ListHashMap<>();
            TreeSet<String> sortedTimestamps = new TreeSet<>();
            for (PersonDataRecord record : recordList) {
                System.out.println("record: "+record);
                Set<String> timestamps = record.getTimestamps();
                for (String timestamp : timestamps) {
                    if (timestamp != null && !timestamp.isEmpty()) {
                        ajourRecords.add(timestamp, record);
                        sortedTimestamps.add(timestamp);
                    }
                }
            }

            // Create one Registration per unique timestamp
            PersonRegistration lastRegistration = null;
            for (String timestamp : sortedTimestamps) {
                OffsetDateTime registrationFrom = CprParser.parseTimestamp(timestamp);
                System.out.println("timestamp: "+timestamp);
                System.out.println("registrationFrom: "+registrationFrom);

                PersonRegistration registration = entity.getRegistration(registrationFrom);
                if (registration == null) {
                    if (lastRegistration == null) {
                        registration = new PersonRegistration();
                    } else {
                        //registration = this.cloneRegistration(lastRegistration);
                        registration = new PersonRegistration();
                        for (PersonEffect originalEffect : lastRegistration.getEffects()) {
                            PersonEffect newEffect = new PersonEffect(registration, originalEffect.getEffectFrom(), originalEffect.getEffectTo());
                            for (PersonBaseData originalData : originalEffect.getDataItems()) {
                                originalData.addEffect(newEffect);
                            }
                        }
                    }
                    registration.setRegistrationFrom(registrationFrom);
                    System.out.println("created new registration at "+registrationFrom);
                }
                registration.setEntity(entity);
                entity.addRegistration(registration);

                // Each record sets its own basedata
                for (PersonDataRecord record : ajourRecords.get(timestamp)) {
                    // Take what we need from the record and put it into dataitems
                    ListHashMap<PersonEffect, PersonBaseData> dataItems = record.getDataEffects(timestamp);
                    if (dataItems != null) {
                        for (PersonEffect effect : dataItems.keySet()) {
                            for (PersonBaseData data : dataItems.get(effect)) {
                                effect.setRegistration(registration);
                                data.addEffect(effect);
                                //PersonEffect effect = registration.getEffect(effectFrom, effectTo);
                                /*if (effect == null) {
                                    effect = new PersonEffect(registration, effectFrom, effectTo);
                                }
                                data.addEffect(effect);*/
                            }
                        }
                    }
                }

                if (lastRegistration != null) {
                    lastRegistration.setRegistrationTo(registrationFrom);
                }
                lastRegistration = registration;
                registrations.add(registration);

            }
            System.out.println(registrations);
            try {
                System.out.println("registrations: "+objectMapper.writeValueAsString(registrations));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            for (PersonRegistration registration : registrations) {
                try {
                    queryManager.saveRegistration(session, entity, registration);
                } catch (DataFordelerException e) {
                    e.printStackTrace();
                }
            }
        }
        transaction.commit();
        session.close();
        return registrations;
    }



    @Override
    protected RegistrationReference createRegistrationReference(URI uri) {
        return new PersonRegistrationReference(uri);
    }

}

package dk.magenta.datafordeler.cpr.data.person;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.RegistrationReference;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.fapi.FapiService;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.CprPlugin;
import dk.magenta.datafordeler.cpr.data.CprEntityManager;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.parsers.CprParser;
import dk.magenta.datafordeler.cpr.parsers.PersonParser;
import dk.magenta.datafordeler.cpr.records.person.PersonDataRecord;
import dk.magenta.datafordeler.cpr.records.Record;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
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
        ArrayList<PersonRegistration> allRegistrations = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(registrationData));

        boolean done = false;
        while (!done) {
            int limit = 1000;
            StringJoiner buffer = new StringJoiner("\n");
            int i;
            for (i = 0; i < limit; i++) {
                String line = reader.readLine();
                if (line != null) {
                    buffer.add(line);
                } else {
                    done = true;
                }
            }

            InputStream chunk = new ByteArrayInputStream(buffer.toString().getBytes("iso-8859-1"));
            List<Record> records = personParser.parse(chunk, "iso-8859-1");
            ListHashMap<PersonEntity, PersonDataRecord> recordMap = new ListHashMap<>();
            Session session = sessionManager.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();


            HashMap<Integer, PersonEntity> entityCache = new HashMap<>();

            for (Record record : records) {
                if (record instanceof PersonDataRecord) {
                    PersonDataRecord personDataRecord = (PersonDataRecord) record;
                    int cprNumber = personDataRecord.getCprNumber();

                    PersonEntity entity = entityCache.get(cprNumber);
                    if (entity == null) {
                        entity = queryManager.getItem(session, PersonEntity.class, Collections.singletonMap("cprNumber", cprNumber));
                        if (entity == null) {
                            entity = new PersonEntity(PersonEntity.generateUUID(cprNumber), CprPlugin.getDomain());
                            entity.setCprNumber(cprNumber);
                        }
                        entityCache.put(cprNumber, entity);
                    }
                    recordMap.add(entity, personDataRecord);
                }
            }

            for (PersonEntity entity : recordMap.keySet()) {
                ArrayList<PersonRegistration> entityRegistrations = new ArrayList<>();
                ListHashMap<OffsetDateTime, PersonDataRecord> ajourRecords = new ListHashMap<>();
                TreeSet<OffsetDateTime> sortedTimestamps = new TreeSet<>();
                List<PersonDataRecord> recordList = recordMap.get(entity);

                for (PersonDataRecord record : recordList) {
                    System.out.println("record: " + record);
                    Set<OffsetDateTime> timestamps = record.getRegistrationTimestamps();
                    for (OffsetDateTime timestamp : timestamps) {
                        if (timestamp != null) {
                            ajourRecords.add(timestamp, record);
                            sortedTimestamps.add(timestamp);
                        }
                    }
                }

                // Create one Registration per unique timestamp
                PersonRegistration lastRegistration = null;
                for (OffsetDateTime registrationFrom : sortedTimestamps) {
                    System.out.println("registrationFrom: " + registrationFrom);

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
                        System.out.println("created new registration at " + registrationFrom);
                    }
                    registration.setEntity(entity);
                    entity.addRegistration(registration);

                    // Each record sets its own basedata
                    HashMap<PersonEffect, PersonBaseData> data = new HashMap<>();
                    for (PersonDataRecord record : ajourRecords.get(registrationFrom)) {
                        // Take what we need from the record and put it into dataitems
                        Set<PersonEffect> effects = record.getEffects();
                        for (PersonEffect effect : effects) {

                            PersonEffect realEffect = registration.getEffect(effect.getEffectFrom(), effect.isUncertainFrom(), effect.getEffectTo(), effect.isUncertainTo());
                            if (realEffect != null) {
                                effect = realEffect;
                            } else {
                                effect.setRegistration(registration);
                            }

                            if (effect.getDataItems().isEmpty()) {
                                PersonBaseData baseData = new PersonBaseData();
                                baseData.addEffect(effect);
                            }
                            for (PersonBaseData baseData : effect.getDataItems()) {
                                // There really should be only one item for each effect right now
                                record.populateBaseData(baseData, effect, registrationFrom, this.queryManager, session);
                            }
                        }

                    /*record.populateBaseData(data, timestamp);
                    for (PersonEffect effect : data.keySet()) {
                        PersonBaseData dataItem = data.get(effect);
                        effect.setRegistration(registration);
                        dataItem.addEffect(effect);*/
                        //PersonEffect effect = registration.getEffect(effectFrom, effectTo);
                        /*if (effect == null) {
                            effect = new PersonEffect(registration, effectFrom, effectTo);
                        }
                        data.addEffect(effect);*/


                        //}
                    }

                    if (lastRegistration != null) {
                        lastRegistration.setRegistrationTo(registrationFrom);
                    }
                    lastRegistration = registration;
                    entityRegistrations.add(registration);

                }
                try {
                    System.out.println("registrations: " + objectMapper.writeValueAsString(entityRegistrations));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                for (PersonRegistration registration : entityRegistrations) {
                    try {
                        queryManager.saveRegistration(session, entity, registration);
                    } catch (DataFordelerException e) {
                        e.printStackTrace();
                    }
                }
                allRegistrations.addAll(entityRegistrations);
            }
            transaction.commit();
            session.close();
        }
        return allRegistrations;
    }



    @Override
    protected RegistrationReference createRegistrationReference(URI uri) {
        return new PersonRegistrationReference(uri);
    }

}

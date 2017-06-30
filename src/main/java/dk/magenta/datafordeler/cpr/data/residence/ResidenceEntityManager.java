package dk.magenta.datafordeler.cpr.data.residence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.RegistrationReference;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.fapi.FapiService;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.data.CprEntityManager;
import dk.magenta.datafordeler.cpr.data.person.PersonRegistrationReference;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.data.residence.data.ResidenceBaseData;
import dk.magenta.datafordeler.cpr.parsers.RoadParser;
import dk.magenta.datafordeler.cpr.records.Record;
import dk.magenta.datafordeler.cpr.records.person.PersonDataRecord;
import dk.magenta.datafordeler.cpr.records.residence.ResidenceRecord;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Created by lars on 16-05-17.
 */
@Component
public class ResidenceEntityManager extends CprEntityManager {

    @Autowired
    private ResidenceEntityService residenceEntityService;

    @Autowired
    private RoadParser roadParser;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private QueryManager queryManager;

    @Autowired
    private ObjectMapper objectMapper;

    public ResidenceEntityManager() {
        this.managedEntityClass = ResidenceEntity.class;
        this.managedEntityReferenceClass = ResidenceEntityReference.class;
        this.managedRegistrationClass = ResidenceRegistration.class;
        this.managedRegistrationReferenceClass = PersonRegistrationReference.class;
    }

    @Override
    protected String getBaseName() {
        return "residence";
    }

    @Override
    public FapiService getEntityService() {
        return this.residenceEntityService;
    }

    @Override
    public String getSchema() {
        return ResidenceEntity.schema;
    }

    @Override
    public List<ResidenceRegistration> parseRegistration(String registrationData) throws IOException, ParseException {
        return this.parseRegistration(new ByteArrayInputStream(registrationData.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public List<ResidenceRegistration> parseRegistration(InputStream registrationData) throws ParseException, IOException {
        ArrayList<ResidenceRegistration> registrations = new ArrayList<>();
        List<Record> records = roadParser.parse(registrationData, "utf-8");
        ListHashMap<ResidenceEntity, ResidenceRecord> recordMap = new ListHashMap<>();
        Session session = sessionManager.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        TreeSet<OffsetDateTime> sortedTimestamps = new TreeSet<>();

        for (Record record : records) {
            if (record instanceof ResidenceRecord) {
                ResidenceRecord residenceRecord = (ResidenceRecord) record;

                //ResidenceEntity entity = queryManager.getItem(session, ResidenceEntity.class, );
                //if (entity == null) {
                ResidenceEntity entity = new ResidenceEntity(UUID.randomUUID(), "cpr");
                //}

                recordMap.add(entity, residenceRecord);
            }
        }

        for (ResidenceEntity entity : recordMap.keySet()) {
            ListHashMap<OffsetDateTime, ResidenceRecord> ajourRecords = new ListHashMap<>();
            List<ResidenceRecord> recordList = recordMap.get(entity);

            for (ResidenceRecord record : recordList) {
                System.out.println("record: "+record);
                Set<OffsetDateTime> timestamps = record.getRegistrationTimestamps();
                for (OffsetDateTime timestamp : timestamps) {
                    if (timestamp != null) {
                        ajourRecords.add(timestamp, record);
                        sortedTimestamps.add(timestamp);
                    }
                }
            }

            // Create one Registration per unique timestamp
            ResidenceRegistration lastRegistration = null;
            for (OffsetDateTime registrationFrom : sortedTimestamps) {
                System.out.println("registrationFrom: "+registrationFrom);

                ResidenceRegistration registration = entity.getRegistration(registrationFrom);
                if (registration == null) {
                    if (lastRegistration == null) {
                        registration = new ResidenceRegistration();
                    } else {
                        //registration = this.cloneRegistration(lastRegistration);
                        registration = new ResidenceRegistration();
                        for (ResidenceEffect originalEffect : lastRegistration.getEffects()) {
                            ResidenceEffect newEffect = new ResidenceEffect(registration, originalEffect.getEffectFrom(), originalEffect.getEffectTo());
                            for (ResidenceBaseData originalData : originalEffect.getDataItems()) {
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
                HashMap<ResidenceEffect, PersonBaseData> data = new HashMap<>();
                for (ResidenceRecord record : ajourRecords.get(registrationFrom)) {
                    // Take what we need from the record and put it into dataitems
                    Set<ResidenceEffect> effects = record.getEffects();
                    for (ResidenceEffect effect : effects) {

                        ResidenceEffect realEffect = registration.getEffect(effect.getEffectFrom(), effect.isUncertainFrom(), effect.getEffectTo(), effect.isUncertainTo());
                        if (realEffect != null) {
                            effect = realEffect;
                        } else {
                            effect.setRegistration(registration);
                        }

                        if (effect.getDataItems().isEmpty()) {
                            ResidenceBaseData baseData = new ResidenceBaseData();
                            baseData.addEffect(effect);
                        }
                        for (ResidenceBaseData baseData : effect.getDataItems()) {
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
                registrations.add(registration);

            }
            System.out.println(registrations);
            try {
                System.out.println("registrations: "+objectMapper.writeValueAsString(registrations));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            for (ResidenceRegistration registration : registrations) {
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

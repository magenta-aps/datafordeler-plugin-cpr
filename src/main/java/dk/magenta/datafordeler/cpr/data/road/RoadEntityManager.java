package dk.magenta.datafordeler.cpr.data.road;

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
import dk.magenta.datafordeler.cpr.data.road.data.RoadBaseData;
import dk.magenta.datafordeler.cpr.parsers.CprParser;
import dk.magenta.datafordeler.cpr.parsers.RoadParser;
import dk.magenta.datafordeler.cpr.records.Record;
import dk.magenta.datafordeler.cpr.records.road.RoadDataRecord;
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
public class RoadEntityManager extends CprEntityManager {

    @Autowired
    private RoadEntityService roadEntityService;

    @Autowired
    private RoadParser roadParser;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private QueryManager queryManager;

    @Autowired
    private ObjectMapper objectMapper;

    private static class RoadIdentifier {
        public int municipalityCode;
        public int roadCode;

        public RoadIdentifier(int municipalityCode, int roadCode) {
            this.municipalityCode = municipalityCode;
            this.roadCode = roadCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RoadIdentifier that = (RoadIdentifier) o;

            if (municipalityCode != that.municipalityCode) return false;
            return roadCode == that.roadCode;
        }

        @Override
        public int hashCode() {
            int result = municipalityCode;
            result = 31 * result + roadCode;
            return result;
        }
    }

    public RoadEntityManager() {
        this.managedEntityClass = RoadEntity.class;
        this.managedEntityReferenceClass = RoadEntityReference.class;
        this.managedRegistrationClass = RoadRegistration.class;
        this.managedRegistrationReferenceClass = RoadRegistrationReference.class;
    }

    @Override
    protected String getBaseName() {
        return "road";
    }

    @Override
    public FapiService getEntityService() {
        return this.roadEntityService;
    }

    @Override
    public String getSchema() {
        return RoadEntity.schema;
    }

    @Override
    public List<RoadRegistration> parseRegistration(String registrationData) throws IOException, ParseException {
        return this.parseRegistration(new ByteArrayInputStream(registrationData.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public List<RoadRegistration> parseRegistration(InputStream registrationData) throws ParseException, IOException {
        ArrayList<RoadRegistration> registrations = new ArrayList<>();
        List<Record> records = roadParser.parse(registrationData, "utf-8");
        ListHashMap<RoadEntity, RoadDataRecord> recordMap = new ListHashMap<>();
        Session session = sessionManager.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        for (Record record : records) {
            if (record instanceof RoadDataRecord) {
                RoadDataRecord roadDataRecord = (RoadDataRecord) record;
                HashMap<String, Object> lookup = new HashMap<>();
                lookup.put("municipalityCode", roadDataRecord.getMunicipalityCode());
                lookup.put("roadCode", roadDataRecord.getRoadCode());
                RoadEntity entity = queryManager.getItem(session, RoadEntity.class, lookup);
                if (entity == null) {
                    entity = new RoadEntity(UUID.randomUUID(), "test");
                    entity.setMunicipalityCode(roadDataRecord.getMunicipalityCode());
                    entity.setRoadCode(roadDataRecord.getRoadCode());
                }
                recordMap.add(entity, roadDataRecord);
            }
        }
        System.out.println("recordMap: "+recordMap);

        for (RoadEntity entity : recordMap.keySet()) {
            ListHashMap<String, RoadDataRecord> ajourRecords = new ListHashMap<>();
            List<RoadDataRecord> recordList = recordMap.get(entity);

            TreeSet<String> sortedTimestamps = new TreeSet<>();
            for (RoadDataRecord record : recordList) {
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
            RoadRegistration lastRegistration = null;
            for (String timestamp : sortedTimestamps) {
                OffsetDateTime registrationFrom = CprParser.parseTimestamp(timestamp);
                System.out.println("timestamp: "+timestamp);
                System.out.println("registrationFrom: "+registrationFrom);

                RoadRegistration registration = entity.getRegistration(registrationFrom);
                if (registration == null) {
                    if (lastRegistration == null) {
                        registration = new RoadRegistration();
                    } else {
                        //registration = this.cloneRegistration(lastRegistration);
                        registration = new RoadRegistration();
                        for (RoadEffect originalEffect : lastRegistration.getEffects()) {
                            RoadEffect newEffect = new RoadEffect(registration, originalEffect.getEffectFrom(), originalEffect.getEffectTo());
                            for (RoadBaseData originalData : originalEffect.getDataItems()) {
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
                for (RoadDataRecord record : ajourRecords.get(timestamp)) {
                    // Take what we need from the record and put it into dataitems
                    ListHashMap<RoadEffect, RoadBaseData> dataItems = record.getDataEffects(timestamp);
                    if (dataItems != null) {
                        for (RoadEffect effect : dataItems.keySet()) {
                            for (RoadBaseData data : dataItems.get(effect)) {
                                effect.setRegistration(registration);
                                data.addEffect(effect);
                                //RoadEffect effect = registration.getEffect(effectFrom, effectTo);
                                /*if (effect == null) {
                                    effect = new RoadEffect(registration, effectFrom, effectTo);
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
            for (RoadRegistration registration : registrations) {
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
        return new RoadRegistrationReference(uri);
    }

}

package dk.magenta.datafordeler.cpr.data.road;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.RegistrationReference;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.fapi.FapiService;
import dk.magenta.datafordeler.core.util.DoubleHashMap;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.CprPlugin;
import dk.magenta.datafordeler.cpr.data.CprEntityManager;
import dk.magenta.datafordeler.cpr.data.road.data.RoadBaseData;
import dk.magenta.datafordeler.cpr.parsers.RoadParser;
import dk.magenta.datafordeler.cpr.records.Record;
import dk.magenta.datafordeler.cpr.records.road.RoadDataRecord;
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
        ArrayList<RoadRegistration> allRegistrations = new ArrayList<>();
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

            List<Record> records = roadParser.parse(chunk, "iso-8859-1");
            ListHashMap<RoadEntity, RoadDataRecord> recordMap = new ListHashMap<>();
            Session session = sessionManager.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();

            DoubleHashMap<Integer, Integer, RoadEntity> entityCache = new DoubleHashMap<>();

            for (Record record : records) {
                if (record instanceof RoadDataRecord) {
                    RoadDataRecord roadDataRecord = (RoadDataRecord) record;
                    HashMap<String, Object> lookup = new HashMap<>();
                    int municipalityCode = roadDataRecord.getMunicipalityCode();
                    int roadCode = roadDataRecord.getRoadCode();
                    lookup.put("municipalityCode", municipalityCode);
                    lookup.put("roadCode", roadCode);
                    RoadEntity entity = entityCache.get(municipalityCode, roadCode);
                    if (entity == null) {
                        entity = queryManager.getItem(session, RoadEntity.class, lookup);
                        if (entity == null) {
                            entity = new RoadEntity(RoadEntity.generateUUID(municipalityCode, roadCode), CprPlugin.getDomain());
                            entity.setMunicipalityCode(municipalityCode);
                            entity.setRoadCode(roadCode);
                        }
                        entityCache.put(municipalityCode, roadCode, entity);
                    }
                    recordMap.add(entity, roadDataRecord);
                }
            }

            for (RoadEntity entity : recordMap.keySet()) {
                ArrayList<RoadRegistration> entityRegistrations = new ArrayList<>();
                ListHashMap<OffsetDateTime, RoadDataRecord> ajourRecords = new ListHashMap<>();
                TreeSet<OffsetDateTime> sortedTimestamps = new TreeSet<>();
                List<RoadDataRecord> recordList = recordMap.get(entity);

                for (RoadDataRecord record : recordList) {
                    Set<OffsetDateTime> timestamps = record.getRegistrationTimestamps();
                    for (OffsetDateTime timestamp : timestamps) {
                        if (timestamp != null) {
                            ajourRecords.add(timestamp, record);
                            sortedTimestamps.add(timestamp);
                        }
                    }
                }

                // Create one Registration per unique timestamp
                RoadRegistration lastRegistration = null;
                for (OffsetDateTime registrationFrom : sortedTimestamps) {

                    RoadRegistration registration = entity.getRegistration(registrationFrom);
                    if (registration == null) {
                        if (lastRegistration == null) {
                            registration = new RoadRegistration();
                        } else {
                            //registration = this.cloneRegistration(lastRegistration);
                            registration = new RoadRegistration();
                            for (RoadEffect originalEffect : lastRegistration.getEffects()) {
                                RoadEffect copyEffect = new RoadEffect(registration, originalEffect.getEffectFrom(), originalEffect.getEffectTo());
                                copyEffect.setUncertainFrom(originalEffect.isUncertainFrom());
                                copyEffect.setUncertainTo(originalEffect.isUncertainTo());
                                for (RoadBaseData originalData : originalEffect.getDataItems()) {
                                    originalData.addEffect(copyEffect);
                                }
                            }
                        }
                        registration.setRegistrationFrom(registrationFrom);
                    }
                    registration.setEntity(entity);
                    entity.addRegistration(registration);


                    // Each record sets its own basedata
                    for (RoadDataRecord record : ajourRecords.get(registrationFrom)) {
                        // Take what we need from the record and put it into dataitems
                        Set<RoadEffect> effects = record.getEffects();
                        for (RoadEffect effect : effects) {

                            RoadEffect realEffect = registration.getEffect(effect.getEffectFrom(), effect.isUncertainFrom(), effect.getEffectTo(), effect.isUncertainTo());
                            if (realEffect != null) {
                                effect = realEffect;
                            } else {
                                effect.setRegistration(registration);
                            }

                            //if (effect.getDataItems().isEmpty()) {
                            RoadBaseData baseData = new RoadBaseData();
                            baseData.addEffect(effect);
                            //}
                            //for (RoadBaseData baseData : effect.getDataItems()) {
                            // There really should be only one item for each effect right now
                            record.populateBaseData(baseData, effect, registrationFrom, this.queryManager, session);
                            //}
                        }
                    }


                    if (lastRegistration != null) {
                        lastRegistration.setRegistrationTo(registrationFrom);
                    }
                    lastRegistration = registration;
                    entityRegistrations.add(registration);

                }

                for (RoadRegistration registration : entityRegistrations) {
                    try {
                        queryManager.saveRegistration(session, entity, registration);
                    } catch (DataFordelerException e) {
                        e.printStackTrace();
                    } catch (javax.persistence.EntityNotFoundException e) {
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
        return new RoadRegistrationReference(uri);
    }

}

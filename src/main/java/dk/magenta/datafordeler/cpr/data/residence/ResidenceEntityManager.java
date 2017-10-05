package dk.magenta.datafordeler.cpr.data.residence;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.RegistrationReference;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.cpr.data.CprEntityManager;
import dk.magenta.datafordeler.cpr.data.person.PersonRegistrationReference;
import dk.magenta.datafordeler.cpr.data.residence.data.ResidenceBaseData;
import dk.magenta.datafordeler.cpr.parsers.CprSubParser;
import dk.magenta.datafordeler.cpr.parsers.ResidenceParser;
import dk.magenta.datafordeler.cpr.records.residence.ResidenceRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * Created by lars on 16-05-17.
 */
@Component
public class ResidenceEntityManager extends CprEntityManager<ResidenceRecord, ResidenceEntity, ResidenceRegistration, ResidenceEffect, ResidenceBaseData> {

    @Autowired
    private ResidenceEntityService residenceEntityService;

    @Autowired
    private ResidenceParser residenceParser;

    @Autowired
    private SessionManager sessionManager;

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
    public ResidenceEntityService getEntityService() {
        return this.residenceEntityService;
    }

    @Override
    public String getSchema() {
        return ResidenceEntity.schema;
    }

    @Override
    public List<ResidenceRegistration> parseRegistration(String registrationData, ImportMetadata importMetadata) throws DataFordelerException {
        return this.parseRegistration(new ByteArrayInputStream(registrationData.getBytes(StandardCharsets.UTF_8)), importMetadata);
    }
/*
    @Override
    public List<ResidenceRegistration> parseRegistration(InputStream registrationData) throws ParseException, IOException {
        ArrayList<ResidenceRegistration> allRegistrations = new ArrayList<>();
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
            ListHashMap<ResidenceEntity, ResidenceRecord> recordMap = new ListHashMap<>();
            Session session = sessionManager.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();

            for (Record record : records) {
                if (record instanceof ResidenceRecord) {
                    ResidenceRecord residenceRecord = (ResidenceRecord) record;

                    //ResidenceEntity entity = queryManager.getItem(session, ResidenceEntity.class, );
                    //if (entity == null) {
                    ResidenceEntity entity = new ResidenceEntity(UUID.randomUUID(), CprPlugin.getDomain());
                    //}

                    recordMap.add(entity, residenceRecord);
                }
            }

            for (ResidenceEntity entity : recordMap.keySet()) {
                ArrayList<ResidenceRegistration> entityRegistrations = new ArrayList<>();
                ListHashMap<OffsetDateTime, ResidenceRecord> ajourRecords = new ListHashMap<>();
                TreeSet<OffsetDateTime> sortedTimestamps = new TreeSet<>();
                List<ResidenceRecord> recordList = recordMap.get(entity);


                for (ResidenceRecord record : recordList) {
                    // System.out.println("record: " + record);
                    Set<OffsetDateTime> timestamps = record.getRegistrationTimestamps();
                    for (OffsetDateTime timestamp : timestamps) {
                        if (timestamp != null) {
                            // System.out.println("Adding "+record+" to ajourrecords at "+timestamp);
                            ajourRecords.add(timestamp, record);
                            sortedTimestamps.add(timestamp);
                        }
                    }
                }
                // System.out.println("ajourRecords: "+ajourRecords);
                // System.out.println("sortedTimestamps: "+sortedTimestamps);

                // Create one Registration per unique timestamp
                ResidenceRegistration lastRegistration = null;
                for (OffsetDateTime registrationFrom : sortedTimestamps) {

                    ResidenceRegistration registration = entity.getRegistration(registrationFrom);
                    if (registration == null) {
                        if (lastRegistration == null) {
                            registration = new ResidenceRegistration();
                        } else {
                            //registrering = this.cloneRegistration(lastRegistration);
                            registration = new ResidenceRegistration();
                            for (ResidenceEffect originalEffect : lastRegistration.getVirkninger()) {
                                ResidenceEffect newEffect = new ResidenceEffect(registration, originalEffect.getVirkningFra(), originalEffect.getVirkningTil());
                                for (ResidenceBaseData originalData : originalEffect.getDataItems()) {
                                    originalData.addVirkning(newEffect);
                                }
                            }
                        }
                        registration.setRegistreringFra(registrationFrom);
                        // System.out.println("created new registrering at " + registreringFra);
                    }
                    registration.setEntity(entity);
                    entity.addRegistration(registration);

                    // Each record sets its own basedata
                    // System.out.println("ajourRecords: "+ajourRecords);
                    // System.out.println("registreringFra: "+registreringFra);
                    for (ResidenceRecord record : ajourRecords.get(registrationFrom)) {
                        // Take what we need from the record and put it into dataitems
                        Set<ResidenceEffect> effects = record.getEffects();
                        for (ResidenceEffect effect : effects) {

                            ResidenceEffect realEffect = registration.getEffect(effect.getVirkningFra(), effect.getEffectFromUncertain(), effect.getVirkningTil(), effect.getEffectToUncertain());
                            if (realEffect != null) {
                                effect = realEffect;
                            } else {
                                effect.setRegistrering(registration);
                            }

                            if (effect.getDataItems().isEmpty()) {
                                ResidenceBaseData baseData = new ResidenceBaseData();
                                baseData.addVirkning(effect);
                            }
                            for (ResidenceBaseData baseData : effect.getDataItems()) {
                                // There really should be only one item for each effect right now
                                record.populateBaseData(baseData, effect, registrationFrom, this.queryManager, session);
                            }
                        }

                    record.populateBaseData(data, timestamp);
                    for (PersonEffect effect : data.keySet()) {
                        PersonBaseData dataItem = data.get(effect);
                        effect.setRegistration(registration);
                        dataItem.addEffect(effect);
                        //PersonEffect effect = registration.getEffect(effectFrom, effectTo);
                        if (effect == null) {
                            effect = new PersonEffect(registration, effectFrom, effectTo);
                        }
                        data.addEffect(effect);


                        //}
                    }

                    if (lastRegistration != null) {
                        lastRegistration.setRegistreringTil(registrationFrom);
                    }
                    lastRegistration = registration;
                    entityRegistrations.add(registration);

                }
                for (ResidenceRegistration registration : entityRegistrations) {
                    try {
                        queryManager.saveRegistrering(session, entity, registration);
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


*/
    @Override
    protected RegistrationReference createRegistrationReference(URI uri) {
        return new PersonRegistrationReference(uri);
    }

    @Override
    protected SessionManager getSessionManager() {
        return this.sessionManager;
    }

    @Override
    protected CprSubParser<ResidenceRecord> getParser() {
        return this.residenceParser;
    }

    @Override
    protected Class<ResidenceEntity> getEntityClass() {
        return ResidenceEntity.class;
    }

    @Override
    protected UUID generateUUID(ResidenceRecord record) {
        return ResidenceEntity.generateUUID(record.getMunicipalityCode(), record.getRoadCode(), record.getHouseNumber(), record.getFloor(), record.getDoor());
    }

    @Override
    protected ResidenceEntity createBasicEntity(ResidenceRecord record) {
        return new ResidenceEntity();
    }

    @Override
    protected ResidenceBaseData createDataItem() {
        return new ResidenceBaseData();
    }

}

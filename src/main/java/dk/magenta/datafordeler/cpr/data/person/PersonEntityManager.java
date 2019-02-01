package dk.magenta.datafordeler.cpr.data.person;

import com.fasterxml.jackson.core.JsonProcessingException;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.Registration;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.core.io.Receipt;
import dk.magenta.datafordeler.cpr.data.CprRecordEntityManager;
import dk.magenta.datafordeler.cpr.parsers.CprSubParser;
import dk.magenta.datafordeler.cpr.parsers.PersonParser;
import dk.magenta.datafordeler.cpr.records.CprBitemporalRecord;
import dk.magenta.datafordeler.cpr.records.person.AddressRecord;
import dk.magenta.datafordeler.cpr.records.person.CprBitemporalPersonRecord;
import dk.magenta.datafordeler.cpr.records.person.ForeignAddressRecord;
import dk.magenta.datafordeler.cpr.records.person.PersonDataRecord;
import dk.magenta.datafordeler.cpr.records.service.PersonEntityRecordService;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.*;

@Component
public class PersonEntityManager extends CprRecordEntityManager<PersonDataRecord, PersonEntity> {

    @Value("${dafo.cpr.person.subscription-enabled:false}")
    private boolean setupSubscriptionEnabled;

    @Value("${dafo.cpr.person.local-subscription-folder:cache}")
    private String localSubscriptionFolder;

    @Value("${dafo.cpr.person.jobid:0}")
    private int jobId;

    @Value("${dafo.cpr.person.customer-id:0}")
    private int customerId;


    @Autowired
    private PersonEntityRecordService personEntityService;

    @Autowired
    private PersonParser personParser;

    @Autowired
    private SessionManager sessionManager;

    private static PersonEntityManager instance;

    public PersonEntityManager() {
        this.managedRegistrationReferenceClass = PersonRegistrationReference.class;
        instance = this;
    }

    public int getJobId() {
        return this.jobId;
    }

    public int getCustomerId() {
        return this.customerId;
    }

    public String getLocalSubscriptionFolder() {
        return this.localSubscriptionFolder;
    }

    public boolean isSetupSubscriptionEnabled() {
        return this.setupSubscriptionEnabled;
    }

    @Override
    protected String getBaseName() {
        return "person";
    }

    @Override
    public PersonEntityRecordService getEntityService() {
        return this.personEntityService;
    }

    @Override
    public String getDomain() {
        return "https://data.gl/cpr/person/1/rest/";
    }

    @Override
    public String getSchema() {
        return PersonEntity.schema;
    }

    @Override
    protected URI getReceiptEndpoint(Receipt receipt) {
        return null;
    }

    private HashSet<String> nonGreenlandicCprNumbers = new HashSet<>();

    @Override
    public List<? extends Registration> parseData(InputStream registrationData, ImportMetadata importMetadata) throws DataFordelerException {
        try {
            List<? extends Registration> result = super.parseData(registrationData, importMetadata);
            if (this.isSetupSubscriptionEnabled() && !this.nonGreenlandicCprNumbers.isEmpty() && importMetadata.getImportConfiguration().size() == 0) {
                this.createSubscription(this.nonGreenlandicCprNumbers);
            }
            return result;
        } finally {
            this.nonGreenlandicCprNumbers.clear();
        }
    }

    @Override
    protected void handleRecord(PersonDataRecord record, ImportMetadata importMetadata) {
        super.handleRecord(record, importMetadata);
        if (record != null) {
            if (record instanceof AddressRecord) {
                AddressRecord addressRecord = (AddressRecord) record;
                if (addressRecord.getMunicipalityCode() < 900) {
                    this.nonGreenlandicCprNumbers.add(addressRecord.getCprNumber());
                }
            } else if (record instanceof ForeignAddressRecord) {
                ForeignAddressRecord foreignAddressRecord = (ForeignAddressRecord) record;
                this.nonGreenlandicCprNumbers.add(foreignAddressRecord.getCprNumber());
            }
        }
    }

    @Override
    protected SessionManager getSessionManager() {
        return this.sessionManager;
    }

    @Override
    protected CprSubParser<PersonDataRecord> getParser() {
        return this.personParser;
    }

    @Override
    protected Class<PersonEntity> getEntityClass() {
        return PersonEntity.class;
    }

    @Override
    protected UUID generateUUID(PersonDataRecord record) {
        return PersonEntity.generateUUID(record.getCprNumber());
    }

    @Override
    protected PersonEntity createBasicEntity(PersonDataRecord record) {
        PersonEntity entity = new PersonEntity();
        entity.setPersonnummer(record.getCprNumber());
        return entity;
    }

    private PersonEntity createBasicEntity(String cprNumber) {
        PersonEntity personEntity = new PersonEntity();
        personEntity.setPersonnummer(cprNumber);
        return personEntity;
    }

    private void createSubscription(HashSet<String> addCprNumbers) throws DataFordelerException {
        this.createSubscription(addCprNumbers, new HashSet<>());
    }

    private void createSubscription(HashSet<String> addCprNumbers, HashSet<String> removeCprNumbers) throws DataFordelerException {
        this.log.info("Collected these numbers for subscription: "+addCprNumbers);
        String charset = this.getConfiguration().getRegisterCharset(this);
        String keyConstant = "";
        StringJoiner content = new StringJoiner("\r\n");

        Session session = sessionManager.getSessionFactory().openSession();
        try {
            List<PersonSubscription> existingSubscriptions = QueryManager.getAllItems(session, PersonSubscription.class);
            HashMap<String, PersonSubscription> map = new HashMap<>();
            for (PersonSubscription subscription : existingSubscriptions) {
                map.put(subscription.getPersonNumber(), subscription);
            }

            addCprNumbers.removeAll(removeCprNumbers);
            addCprNumbers.removeAll(map.keySet());


            HashMap<String, HashSet<String>> loop = new HashMap<>();
            loop.put("OP", addCprNumbers);
            loop.put("SL", removeCprNumbers);

            for (String operation : loop.keySet()) {
                HashSet<String> cprNumbers = loop.get(operation);
                for (String cprNumber : cprNumbers) {
                    content.add(
                            String.format(
                                    "%02d%04d%02d%2s%10s%15s%45s",
                                    6,
                                    this.getCustomerId(),
                                    0,
                                    operation,
                                    cprNumber,
                                    keyConstant,
                                    ""
                            )
                    );
                }
            }

            for (String cprNumber : addCprNumbers) {
                content.add(
                        String.format(
                                "%02d%06d%10s%15s",
                                7,
                                this.getJobId(),
                                cprNumber,
                                keyConstant,
                                ""
                        )
                );
            }

            this.addSubscription(content.toString(), charset, this);

            session.beginTransaction();
            try {
                for (String add : addCprNumbers) {
                    PersonSubscription newSubscription = new PersonSubscription();
                    newSubscription.setPersonNumber(add);
                    session.save(newSubscription);
                }
                for (String remove : removeCprNumbers) {
                    PersonSubscription removeSubscription = map.get(remove);
                    if (removeSubscription != null) {
                        session.delete(removeSubscription);
                    }
                }
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
            }
        } finally {
            session.close();
        }
    }


    private HashMap<String, Integer> cnts = new HashMap<>();

    protected void parseAlternate(PersonEntity entity, Collection<PersonDataRecord> records, ImportMetadata importMetadata) {
        OffsetDateTime updateTime = importMetadata.getImportTime();
        int i = 1;
        Integer c = cnts.get(entity.getPersonnummer());
        if (c!=null) {
            i=c;
        }
        for (PersonDataRecord record : records) {
            //System.out.println("-------------------------");
            //System.out.println(record.getLine());
            //System.out.println("cnt: "+i);
            for (CprBitemporalRecord bitemporalRecord : record.getBitemporalRecords()) {
                bitemporalRecord.setDafoUpdated(updateTime);
                bitemporalRecord.setOrigin(record.getOrigin());
                bitemporalRecord.cnt = i;
                bitemporalRecord.line = record.getLine();
                entity.addBitemporalRecord((CprBitemporalPersonRecord) bitemporalRecord, importMetadata.getSession());
            }
            i++;
        }
        cnts.put(entity.getPersonnummer(), i);
        /*try {
            System.out.println("address: "+getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(entity.getAddress()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }*/
    }

    public static String json(Object o) {
        try {
            return instance.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}

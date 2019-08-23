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
import dk.magenta.datafordeler.cpr.records.person.*;
import dk.magenta.datafordeler.cpr.records.person.data.BirthTimeDataRecord;
import dk.magenta.datafordeler.cpr.records.person.data.ChurchVerificationDataRecord;
import dk.magenta.datafordeler.cpr.records.person.data.ParentDataRecord;
import dk.magenta.datafordeler.cpr.records.service.PersonEntityRecordService;
import dk.magenta.datafordeler.cpr.synchronization.SubscribtionTimerTask;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.Calendar;

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

    private Timer subscribtionUploadTimer = new Timer();

    /**
     * Run bean initialization. Make the application upload subscribtions every morning at 6.
     */
    @PostConstruct
    public void init() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date time = calendar.getTime();
        subscribtionUploadTimer.schedule(new SubscribtionTimerTask(this), time, 1000 * 60 * 60 * 24);
    }


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

    private HashSet<String> nonGreenlandicFatherCprNumbers = new HashSet<>();

    /**
     * Parse the file of persons.
     * If the file contains any fathers that is unknown to DAFO add it
     * @param registrationData
     * @param importMetadata
     * @return
     * @throws DataFordelerException
     */
    @Override
    public List<? extends Registration> parseData(InputStream registrationData, ImportMetadata importMetadata) throws DataFordelerException {
        try {
            List<? extends Registration> result = super.parseData(registrationData, importMetadata);
            if (this.isSetupSubscriptionEnabled() && !this.nonGreenlandicCprNumbers.isEmpty() && importMetadata.getImportConfiguration().size() == 0) {
                this.createSubscription(this.nonGreenlandicCprNumbers);
            }
            if (this.isSetupSubscriptionEnabled() && !this.nonGreenlandicFatherCprNumbers.isEmpty() && importMetadata.getImportConfiguration().size() == 0) {
                try(Session session = sessionManager.getSessionFactory().openSession()) {
                    PersonRecordQuery personQuery = new PersonRecordQuery();
                    for(String fatherCpr : nonGreenlandicFatherCprNumbers) {
                        personQuery.addPersonnummer(fatherCpr);
                    }

                    personQuery.applyFilters(session);
                    List<PersonEntity> personEntities = QueryManager.getAllEntities(session, personQuery, PersonEntity.class);

                    for(PersonEntity person : personEntities) {
                        nonGreenlandicFatherCprNumbers.remove(person.getPersonnummer());
                    }
                }
                this.createSubscription(this.nonGreenlandicFatherCprNumbers);
            }
            return result;
        } finally {
            this.nonGreenlandicCprNumbers.clear();
            this.nonGreenlandicFatherCprNumbers.clear();
        }
    }

    /**
     * Handle parsing if records from cpr
     * If a person is leaving Greenland they should be added.
     * If a person is under 18 years old, and has a father with no connection to Greenland they should be added.
     * @param record
     * @param importMetadata
     */
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
            } else if(record instanceof PersonRecord) {

                PersonRecord person = (PersonRecord) record;
                List<CprBitemporalRecord> bitemporalRecords = person.getBitemporalRecords();

                ParentDataRecord father = (ParentDataRecord) bitemporalRecords.stream().
                        filter(bitemporalCprRecord -> bitemporalCprRecord instanceof ParentDataRecord && !((ParentDataRecord) bitemporalCprRecord).isMother()).
                        findAny().orElse(null);

                BirthTimeDataRecord birthTime = (BirthTimeDataRecord) bitemporalRecords.stream().
                        filter(bitemporalCprRecord -> bitemporalCprRecord instanceof BirthTimeDataRecord).
                        findAny().orElse(null);

                if (birthTime != null && father != null && birthTime.getBirthDatetime() != null && birthTime.getBirthDatetime().isAfter(LocalDateTime.now().minusYears(18))) {
                    if (!father.getCprNumber().isEmpty() && !father.getCprNumber().equals("0000000000")) {
                        log.debug("fatherAdd " + father.getCprNumber());
                        nonGreenlandicFatherCprNumbers.add(father.getCprNumber());
                    }
                }
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

    /**
     * Create subscribtions by adding them to the table of subscribtions
     * @param addCprNumbers
     * @param removeCprNumbers
     */
    private void createSubscription(HashSet<String> addCprNumbers, HashSet<String> removeCprNumbers) {
        this.log.info("Collected these numbers for subscription: "+addCprNumbers);

        Session session = sessionManager.getSessionFactory().openSession();
        try {
            List<PersonSubscription> existingSubscriptions = QueryManager.getAllItems(session, PersonSubscription.class);
            HashMap<String, PersonSubscription> map = new HashMap<>();
            for (PersonSubscription subscription : existingSubscriptions) {
                map.put(subscription.getPersonNumber(), subscription);
            }

            addCprNumbers.removeAll(removeCprNumbers);
            addCprNumbers.removeAll(map.keySet());

            session.beginTransaction();
            try {
                for (String add : addCprNumbers) {
                    PersonSubscription newSubscription = new PersonSubscription();
                    newSubscription.setPersonNumber(add);
                    newSubscription.setAssignment(PersonSubscriptionAssignementStatus.CreatedInTable);
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

    /**
     * Create the subscribtion-file from the table of subscribtions, and upload them to FTP-server
     */
    public void createSubscriptionFile() {
        String charset = this.getConfiguration().getRegisterCharset(this);

        Transaction transaction = null;
        try(Session session = sessionManager.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Criteria criteria = session.createCriteria(PersonSubscription.class);
            criteria.add(Restrictions.eq(PersonSubscription.DB_FIELD_CPR_ASSIGNMENT_STATUS, PersonSubscriptionAssignementStatus.CreatedInTable));
            List<PersonSubscription> subscribtionList = criteria.list();
            // If there if no subscribtion to upload just log
            if(subscribtionList.size()==0) {
                log.info("There is found nu subscribtions for upload");
                return;
            }

            for(PersonSubscription subscription : subscribtionList) {
                subscription.setAssignment(PersonSubscriptionAssignementStatus.UploadedToCpr);
            }

            StringJoiner content = new StringJoiner("\r\n");

            for (PersonSubscription subscribtion : subscribtionList) {
                    content.add(
                            String.format(
                                    "%02d%04d%02d%2s%10s%15s%45s",
                                    6,
                                    this.getCustomerId(),
                                    0,
                                    "OP",
                                    subscribtion.getPersonNumber(),
                                    "",
                                    ""
                            )
                    );
            }

            for (PersonSubscription subscribtion : subscribtionList) {
                content.add(
                        String.format(
                                "%02d%06d%10s%15s",
                                7,
                                this.getJobId(),
                                subscribtion.getPersonNumber(),
                                "",
                                ""
                        )
                );
            }
            this.addSubscription(content.toString(), charset, this);
            transaction.commit();

        } catch(Exception e) {
            log.error(e);
            transaction.rollback();
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

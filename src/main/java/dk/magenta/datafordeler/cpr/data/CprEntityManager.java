package dk.magenta.datafordeler.cpr.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.magenta.datafordeler.core.database.*;
import dk.magenta.datafordeler.core.exception.*;
import dk.magenta.datafordeler.core.io.ImportInputStream;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.core.io.Receipt;
import dk.magenta.datafordeler.core.plugin.*;
import dk.magenta.datafordeler.core.util.ItemInputStream;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.core.util.Stopwatch;
import dk.magenta.datafordeler.cpr.CprPlugin;
import dk.magenta.datafordeler.cpr.CprRegisterManager;
import dk.magenta.datafordeler.cpr.configuration.CprConfiguration;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.parsers.CprSubParser;
import dk.magenta.datafordeler.cpr.records.Bitemporality;
import dk.magenta.datafordeler.cpr.records.CprDataRecord;
import dk.magenta.datafordeler.cpr.records.person.AddressRecord;
import dk.magenta.datafordeler.cpr.records.person.HistoricAddressRecord;
import dk.magenta.datafordeler.cpr.records.person.HistoricPersonDataRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.*;
import java.util.*;

@Component
public abstract class CprEntityManager<T extends CprDataRecord, E extends Entity<E, R>, R extends CprRegistration<E, R, V>, V extends CprEffect<R, V, D>, D extends CprData<V, D>> extends EntityManager {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    CprConfigurationManager cprConfigurationManager;

    @Autowired
    Stopwatch timer;

    private static boolean SAVE_RECORD_DATA = false;

    public static final String IMPORTCONFIG_RECORDTYPE = "recordtype";
    public static final String IMPORTCONFIG_PNR = "personnummer";

    private HttpCommunicator commonFetcher;

    protected Logger log = LogManager.getLogger(this.getClass().getSimpleName());

    private Collection<String> handledURISubstrings;

    protected abstract String getBaseName();

    public CprEntityManager() {
        this.commonFetcher = new HttpCommunicator();
        this.handledURISubstrings = new ArrayList<>();
    }

    @Override
    public void setRegisterManager(RegisterManager registerManager) {
        super.setRegisterManager(registerManager);
        //this.handledURISubstrings.add(expandBaseURI(this.getBaseEndpoint(), "/" + this.getBaseName(), null, null).toString());
        //this.handledURISubstrings.add(expandBaseURI(this.getBaseEndpoint(), "/get/" + this.getBaseName(), null, null).toString());
    }

    @Override
    public Collection<String> getHandledURISubstrings() {
        return this.handledURISubstrings;
    }

    @Override
    protected ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    @Override
    protected Communicator getRegistrationFetcher() {
        return this.commonFetcher;
    }

    @Override
    protected Communicator getReceiptSender() {
        return this.commonFetcher;
    }

    @Override
    public URI getBaseEndpoint() {
        return this.getRegisterManager().getBaseEndpoint();
    }

    @Override
    protected URI getReceiptEndpoint(Receipt receipt) {
        return null;
    }

    @Override
    public RegistrationReference parseReference(InputStream referenceData) throws IOException {
        return this.getObjectMapper().readValue(referenceData, this.managedRegistrationReferenceClass);
    }

    @Override
    public RegistrationReference parseReference(String referenceData, String charsetName) throws IOException {
        return this.getObjectMapper().readValue(referenceData.getBytes(charsetName), this.managedRegistrationReferenceClass);
    }

    protected abstract RegistrationReference createRegistrationReference(URI uri);

    @Override
    public RegistrationReference parseReference(URI uri) {
        return this.createRegistrationReference(uri);
    }

    @Override
    public URI getRegistrationInterface(RegistrationReference reference) throws WrongSubclassException {
        if (!this.managedRegistrationReferenceClass.isInstance(reference)) {
            throw new WrongSubclassException(this.managedRegistrationReferenceClass, reference);
        }
        if (reference.getURI() != null) {
            return reference.getURI();
        }
        return EntityManager.expandBaseURI(this.getBaseEndpoint(), "/get/"+this.getBaseName()+"/"+reference.getChecksum());
    }

    @Override
    protected URI getListChecksumInterface(OffsetDateTime fromDate) {
        return this.getRegisterManager().getListChecksumInterface(this.getSchema(), fromDate);
    }

    @Override
    protected ItemInputStream<? extends EntityReference> parseChecksumResponse(InputStream responseContent) throws DataFordelerException {
        return ItemInputStream.parseJsonStream(responseContent, this.managedEntityReferenceClass, "items", this.getObjectMapper());
    }

    @Override
    protected Logger getLog() {
        return this.log;
    }

    protected CprConfiguration getConfiguration() {
        return this.cprConfigurationManager.getConfiguration();
    }

    public CprRegisterManager getRegisterManager() {
        return (CprRegisterManager) super.getRegisterManager();
    }


    protected abstract SessionManager getSessionManager();
    protected abstract CprSubParser<T> getParser();
    protected abstract Class<E> getEntityClass();
    protected abstract UUID generateUUID(T record);
    protected abstract E createBasicEntity(T record);
    protected abstract D createDataItem();


    private static final String TASK_PARSE = "CprParse";
    private static final String TASK_FIND_ENTITY = "CprFindEntity";
    private static final String TASK_FIND_REGISTRATIONS = "CprFindRegistrations";
    private static final String TASK_FIND_ITEMS = "CprFindItems";
    private static final String TASK_POPULATE_DATA = "CprPopulateData";
    private static final String TASK_SAVE = "CprSave";
    private static final String TASK_CHUNK_HANDLE = "CprChunk";

    @Override
    public List<R> parseData(InputStream registrationData, ImportMetadata importMetadata) throws DataFordelerException {
        String charset = this.getConfiguration().getRegisterCharset(this);
        BufferedReader reader = new BufferedReader(new InputStreamReader(registrationData, Charset.forName(charset)));
        CprSubParser<T> parser = this.getParser();
        Session session = importMetadata.getSession();
        boolean wrappedInTransaction = importMetadata.isTransactionInProgress();
        log.info("Parsing in thread "+Thread.currentThread().getId());

        int maxChunkSize = 1000;
        List<File> cacheFiles = null;
        int totalChunks = 0;
        if (registrationData instanceof ImportInputStream) {
            ImportInputStream importStream = (ImportInputStream) registrationData;
            cacheFiles = importStream.getCacheFiles();
            int lines = importStream.getLineCount();

            // Integer division with rounding up
            //totalChunks = (int) Math.ceil((float) lines / (float) maxChunkSize);
            totalChunks = (lines + maxChunkSize - 1) / maxChunkSize;
            //totalChunks = (lines / maxChunkSize) + (lines % maxChunkSize == 0 ? 0 : 1);
        }

        boolean done = false;
        long chunkCount = 1;
        long startChunk = importMetadata.getStartChunk();
        while (!done) {
            try {

                String line;
                int i = 0;
                int size = 0;
                ArrayList<String> dataChunk = new ArrayList<>();
                try {
                    for (i = 0; (line = reader.readLine()) != null && i < maxChunkSize; i++) {
                        dataChunk.add(line);
                        size += line.length();
                    }
                    if (line == null) {
                        done = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    done = true;
                }

                if (chunkCount >= startChunk) {
                    log.info("Handling chunk " + chunkCount + (totalChunks > 0 ? ("/" + totalChunks) : "") + " (" + size + " chars)");
                    timer.start(TASK_CHUNK_HANDLE);
                    // Parse chunk into a set of records
                    timer.start(TASK_PARSE);
                    List<T> chunkRecords = parser.parse(dataChunk, charset);
                    log.debug("Batch parsed into " + chunkRecords.size() + " records");
                    timer.measure(TASK_PARSE);

                    if (!chunkRecords.isEmpty()) {

                        if (!wrappedInTransaction) {
                            session.beginTransaction();
                            importMetadata.setTransactionInProgress(true);
                        }
                        try {

                            // Find Entities (or create those that are missing), and put them in the recordMap
                            timer.start(TASK_FIND_ENTITY);
                            ListHashMap<E, T> recordMap = new ListHashMap<>();
                            HashMap<UUID, E> entityCache = new HashMap<>();
                            LinkedHashSet<UUID> uuids = new LinkedHashSet<>();
                            for (T record : chunkRecords) {
                                this.checkInterrupt(importMetadata);
                                this.handleRecord(record, importMetadata);
                                if (this.filter(record, importMetadata.getImportConfiguration())) {
                                    UUID uuid = this.generateUUID(record);
                                    uuids.add(uuid);
                                    E entity = entityCache.get(uuid);
                                    if (entity == null) {
                                        Identification identification = QueryManager.getOrCreateIdentification(session, uuid, CprPlugin.getDomain());
                                        entity = QueryManager.getEntity(session, identification, this.getEntityClass());
                                        if (entity == null) {
                                            entity = this.createBasicEntity(record);
                                            entity.setIdentifikation(identification);
                                        }
                                        entityCache.put(uuid, entity);
                                    }
                                    recordMap.add(entity, record);
                                }
                            }
                            log.info("Batch resulted in " + recordMap.keySet().size() + " unique entities");
                            timer.measure(TASK_FIND_ENTITY);


                            for (UUID uuid : uuids) {
                                E entity = entityCache.get(uuid);
                                List<T> records = recordMap.get(entity);

                                ListHashMap<Bitemporality, T> groups = this.sortIntoGroups(records);
                                HashSet<R> entityRegistrations = new HashSet<>();

                                ArrayList<Bitemporality> sortedBitemporalities = new ArrayList<>(groups.keySet());
                                sortedBitemporalities.sort(Bitemporality::compareTo);

                                for (Bitemporality bitemporality :sortedBitemporalities) {
                                    //System.out.println("Bitemporality "+bitemporality.toString());

                                    timer.start(TASK_FIND_REGISTRATIONS);
                                    List<T> groupRecords = groups.get(bitemporality);

                                    ArrayList<String> recordtypes = new ArrayList<>();
                                    for (T rec : groupRecords) {
                                        recordtypes.add(rec.getRecordType());
                                    }

                                    List<R> registrations = entity.findRegistrations(bitemporality.registrationFrom, bitemporality.registrationTo);

                                    ArrayList<V> effects = new ArrayList<>();
                                    for (R registration : registrations) {
                                        this.checkInterrupt(importMetadata);
                                        V effect = registration.getEffect(bitemporality);
                                        if (effect == null) {
                                            effect = registration.createEffect(bitemporality);
                                            //System.out.println("Create new effect "+effect.getRegistration().getRegistrationFrom()+"|"+effect.getRegistration().getRegistrationTo()+"|"+effect.getEffectFrom()+"|"+effect.getEffectTo());
                                        } else {
                                            //System.out.println("Use existing effect "+effect.getRegistration().getRegistrationFrom()+"|"+effect.getRegistration().getRegistrationTo()+"|"+effect.getEffectFrom()+"|"+effect.getEffectTo());
                                        }
                                        //System.out.println("effect: "+effect.getRegistration().getRegistrationFrom()+"|"+effect.getRegistration().getRegistrationTo()+"|"+effect.getEffectFrom()+"|"+effect.getEffectTo());
                                        effects.add(effect);
                                    }
                                    entityRegistrations.addAll(registrations);
                                    timer.measure(TASK_FIND_REGISTRATIONS);


                                    // R-V-D scenario
                                    // Every DataItem that we locate for population must match the given effects exactly,
                                    // or we risk assigning data to an item that shouldn't be assigned to

                                    timer.start(TASK_POPULATE_DATA);
                                    for (V e : effects) {
                                        if (e.getDataItems().isEmpty()) {
                                            D baseData = this.createDataItem();
                                            baseData.addEffect(e);
                                        }
                                        for (D baseData : e.getDataItems()) {
                                            for (T record : groupRecords) {
                                                boolean updated = false;

                                                if (record.populateBaseData(baseData, bitemporality, session, importMetadata)) {
                                                       updated = true;
                                                }
                                                this.checkInterrupt(importMetadata);
                                                if (updated) {
                                                    baseData.setUpdated(importMetadata.getImportTime());
                                                    if (SAVE_RECORD_DATA) {
                                                        RecordData recordData = new RecordData(importMetadata.getImportTime());
                                                        recordData.setSourceData(record.getLine());
                                                        baseData.addRecordData(recordData);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    timer.measure(TASK_POPULATE_DATA);

                                    for (R registration : registrations) {
                                        V effect = registration.getEffect(bitemporality.effectFrom, bitemporality.effectFromUncertain, null, false);
                                        if (effect != null) {
                                            Bitemporality outdatedTemporality = bitemporality.withEffect(effect);
                                            for (T record : groupRecords) {
                                                if (record instanceof HistoricPersonDataRecord) {
                                                    HistoricPersonDataRecord historicRecord = (HistoricPersonDataRecord) record;
                                                    for (D baseData : effect.getDataItems()) {
                                                        PersonBaseData personBaseData = (PersonBaseData) baseData;
                                                        if (historicRecord.cleanBaseData((PersonBaseData) baseData, bitemporality, outdatedTemporality, session)) {
                                                            if (personBaseData.isEmpty()) {
                                                                HashSet<PersonEffect> personEffects = new HashSet<>(personBaseData.getEffects());
                                                                for (PersonEffect personEffect : personEffects) {
                                                                    personBaseData.removeEffect(personEffect);
                                                                }
                                                                session.delete(personBaseData);
                                                                for (PersonEffect personEffect : personEffects) {
                                                                    if (personEffect.getDataItems().isEmpty()) {
                                                                        personEffect.getRegistration().removeEffect(personEffect);
                                                                        session.delete(personEffect);
                                                                    }
                                                                }
                                                            } else {
                                                                baseData.setUpdated(importMetadata.getImportTime());
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }


/*

                                    D baseData = null;
                                    HashSet<D> searchPool = new HashSet<>();
                                    HashMap<D, Integer> counters = new HashMap<>();
                                    for (V effect : effects) {
                                        for (D data : effect.getDataItems()) {
                                            Integer count = counters.get(data);
                                            if (count == null) {
                                                count = 0;
                                            }
                                            count++;
                                            counters.put(data, count);
                                        }
                                    }
                                    int effectsCount = effects.size();
                                    for (D data : counters.keySet()) {
                                        if (counters.get(data) == effectsCount) {
                                            searchPool.add(data);
                                        }
                                    }

                                    System.out.println("searchPool.size: "+searchPool.size());

                                    // Find a basedata that matches our effects perfectly
                                    for (D data : searchPool) {
                                        this.checkInterrupt(importMetadata);
                                        Set<V> existingEffects = data.getEffects();

                                        Hibernate.initialize(existingEffects);


                                        System.out.println("existingEffects");
                                        for (V e : existingEffects) {
                                            System.out.println("    "+System.identityHashCode(e)+" "+e.getRegistration().getRegistrationFrom()+"|"+e.getRegistration().getRegistrationTo()+"|"+e.getEffectFrom()+"|"+e.getEffectTo());
                                        }




                                        System.out.println(existingEffects.containsAll(effects)+" && "+effects.containsAll(existingEffects));
                                        if (existingEffects.containsAll(effects) && effects.containsAll(existingEffects)) {
                                            baseData = data;
                                            System.out.println("Reuse existing basedata "+System.identityHashCode(baseData));
                                            break;
                                        }
                                    }

                                    if (baseData == null) {
                                        baseData = this.createDataItem();
                                        System.out.println("Creating new basedata "+System.identityHashCode(baseData));
                                        for (V effect : effects) {
                                            System.out.println("Wire basedata to effect "+effect.getRegistration().getRegistrationFrom()+"|"+effect.getRegistration().getRegistrationTo()+"|"+effect.getEffectFrom()+"|"+effect.getEffectTo());
                                            baseData.addEffect(effect);
                                        }
                                    }


                                    timer.start(TASK_POPULATE_DATA);
                                    for (T record : groupRecords) {
                                        boolean updated = false;
                                        for (V effect : effects) {
                                            this.checkInterrupt(importMetadata);
                                            if (record.populateBaseData(baseData, effect, bitemporality.registrationFrom, session, importMetadata)) {
                                                updated = true;
                                            }
                                        }
                                        this.checkInterrupt(importMetadata);
                                        if (updated) {
                                            baseData.setUpdated(importMetadata.getImportTime());
                                            if (SAVE_RECORD_DATA) {
                                                RecordData recordData = new RecordData(importMetadata.getImportTime());
                                                recordData.setSourceData(record.getLine());
                                                baseData.addRecordData(recordData);
                                            }
                                        }
                                    }
                                    timer.measure(TASK_POPULATE_DATA);
*/
                                }

                                timer.start(TASK_SAVE);
                                ArrayList<R> registrationList = new ArrayList<>(entityRegistrations);
                                Collections.sort(registrationList);
                                int j = 0;
                                for (R registration : registrationList) {
                                    this.checkInterrupt(importMetadata);
                                    registration.setSequenceNumber(j++);
                                    registration.setLastImportTime(importMetadata.getImportTime());

                                    try {
                                        QueryManager.saveRegistration(session, entity, registration, false, false, false);
                                    } catch (DataFordelerException e) {
                                        e.printStackTrace();
                                    } catch (javax.persistence.EntityNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                                timer.measure(TASK_SAVE);
                            }

                            this.checkInterrupt(importMetadata);


                        } catch (ImportInterruptedException e) {
                            if (!wrappedInTransaction) {
                                session.getTransaction().rollback();
                                importMetadata.setTransactionInProgress(false);
                                session.clear();
                            }
                            e.setChunk(chunkCount);
                            throw e;
                        }

                        session.flush();
                        if (!wrappedInTransaction) {
                            session.getTransaction().commit();
                            importMetadata.setTransactionInProgress(false);
                            session.clear();
                        }
                    }

                    long chunkTime = timer.getTotal(TASK_CHUNK_HANDLE);
                    timer.reset(TASK_CHUNK_HANDLE);
                    if (!chunkRecords.isEmpty()) {
                        log.info(i + " lines => " + chunkRecords.size() + " records handled in " + chunkTime + " ms (" + ((float) chunkTime / (float) chunkRecords.size()) + " ms avg)");
                    }
                    log.info(timer.formatAllTotal());
                }
                chunkCount++;

            } catch (ImportInterruptedException e) {
                log.info("Import aborted in chunk " + chunkCount);
                if (e.getChunk() == null) {
                    log.info("That's before our startPoint, propagate startPoint " + startChunk);
                    e.setChunk(startChunk);
                }
                e.setFiles(cacheFiles);
                e.setEntityManager(this);
                throw e;
            }
        }
        return null;
    }

    protected boolean filter(T record, ObjectNode importConfiguration) {
        return record.filter(importConfiguration);
    }


    public ListHashMap<Bitemporality, T> sortIntoGroups(Collection<T> records) {
        // Sort the records into groups that share bitemporality
        ListHashMap<Bitemporality, T> recordGroups = new ListHashMap<>();
        for (T record : records) {
            // Find the appropriate registration object
            List<Bitemporality> bitemporalities = record.getBitemporality();
            for (Bitemporality bitemporality : bitemporalities) {
                recordGroups.add(bitemporality, record);
            }
        }
        return recordGroups;
    }

    @Override
    public boolean handlesOwnSaves() {
        return true;
    }

    private void checkInterrupt(ImportMetadata importMetadata) throws ImportInterruptedException {
        if (importMetadata.getStop()) {
            throw new ImportInterruptedException(new InterruptedException());
        }
    }

    protected void handleRecord(T record, ImportMetadata importMetadata) {}


    public int getJobId() {
        return 0;
    }

    public int getCustomerId() {
        return 0;
    }

    public String getLocalSubscriptionFolder() {
        return null;
    }

    public boolean isSetupSubscriptionEnabled() {
        return false;
    }

    protected URI getSubscriptionURI() throws DataFordelerException {
        CprConfiguration configuration = this.getConfiguration();
        return configuration.getRegisterSubscriptionURI(this);
    }

    public void addSubscription(String contents, String charset, CprEntityManager entityManager) throws DataFordelerException {
        if (this.getJobId() == 0) {
            throw new ConfigurationException("CPR jobId not set");
        }
        if (this.getCustomerId() == 0) {
            throw new ConfigurationException("CPR customerId not set");
        }
        if (this.getLocalSubscriptionFolder() == null || this.getLocalSubscriptionFolder().isEmpty()) {
            throw new ConfigurationException("CPR localSubscriptionFolder not set");
        }
        // Create file
        LocalDate subscriptionDate = LocalDate.now();
        // If it's after noon, CPR will not process the file today.
        ZonedDateTime dailyDeadline = subscriptionDate.atTime(LocalTime.of(11, 45)).atZone(ZoneId.of("Europe/Copenhagen"));
        if (ZonedDateTime.now().isAfter(dailyDeadline)) {
            subscriptionDate = subscriptionDate.plusDays(1);
        }
        File localSubscriptionFolder = new File(this.getLocalSubscriptionFolder());
        if (localSubscriptionFolder.isFile()) {
            throw new ConfigurationException("CPR localSubscriptionFolder is a file, not a folder");
        }
        if (!localSubscriptionFolder.exists()) {
            localSubscriptionFolder.mkdirs();
        }
        File subscriptionFile = new File(
                localSubscriptionFolder,
                String.format(
                        "d%02d%02d%02d",
                        subscriptionDate.getYear() % 100,
                        subscriptionDate.getMonthValue(),
                        subscriptionDate.getDayOfMonth()
                ) +
                        "." +
                        String.format("i%06d", this.getJobId())

        );
        try {
            if (!subscriptionFile.exists()) {
                subscriptionFile.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(subscriptionFile, true);
            fileOutputStream.write(contents.getBytes(charset));
            fileOutputStream.close();
        } catch (IOException e) {
            throw new DataStreamException(e);
        }

        // Upload file
        URI destination = this.getSubscriptionURI();
        FtpCommunicator ftpSender = this.getRegisterManager().getFtpCommunicator(destination, entityManager);
        try {
            ftpSender.send(destination, subscriptionFile);
        } catch (IOException e) {
            throw new DataStreamException(e);
        }
    }

    /**
     * Should return whether the configuration is set so that pulls are enabled for this entitymanager
     */
    @Override
    public boolean pullEnabled() {
        try {
            return this.getRegisterManager().getEventInterface(this) != null;
        } catch (DataFordelerException e) {
            return false;
        }
    }
}

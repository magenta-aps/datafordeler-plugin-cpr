package dk.magenta.datafordeler.cpr.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.database.*;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.exception.ImportInterruptedException;
import dk.magenta.datafordeler.core.exception.WrongSubclassException;
import dk.magenta.datafordeler.core.io.ImportInputStream;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.core.io.Receipt;
import dk.magenta.datafordeler.core.plugin.Communicator;
import dk.magenta.datafordeler.core.plugin.EntityManager;
import dk.magenta.datafordeler.core.plugin.HttpCommunicator;
import dk.magenta.datafordeler.core.plugin.RegisterManager;
import dk.magenta.datafordeler.core.util.ItemInputStream;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.core.util.Stopwatch;
import dk.magenta.datafordeler.cpr.CprPlugin;
import dk.magenta.datafordeler.cpr.configuration.CprConfiguration;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import dk.magenta.datafordeler.cpr.parsers.CprSubParser;
import dk.magenta.datafordeler.cpr.records.Bitemporality;
import dk.magenta.datafordeler.cpr.records.CprDataRecord;
import dk.magenta.datafordeler.cpr.records.Record;
import dk.magenta.datafordeler.cpr.records.person.NameRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Created by lars on 29-05-17.
 */
@Component
public abstract class CprEntityManager<T extends CprDataRecord, E extends Entity<E, R>, R extends CprRegistration<E, R, V>, V extends CprEffect<R, V, D>, D extends CprData<V, D>> extends EntityManager {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    CprConfigurationManager cprConfigurationManager;

    @Autowired
    Stopwatch timer;

    private static boolean SAVE_RECORD_DATA = false;

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
    public List<R> parseRegistration(InputStream registrationData, ImportMetadata importMetadata) throws DataFordelerException {
        String charset = this.getConfiguration().getRegisterCharset(this);
        BufferedReader reader = new BufferedReader(new InputStreamReader(registrationData, Charset.forName(charset)));
        CprSubParser<T> parser = this.getParser();
        Session session = importMetadata.getSession();
        boolean wrappedInTransaction = importMetadata.isTransactionInProgress();
        log.info("Parsing in thread "+Thread.currentThread().getId());

        List<File> cacheFiles = null;
        if (registrationData instanceof ImportInputStream) {
            cacheFiles = ((ImportInputStream) registrationData).getCacheFiles();
        }


        boolean done = false;
        int maxChunkSize = 1000;
        long chunkCount = 0;
        long startChunk = importMetadata.getStartChunk();
        while (!done) {
            try {

                String line;
                int i = 0;
                ArrayList<String> dataChunk = new ArrayList<>();
                try {
                    for (i = 0; (line = reader.readLine()) != null && i < maxChunkSize; i++) {
                        dataChunk.add(line);
                    }
                    if (line == null) {
                        done = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    done = true;
                }

                if (chunkCount >= startChunk) {
                    log.info("Handling chunk " + chunkCount);
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
                                if (this.filter(record)) {
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

                                for (Bitemporality bitemporality : groups.keySet()) {

                                    timer.start(TASK_FIND_REGISTRATIONS);
                                    List<T> groupRecords = groups.get(bitemporality);
                                    List<R> registrations = entity.findRegistrations(bitemporality.registrationFrom, bitemporality.registrationTo);

                                    ArrayList<V> effects = new ArrayList<>();
                                    for (R registration : registrations) {
                                        this.checkInterrupt(importMetadata);
                                        V effect = registration.getEffect(bitemporality);
                                        if (effect == null) {
                                            log.debug("Create new effect");
                                            effect = registration.createEffect(bitemporality);
                                        } else {
                                            log.debug("Use existing effect");
                                        }
                                        effects.add(effect);
                                    }
                                    entityRegistrations.addAll(registrations);
                                    timer.measure(TASK_FIND_REGISTRATIONS);


                                    timer.start(TASK_FIND_ITEMS);
                                    // R-V-D scenario
                                    // Every DataItem that we locate for population must match the given effects exactly,
                                    // or we risk assigning data to an item that shouldn't be assigned to

                                    D baseData = null;
                                    HashSet<D> searchPool = new HashSet<>();
                                    HashMap<D, Integer> foo = new HashMap<>();
                                    for (V effect : effects) {
                                        for (D data : effect.getDataItems()) {
                                            Integer count = foo.get(data);
                                            if (count == null) {
                                                count = 0;
                                            }
                                            count++;
                                            foo.put(data, count);
                                        }
                                    }
                                    int effectsCount = effects.size();
                                    for (D data : foo.keySet()) {
                                        if (foo.get(data) == effectsCount) {
                                            searchPool.add(data);
                                        }
                                    }

                                    // Find a basedata that matches our effects perfectly
                                    for (D data : searchPool) {
                                        this.checkInterrupt(importMetadata);
                                        Set<V> existingEffects = data.getEffects();

                                        Hibernate.initialize(existingEffects);
                                        if (existingEffects.containsAll(effects) && effects.containsAll(existingEffects)) {
                                            baseData = data;
                                            log.debug("Reuse existing basedata");
                                            break;
                                        }
                                    }

                                    if (baseData == null) {
                                        //log.info("Creating new basedata");
                                        baseData = this.createDataItem();
                                        for (V effect : effects) {
                                            //log.debug("Wire basedata to effect "+effect.getRegistration().getRegistrationFrom()+"|"+effect.getRegistration().getRegistrationTo()+"|"+effect.getEffectFrom()+"|"+effect.getEffectTo());
                                            baseData.addEffect(effect);
                                        }
                                    }
                                    timer.measure(TASK_FIND_ITEMS);


                                    timer.start(TASK_POPULATE_DATA);
                                    for (T record : groupRecords) {
                                        boolean updated = false;
                                        for (V effect : effects) {
                                            this.checkInterrupt(importMetadata);
                                            if (record.populateBaseData(baseData, effect, bitemporality.registrationFrom, session)) {
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

    protected boolean filter(T record) {
        return record.filter();
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
}

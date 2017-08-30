package dk.magenta.datafordeler.cpr.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.database.*;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.exception.DataStreamException;
import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.exception.WrongSubclassException;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
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
        this.handledURISubstrings.add(expandBaseURI(this.getBaseEndpoint(), "/" + this.getBaseName(), null, null).toString());
        this.handledURISubstrings.add(expandBaseURI(this.getBaseEndpoint(), "/get/" + this.getBaseName(), null, null).toString());
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

    private CprConfiguration getConfiguration() {
        return this.cprConfigurationManager.getConfiguration();
    }


    protected abstract SessionManager getSessionManager();
    protected abstract QueryManager getQueryManager();
    protected abstract CprSubParser<T> getParser();
    protected abstract Class<E> getEntityClass();
    protected abstract UUID generateUUID(T record);
    protected abstract E createBasicEntity(T record);
    protected abstract D createDataItem();

    @Override
    public List<R> parseRegistration(String registrationData) throws DataFordelerException {
        return this.parseRegistration(new ByteArrayInputStream(registrationData.getBytes(StandardCharsets.UTF_8)));
    }

    private static final String TASK_PARSE = "CprParse";
    private static final String TASK_FIND_ENTITY = "CprFindEntity";
    private static final String TASK_FIND_REGISTRATIONS = "CprFindRegistrations";
    private static final String TASK_FIND_ITEMS = "CprFindItems";
    private static final String TASK_POPULATE_DATA = "CprPopulateData";
    private static final String TASK_SAVE = "CprSave";
    private static final String TASK_CHUNK_HANDLE = "CprChunk";

    @Override
    public List<R> parseRegistration(InputStream registrationData) throws DataFordelerException {
        ArrayList<R> allRegistrations = new ArrayList<>();
        String charset = this.getConfiguration().getCharset();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(registrationData, charset));
        } catch (UnsupportedEncodingException e) {
            throw new DataStreamException(e);
        }
        CprSubParser<T> parser = this.getParser();
        QueryManager queryManager = this.getQueryManager();

        boolean done = false;
        long linesRead = 0;
        int limit = 1000;
        while (!done) {
            timer.start(TASK_CHUNK_HANDLE);
            Session session = this.getSessionManager().getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();

            timer.start(TASK_PARSE);
            StringJoiner buffer = new StringJoiner("\n");
            int i;
            try {
                for (i = 0; i < limit; i++) {
                String line = null;
                    line = reader.readLine();

                if (line != null) {
                    buffer.add(line);
                    linesRead++;
                } else {
                    done = true;
                }
            }
            } catch (IOException e) {
                throw new DataStreamException(e);
            }
            log.debug("Loaded a total of "+linesRead+" lines");
            log.debug("Processing batch of "+i+" lines");
            InputStream chunk = null;
            try {
                chunk = new ByteArrayInputStream(buffer.toString().getBytes(charset));
            } catch (UnsupportedEncodingException e) {
                throw new DataStreamException(e);
            }

            List<T> chunkRecords = parser.parse(chunk, charset);
            log.debug("Batch parsed into "+chunkRecords.size()+" records");
            timer.measure(TASK_PARSE);

            timer.start(TASK_FIND_ENTITY);
            // Find Entities (or create those that are missing), and put them in the recordMap
            ListHashMap<E, T> recordMap = new ListHashMap<>();
            HashMap<UUID, E> entityCache = new HashMap<>();
            for (T record : chunkRecords) {
                UUID uuid = this.generateUUID(record);
                E entity = entityCache.get(uuid);
                if (entity == null) {
                    entity = queryManager.getEntity(session, uuid, this.getEntityClass());
                    if (entity == null) {
                        entity = this.createBasicEntity(record);
                        entity.setUUID(uuid);
                        entity.setDomain(CprPlugin.getDomain());
                    }
                    entityCache.put(uuid, entity);
                }
                recordMap.add(entity, record);
            }
            log.debug("Batch resulted in "+recordMap.keySet().size()+" unique entities");
            timer.measure(TASK_FIND_ENTITY);

            for (E entity : recordMap.keySet()) {
                List<T> records = recordMap.get(entity);
                Collection<R> entityRegistrations = this.parseRegistration(entity, records, queryManager, session);
                allRegistrations.addAll(entityRegistrations);
            }
            transaction.commit();
            session.close();
            timer.measure(TASK_CHUNK_HANDLE);
            long chunkTime = timer.getTotal(TASK_CHUNK_HANDLE);
            timer.reset(TASK_CHUNK_HANDLE);
            if (!chunkRecords.isEmpty()) {
                log.info(i + " lines => " + chunkRecords.size() + " records handled in " + chunkTime + " ms (" + ((float) chunkTime / (float) chunkRecords.size()) + " ms avg)");
            }
        }

        log.info(timer.formatTotal(TASK_PARSE));
        log.info(timer.formatTotal(TASK_FIND_ENTITY));
        log.info(timer.formatTotal(TASK_FIND_REGISTRATIONS));
        log.info(timer.formatTotal(TASK_FIND_ITEMS));
        log.info(timer.formatTotal(TASK_POPULATE_DATA));
        log.info(timer.formatTotal(TASK_SAVE));
        return allRegistrations;
    }


    private static long tic() {
        return Instant.now().toEpochMilli();
    }

    private static long toc(long s) {
        return tic() - s;
    }


    private Collection<R> parseRegistration(E entity, List<T> records, QueryManager queryManager, Session session) {

        HashSet<R> allRegistrations = new HashSet<>();
        ListHashMap<Bitemporality, T> groups = this.sortIntoGroups(records);

        for (Bitemporality bitemporality : groups.keySet()) {

            timer.start(TASK_FIND_REGISTRATIONS);
            List<T> groupRecords = groups.get(bitemporality);
            List<R> registrations = entity.findRegistrations(bitemporality.registrationFrom, bitemporality.registrationTo);
            ArrayList<V> effects = new ArrayList<>();
            for (R registration : registrations) {
                V effect = registration.getEffect(bitemporality);
                if (effect == null) {
                    effect = registration.createEffect(bitemporality);
                }
                effects.add(effect);
            }
            allRegistrations.addAll(registrations);
            timer.measure(TASK_FIND_REGISTRATIONS);

            timer.start(TASK_FIND_ITEMS);
            // R-V-D scenario
            // Every DataItem that we locate for population must match the given effects exactly,
            // or we risk assigning data to an item that shouldn't be assigned to
            D baseData = null;
            HashSet<D> searchPool = new HashSet<>();
            for (V effect : effects) {
                searchPool.addAll(effect.getDataItems());
            }
            for (D data : searchPool) {
                if (data.getEffects().containsAll(effects) && effects.containsAll(data.getEffects())) {
                    baseData = data;
                    log.debug("Reuse existing basedata");
                    break;
                }
            }
            if (baseData == null) {
                log.debug("Creating new basedata");
                baseData = this.createDataItem();
                for (V effect : effects) {
                    log.debug("Wire basedata to effect "+effect.getRegistration().getRegistrationFrom()+"|"+effect.getRegistration().getRegistrationTo()+"|"+effect.getEffectFrom()+"|"+effect.getEffectTo());
                    baseData.addEffect(effect);
                }
            }
            timer.measure(TASK_FIND_ITEMS);

            timer.start(TASK_POPULATE_DATA);
            for (V effect : effects) {
                OffsetDateTime registrationFrom = effect.getRegistration().getRegistrationFrom();
                for (T record : groupRecords) {
                    record.populateBaseData(baseData, effect, registrationFrom, queryManager, session);
                }
            }
            timer.measure(TASK_POPULATE_DATA);
        }

        timer.start(TASK_SAVE);
        ArrayList<R> registrationList = new ArrayList<>(allRegistrations);
        Collections.sort(registrationList);
        for (R registration : registrationList) {
            try {
                queryManager.saveRegistration(session, entity, registration, false, false);
            } catch (DataFordelerException e) {
                e.printStackTrace();
            } catch (javax.persistence.EntityNotFoundException e) {
                e.printStackTrace();
            }
        }
        timer.measure(TASK_SAVE);

        return allRegistrations;
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

}

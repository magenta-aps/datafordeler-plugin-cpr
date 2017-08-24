package dk.magenta.datafordeler.cpr.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.database.*;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.exception.WrongSubclassException;
import dk.magenta.datafordeler.core.io.Receipt;
import dk.magenta.datafordeler.core.plugin.Communicator;
import dk.magenta.datafordeler.core.plugin.EntityManager;
import dk.magenta.datafordeler.core.plugin.HttpCommunicator;
import dk.magenta.datafordeler.core.plugin.RegisterManager;
import dk.magenta.datafordeler.core.role.SystemRole;
import dk.magenta.datafordeler.core.util.Equality;
import dk.magenta.datafordeler.core.util.ItemInputStream;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.CprPlugin;
import dk.magenta.datafordeler.cpr.configuration.CprConfiguration;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import dk.magenta.datafordeler.cpr.parsers.CprSubParser;
import dk.magenta.datafordeler.cpr.records.Bitemporality;
import dk.magenta.datafordeler.cpr.records.CprDataRecord;
import dk.magenta.datafordeler.cpr.records.Record;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
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
    public List<R> parseRegistration(String registrationData) throws IOException, ParseException {
        return this.parseRegistration(new ByteArrayInputStream(registrationData.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public List<R> parseRegistration(InputStream registrationData) throws ParseException, IOException {
        ArrayList<R> allRegistrations = new ArrayList<>();
        String charset = this.getConfiguration().getCharset();
        BufferedReader reader = new BufferedReader(new InputStreamReader(registrationData, charset));
        CprSubParser<T> parser = this.getParser();
        QueryManager queryManager = this.getQueryManager();

        int random = new Random().nextInt(10000);

        boolean done = false;
        long linesRead = 0;
        int limit = 1000;
        while (!done) {
            StringJoiner buffer = new StringJoiner("\n");
            int i;
            for (i = 0; i < limit; i++) {
                String line = reader.readLine();
                if (line != null) {
                    buffer.add(line);
                    linesRead++;
                } else {
                    done = true;
                }
            }
            log.debug("Loaded a total of "+linesRead+" lines");
            log.debug("Processing batch of "+i+" lines");
            InputStream chunk = new ByteArrayInputStream(buffer.toString().getBytes(charset));

            List<T> chunkRecords = parser.parse(chunk, charset);
            log.debug("Batch parsed into "+chunkRecords.size()+" records");
            ListHashMap<E, T> recordMap = new ListHashMap<>();
            HashMap<UUID, E> entityCache = new HashMap<>();

            Session session = this.getSessionManager().getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();

            // Find Entities (or create those that are missing), and put them in the recordMap
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


            for (E entity : recordMap.keySet()) {
                List<T> records = recordMap.get(entity);
                if (random-- == 0) {
                    inspect.add(entity.getUUID());
                    System.out.println(records);
                }
                Collection<R> entityRegistrations = this.parseRegistration(entity, records, queryManager, session);
                allRegistrations.addAll(entityRegistrations);
            }
            transaction.commit();
            session.close();
        }
        return allRegistrations;
    }


    private Collection<R> parseRegistration(E entity, List<T> records, QueryManager queryManager, Session session) {

        HashSet<R> allRegistrations = new HashSet<>();

        ListHashMap<Bitemporality, T> groups = this.sortIntoGroups(records);

        for (Bitemporality bitemporality : groups.keySet()) {
            List<T> groupRecords = groups.get(bitemporality);
            List<R> registrations = this.findRegistrations(entity, bitemporality);
            ArrayList<V> effects = new ArrayList<>();
            for (R registration : registrations) {
                V effect = registration.getEffect(bitemporality);
                if (effect == null) {
                    effect = registration.createEffect(bitemporality);
                }
                effects.add(effect);
            }
            allRegistrations.addAll(registrations);



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
                }
            }
            if (baseData == null) {
                log.debug("Creating new basedata");
                baseData = this.createDataItem();
            }
            for (V effect : effects) {
                log.debug("Wire basedata to effect "+effect.getRegistration().getRegistrationFrom()+"|"+effect.getRegistration().getRegistrationTo()+"|"+effect.getEffectFrom()+"|"+effect.getEffectTo());
                baseData.addEffect(effect);
                for (T record : groupRecords) {
                    record.populateBaseData(baseData, effect, effect.getRegistration().getRegistrationFrom(), queryManager, session);
                }
            }
        }

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
        return allRegistrations;
    }

/*
    protected <R extends Registration, E extends Entity<E, R>> Map<OffsetDateTime, R> buildRegistrations(E entity, List<T> records) {
        failFlag = false;
        // Create a list of registrations, sorted by date and made so that each registration ends when the next begins
        HashMap<OffsetDateTime, R> registrationMap = new HashMap<>();
        HashSet<OffsetDateTime> startTimeSet = new HashSet<>();
        TreeSet<R> existingRegistrations = new TreeSet<>();
        for (R registration : entity.getRegistrations()) {
            registrationMap.put(registration.getRegistrationFrom(), registration);
            existingRegistrations.add(registration);
            startTimeSet.add(registration.getRegistrationFrom());
        }
        TreeSet<OffsetDateTime> newTimes = new TreeSet<>();
        for (T record : records) {
            //System.out.println(record.getClass().getSimpleName()+": "+record.getLastUpdated());
            Set<OffsetDateTime> registrationStarts = record.getRegistrationTimestamps();
            startTimeSet.addAll(registrationStarts);
//            for (OffsetDateTime registrationStart : registrationStarts) {
//                if (!registrationMap.containsKey(registrationStart)) {
//                    log.debug("Create new registration " + registrationStart);
//                    R registration = entity.createRegistration();
//                    registration.setRegistrationFrom(registrationStart);
//                    registrationMap.put(registrationStart, registration);
//                    if (registrationStart != null) {
//                        newTimes.add(registrationStart);
//                    }
//                }
//            }
        }

        //HashSet<OffsetDateTime> startTimeSet = new HashSet<>(registrationMap.keySet());
        boolean hadNull = startTimeSet.remove(null);
        ArrayList<OffsetDateTime> startTimes = new ArrayList<>(startTimeSet);
        Collections.sort(startTimes);
        System.out.println("startTimes: "+startTimes);
        R last = null;
        if (hadNull) {
            startTimes.add(0, null);
        }
        for (OffsetDateTime startTime : startTimes) {
            System.out.println("--------");
            R registration = registrationMap.get(startTime);
            try {
                System.out.println("registration: "+objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(registration));
                System.out.println("last: "+objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(last));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            if (last != null) {
                if (last.getRegistrationTo() == null) {
                    last.setRegistrationTo(startTime);

                } else if (!Equality.equal(last.getRegistrationTo(), startTime)) {

                    log.error("Registration time mismatch on "+entity.getUUID()+": "+last.getRegistrationTo()+" != "+startTime);
                    inspect.add(entity.getUUID());

                    System.out.println("Existing registrations:");
                    for (R r : existingRegistrations) {
                        System.out.println("    "+r.getSequenceNumber()+": "+r.getRegistrationFrom()+" - "+r.getRegistrationTo());
                    }
                    System.out.println("New registration times:");
                    for (OffsetDateTime d : newTimes) {
                        System.out.println("    "+d);
                    }

                    registration = (R) last.split(startTime);
                    registration.setRegisterChecksum("split");
                    System.out.println("Extra registration created: "+registration.getRegistrationFrom()+" - "+registration.getRegistrationTo());
                    registrationMap.put(startTime, registration);
                    failFlag = true;
                }
            }
            if (registration == null) {
                log.debug("Create new registration " + startTime);
                registration = entity.createRegistration();
                registration.setRegistrationFrom(startTime);
                registrationMap.put(startTime, registration);
                if (startTime != null) {
                    newTimes.add(startTime);
                }
            }
            registration.setSequenceNumber(last != null ? last.getSequenceNumber() + 1 : 0);
            last = registration;
        }
        return registrationMap;
    }
*/
    private boolean failFlag = false;

    public List<UUID> inspect = new ArrayList<>();





    public List<R> findRegistrations(E entity, Bitemporality bitemporality) {
        // Find/create all necessary Registrations
        log.debug("Finding registrations within bounds "+bitemporality.registrationFrom+" - "+bitemporality.registrationTo);

        ArrayList<R> registrations = new ArrayList<>();

        ArrayList<R> orderedRegistrations = new ArrayList<>(entity.getRegistrations());
        Collections.sort(orderedRegistrations);

        OffsetDateTime latestEnd = OffsetDateTime.MIN;
        for (R existingRegistration : orderedRegistrations) {
            R registration = existingRegistration;
            log.debug("Looking at registration "+registration.getRegistrationFrom()+" - "+registration.getRegistrationTo());

            // There is a gap, or a missing registration at the start
            if (
                    latestEnd.isBefore(nFrom(registration.getRegistrationFrom())) &&
                    (latestEnd.isAfter(nFrom(bitemporality.registrationFrom)) || nFrom(bitemporality.registrationFrom).isBefore(nFrom(registration.getRegistrationFrom())))
            ) {
                log.debug("Gap found at "+latestEnd+" - "+registration.getRegistrationFrom()+", creating registration");
                R newReg = entity.createRegistration();
                newReg.setRegistrationFrom(latestEnd.isEqual(OffsetDateTime.MIN) ? bitemporality.registrationFrom : latestEnd);
                newReg.setRegistrationTo(registration.getRegistrationFrom());
                registrations.add(newReg);
            }
            latestEnd = registration.getRegistrationTo();

            // If the registration starts before our requested start, but ends after, we must do a split and move on with the second part
            if (
                    nFrom(registration.getRegistrationFrom()).isBefore(nFrom(bitemporality.registrationFrom)) &&
                    nTo(registration.getRegistrationTo()).isAfter(nFrom(bitemporality.registrationFrom))
            ) {
                log.debug("Registration straddles our start, split it at "+bitemporality.registrationFrom);
                registration = registration.split(bitemporality.registrationFrom);
                log.debug("Registration is now "+registration.getRegistrationFrom()+" - "+registration.getRegistrationTo());
            }
            // If the registration ends after our requested end, but begins before that, do a split
            if (
                    nFrom(registration.getRegistrationFrom()).isBefore(nTo(bitemporality.registrationTo)) &&
                    nTo(registration.getRegistrationTo()).isAfter(nTo(bitemporality.registrationTo))
            ) {
                log.debug("Registration straddles our end, split it at "+bitemporality.registrationTo);
                registration.split(bitemporality.registrationTo);
                log.debug("Registration is now "+registration.getRegistrationFrom()+" - "+registration.getRegistrationTo());
            }
            // If the registration lies within our bounds, include it
            if (
                    (
                            Equality.equal(registration.getRegistrationFrom(), bitemporality.registrationFrom) || // Check exact match
                            (nFrom(registration.getRegistrationFrom()).isAfter(nFrom(bitemporality.registrationFrom))) // Check if it lies after our requested start
                    ) && (
                            Equality.equal(registration.getRegistrationTo(), bitemporality.registrationTo) || // Check exact match
                            (nTo(registration.getRegistrationTo()).isBefore(nTo(bitemporality.registrationTo))) // Check if it lies before our requested end
                    )
            ) {
                log.debug("Registration lies within bounds, adding it to list");
                registrations.add(registration);
            }
        }

        // If the last existing registration ends before our requested end, create a new registration there
        OffsetDateTime requestedEndTime = bitemporality.registrationTo == null ? OffsetDateTime.MAX : bitemporality.registrationTo;
        if (latestEnd != null && latestEnd.isBefore(requestedEndTime)) {
            log.debug("Last registrations ended before our requested end, create missing registration at "+(latestEnd.isEqual(OffsetDateTime.MIN) ? bitemporality.registrationFrom : latestEnd)+" - "+bitemporality.registrationTo);
            R registration = entity.createRegistration();
            registration.setRegistrationFrom(latestEnd.isEqual(OffsetDateTime.MIN) ? bitemporality.registrationFrom : latestEnd);
            registration.setRegistrationTo(bitemporality.registrationTo);
            registrations.add(registration);
        }


        orderedRegistrations = new ArrayList<>(entity.getRegistrations());
        Collections.sort(orderedRegistrations);
        int seqNo = 0;
        for (R registration : orderedRegistrations) {




            registration.setSequenceNumber(seqNo);
            seqNo++;
        }

        return registrations;
    }

    private static OffsetDateTime nFrom(OffsetDateTime a) {
        if (a == null) return OffsetDateTime.MIN;
        return a;
    }

    private static OffsetDateTime nTo(OffsetDateTime a) {
        if (a == null) return OffsetDateTime.MAX;
        return a;
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

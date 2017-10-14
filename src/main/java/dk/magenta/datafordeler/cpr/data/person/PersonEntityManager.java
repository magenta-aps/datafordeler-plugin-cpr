package dk.magenta.datafordeler.cpr.data.person;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.RecordData;
import dk.magenta.datafordeler.core.database.RegistrationReference;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.exception.DataStreamException;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.CprPlugin;
import dk.magenta.datafordeler.cpr.data.CprEntityManager;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.parsers.CprSubParser;
import dk.magenta.datafordeler.cpr.parsers.PersonParser;
import dk.magenta.datafordeler.cpr.records.Bitemporality;
import dk.magenta.datafordeler.cpr.records.person.AddressRecord;
import dk.magenta.datafordeler.cpr.records.person.PersonDataRecord;
import dk.magenta.datafordeler.cpr.records.person.PersonRecord;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static dk.magenta.datafordeler.cpr.records.person.PersonDataRecord.RECORDTYPE_DOMESTIC_ADDRESS;

/**
 * Created by lars on 16-05-17.
 */
@Component
public class PersonEntityManager extends CprEntityManager<PersonDataRecord, PersonEntity, PersonRegistration, PersonEffect, PersonBaseData> {

    @Autowired
    private PersonEntityService personEntityService;

    @Autowired
    private PersonParser personParser;

    @Autowired
    private SessionManager sessionManager;

    public PersonEntityManager() {
        this.managedEntityClass = PersonEntity.class;
        this.managedEntityReferenceClass = PersonEntityReference.class;
        this.managedRegistrationClass = PersonRegistration.class;
        this.managedRegistrationReferenceClass = PersonRegistrationReference.class;
    }

    @Override
    protected String getBaseName() {
        return "person";
    }

    @Override
    public PersonEntityService getEntityService() {
        return this.personEntityService;
    }

    @Override
    public String getSchema() {
        return PersonEntity.schema;
    }

    @Override
    public List<PersonRegistration> parseRegistration(String registrationData, ImportMetadata importMetadata) throws DataFordelerException {
        return this.parseRegistration(new ByteArrayInputStream(registrationData.getBytes(StandardCharsets.UTF_8)), importMetadata);
    }

    @Override
    protected RegistrationReference createRegistrationReference(URI uri) {
        return new PersonRegistrationReference(uri);
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
        PersonEntity personEntity = new PersonEntity();
        personEntity.setPersonnummer(record.getCprNumber());
        return personEntity;
    }

    private PersonEntity createBasicEntity(String cprNumber) {
        PersonEntity personEntity = new PersonEntity();
        personEntity.setPersonnummer(cprNumber);
        return personEntity;
    }

    @Override
    protected PersonBaseData createDataItem() {
        return new PersonBaseData();
    }








    @Override
    public List<PersonRegistration> parseRegistration(InputStream registrationData, ImportMetadata importMetadata) throws DataFordelerException {
        ArrayList<PersonRegistration> allRegistrations = new ArrayList<>();
        String charset = this.getConfiguration().getRegisterCharset(this);
        BufferedReader reader = new BufferedReader(new InputStreamReader(registrationData, Charset.forName(charset)));

        CprSubParser<PersonDataRecord> parser = this.getParser();



        boolean done = false;
        long linesRead = 0;
        ListHashMap<String, PersonDataRecord> cache = new ListHashMap<>();
        HashSet<String> acceptList = new HashSet<>();
        HashSet<String> rejectList = new HashSet<>();

        while (!done) {
            String line;
            try {
                line = reader.readLine();
                if (line == null) {
                    done = true;
                    break;
                }
            } catch (IOException e) {
                throw new DataStreamException(e);
            }
            PersonDataRecord record = parser.parseLine(line);
            if (record != null) {
                String cprNumber = record.getCprNumber();
                if (!rejectList.contains(cprNumber)) {
                    cache.add(cprNumber, record);

                    if (RECORDTYPE_DOMESTIC_ADDRESS.equals(record.getRecordType())) {
                        AddressRecord addressRecord = (AddressRecord) record;
                        if (addressRecord.getMunicipalityCode() >= 950) {
                            // ok
                            acceptList.add(cprNumber);
                        } else {
                            //discard
                            rejectList.add(cprNumber);
                            cache.remove(cprNumber);
                        }
                    }
                }
            }
            linesRead++;
            System.out.println(linesRead + " lines read");
        }
        rejectList.clear();
        System.out.println(linesRead + " lines read into memory");
        int count = cache.size();

        Session session = this.getSessionManager().getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        int counter = 0;
        for (String cprNumber : acceptList) {
            List<PersonDataRecord> records = cache.get(cprNumber);
            UUID uuid = this.generateUUID(records.get(0));
            PersonEntity entity = QueryManager.getEntity(session, uuid, PersonEntity.class);

            if (entity == null) {
                entity = this.createBasicEntity(records.get(0));
                entity.setUUID(uuid);
                entity.setDomain(CprPlugin.getDomain());
            }
            int komkod = 0;
            for (PersonDataRecord record : records) {
                if (record.getRecordType() == RECORDTYPE_DOMESTIC_ADDRESS) {
                    komkod = ((AddressRecord) record).getMunicipalityCode();
                }
            }

            Collection<PersonRegistration> entityRegistrations = this.parseRegistration(entity, records, session, importMetadata);
            allRegistrations.addAll(entityRegistrations);

            counter++;
            System.out.println(counter + " / " + count + " " + komkod);
        }

        transaction.commit();
        session.close();

        return allRegistrations;
    }
}

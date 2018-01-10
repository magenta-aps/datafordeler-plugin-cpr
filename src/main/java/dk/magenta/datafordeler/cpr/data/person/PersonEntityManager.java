package dk.magenta.datafordeler.cpr.data.person;

import dk.magenta.datafordeler.core.database.RegistrationReference;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.exception.DataStreamException;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.cpr.CprRegisterManager;
import dk.magenta.datafordeler.cpr.data.CprEntityManager;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.parsers.CprSubParser;
import dk.magenta.datafordeler.cpr.parsers.PersonParser;
import dk.magenta.datafordeler.cpr.records.person.AddressRecord;
import dk.magenta.datafordeler.cpr.records.person.ForeignAddressRecord;
import dk.magenta.datafordeler.cpr.records.person.PersonDataRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

import static dk.magenta.datafordeler.cpr.records.person.PersonDataRecord.RECORDTYPE_FOREIGN_ADDRESS;

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

    private HashSet<String> nonGreenlandicCprNumbers = new HashSet<>();

    @Override
    public List<PersonRegistration> parseData(InputStream registrationData, ImportMetadata importMetadata) throws DataFordelerException {
        try {
            List<PersonRegistration> result = super.parseData(registrationData, importMetadata);
            CprRegisterManager registerManager = (CprRegisterManager) this.getRegisterManager();
            if (registerManager.isSetupSubscriptionEnabled() && !this.nonGreenlandicCprNumbers.isEmpty()) {
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
            }
            if (record instanceof ForeignAddressRecord) {
                ForeignAddressRecord foreignAddressRecord = (ForeignAddressRecord) record;
                this.nonGreenlandicCprNumbers.add(foreignAddressRecord.getCprNumber());
            }
        }
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

    @Override
    protected PersonBaseData createDataItem() {
        return new PersonBaseData();
    }

    private void createSubscription(HashSet<String> addCprNumbers) throws DataFordelerException {
        this.createSubscription(addCprNumbers, new HashSet<>());
    }

    private void createSubscription(HashSet<String> addCprNumbers, HashSet<String> removeCprNumbers) throws DataFordelerException {
        this.log.info("Collected these numbers for subscription: "+addCprNumbers);
        CprRegisterManager registerManager = (CprRegisterManager) this.getRegisterManager();
        String charset = this.getConfiguration().getRegisterCharset(this);
        String keyConstant = "";
        StringJoiner content = new StringJoiner("\r\n");

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
                                registerManager.getCustomerId(),
                                0,
                                operation,
                                cprNumber,
                                keyConstant,
                                ""
                        )
                );
            }
        }

        registerManager.addSubscription(content.toString(), charset, this);
    }
}

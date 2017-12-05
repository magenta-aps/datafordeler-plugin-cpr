package dk.magenta.datafordeler.cpr.data.person;

import dk.magenta.datafordeler.core.database.RegistrationReference;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.cpr.data.CprEntityManager;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.parsers.CprSubParser;
import dk.magenta.datafordeler.cpr.parsers.PersonParser;
import dk.magenta.datafordeler.cpr.records.person.PersonDataRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.UUID;

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
/*
    @Override
    public List<PersonRegistration> parseData(String registrationData, ImportMetadata importMetadata) throws DataFordelerException {
        return this.parseData(new ByteArrayInputStream(registrationData.getBytes(StandardCharsets.UTF_8)), importMetadata);
    }*/

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

}

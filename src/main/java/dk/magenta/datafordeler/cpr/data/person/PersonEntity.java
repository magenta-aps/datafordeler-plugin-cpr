package dk.magenta.datafordeler.cpr.data.person;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dk.magenta.datafordeler.core.database.Entity;
import dk.magenta.datafordeler.core.database.Identification;

import javax.persistence.Column;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.UUID;

import static dk.magenta.datafordeler.cpr.data.person.PersonEntity.DB_FIELD_CPR_NUMBER;

/**
 * Created by lars on 16-05-17.
 */
@javax.persistence.Entity
@Table(name="cpr_person_entity", indexes = {
        @Index(name = "identification", columnList = "identification_id"),
        @Index(name = "personnummer", columnList = DB_FIELD_CPR_NUMBER)
})
@XmlAccessorType(XmlAccessType.FIELD)
public class PersonEntity extends Entity<PersonEntity, PersonRegistration> {

    public PersonEntity() {
    }

    public PersonEntity(Identification identification) {
        super(identification);
    }

    public PersonEntity(UUID uuid, String domain) {
        super(uuid, domain);
    }

    @Override
    protected PersonRegistration createEmptyRegistration() {
        return new PersonRegistration();
    }

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="type")
    public static final String schema = "Person";

    public static final String DB_FIELD_CPR_NUMBER = "personnummer";
    public static final String IO_FIELD_CPR_NUMBER = "personnummer";

    @Column(name = DB_FIELD_CPR_NUMBER)
    @JsonProperty("personnummer")
    @XmlElement(name=("personnummer"))
    private String personnummer;

    public String getPersonnummer() {
        return this.personnummer;
    }

    public void setPersonnummer(String personnummer) {
        this.personnummer = personnummer;
    }



    public static UUID generateUUID(String cprNumber) {
        String uuidInput = "person:"+cprNumber;
        return UUID.nameUUIDFromBytes(uuidInput.getBytes());
    }

}

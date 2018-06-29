package dk.magenta.datafordeler.cpr.data.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dk.magenta.datafordeler.core.database.Identification;
import dk.magenta.datafordeler.cpr.CprPlugin;
import dk.magenta.datafordeler.cpr.data.CprEntity;
import dk.magenta.datafordeler.cpr.records.person.*;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static dk.magenta.datafordeler.cpr.data.person.PersonEntity.DB_FIELD_CPR_NUMBER;

/**
 * An Entity representing a person. Bitemporal data is structured as
 * described in {@link dk.magenta.datafordeler.core.database.Entity}
 */
@javax.persistence.Entity
@Table(name= CprPlugin.DEBUG_TABLE_PREFIX + "cpr_person_entity", indexes = {
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + "cpr_person_identification", columnList = "identification_id", unique = true),
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + "cpr_person_personnummer", columnList = DB_FIELD_CPR_NUMBER, unique = true)
})
@XmlAccessorType(XmlAccessType.FIELD)
public class PersonEntity extends CprEntity<PersonEntity, PersonRegistration> {

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






    public static final String IO_FIELD_ADDRESS_CONAME = "conavn";
    @JsonProperty(IO_FIELD_ADDRESS_CONAME)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<AddressConameDataRecord> personAddressConameDataSet = new HashSet<>();

    public static final String IO_FIELD_ADDRESS = "addresse";
    @JsonProperty(IO_FIELD_ADDRESS)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<AddressDataRecord> personAddressDataSet = new HashSet<>();

    public static final String IO_FIELD_ADDRESS_NAME = "addresseringsnavn";
    @JsonProperty(IO_FIELD_ADDRESS_NAME)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<AddressNameDataRecord> personAddressNameDataSet = new HashSet<>();

    public static final String IO_FIELD_BIRTHPLACE = "fødselsted";
    @JsonProperty(IO_FIELD_BIRTHPLACE)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<BirthPlaceDataRecord> personBirthPlaceDataSet = new HashSet<>();

    public static final String IO_FIELD_BIRTHPLACE_VERIFICATION = "fødselssted_verifikation";
    @JsonProperty(IO_FIELD_BIRTHPLACE_VERIFICATION)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<BirthPlaceVerificationDataRecord> personBirthPlaceVerificationDataSet = new HashSet<>();

    public static final String IO_FIELD_BIRTHTIME = "fødselstidspunkt";
    @JsonProperty(IO_FIELD_BIRTHTIME)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<BirthTimeDataRecord> personBirthTimeDataSet = new HashSet<>();

    public static final String IO_FIELD_CHURCH = "folkekirkerelation";
    @JsonProperty(IO_FIELD_CHURCH)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<ChurchDataRecord> personChurchDataSet = new HashSet<>();

    public static final String IO_FIELD_CHURCH_VERIFICATION = "folkekirkerelation_verifikation";
    @JsonProperty(IO_FIELD_CHURCH_VERIFICATION)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<ChurchVerificationDataRecord> personChurchVerificationDataSet = new HashSet<>();





    public void addBitemporalRecord(CprBitemporalPersonRecord record) {
        if (record instanceof AddressConameDataRecord) {
            this.personAddressConameDataSet.add((AddressConameDataRecord) record);
        }
        if (record instanceof AddressDataRecord) {
            this.personAddressDataSet.add((AddressDataRecord) record);
        }
        if (record instanceof AddressNameDataRecord) {
            this.personAddressNameDataSet.add((AddressNameDataRecord) record);
        }
        if (record instanceof BirthPlaceDataRecord) {
            this.personBirthPlaceDataSet.add((BirthPlaceDataRecord) record);
        }
        if (record instanceof BirthPlaceVerificationDataRecord) {
            this.personBirthPlaceVerificationDataSet.add((BirthPlaceVerificationDataRecord) record);
        }
        if (record instanceof BirthTimeDataRecord) {
            this.personBirthTimeDataSet.add((BirthTimeDataRecord) record);
        }
        if (record instanceof ChurchDataRecord) {
            this.personChurchDataSet.add((ChurchDataRecord) record);
        }
        if (record instanceof ChurchVerificationDataRecord) {
            this.personChurchVerificationDataSet.add((ChurchVerificationDataRecord) record);
        }
        record.setEntity(this);
    }

    @JsonIgnore
    public Set<CprBitemporalPersonRecord> getBitemporalRecords() {
        HashSet<CprBitemporalPersonRecord> records = new HashSet<>();
        records.addAll(this.personAddressConameDataSet);
        records.addAll(this.personAddressDataSet);
        records.addAll(this.personAddressNameDataSet);
        records.addAll(this.personBirthPlaceDataSet);
        records.addAll(this.personBirthPlaceVerificationDataSet);
        return records;
    }
}

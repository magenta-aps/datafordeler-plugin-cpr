package dk.magenta.datafordeler.cpr.data.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dk.magenta.datafordeler.core.database.Identification;
import dk.magenta.datafordeler.cpr.CprPlugin;
import dk.magenta.datafordeler.cpr.data.CprEntity;
import dk.magenta.datafordeler.cpr.records.person.*;
import dk.magenta.datafordeler.cpr.records.person.data.*;
import org.hibernate.Session;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.*;

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

    public static final String IO_FIELD_CITIZENSHIP = "statsborgerskab";
    @JsonProperty(IO_FIELD_CITIZENSHIP)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<CitizenshipDataRecord> personCitizenshipDataSet = new HashSet<>();

    public static final String IO_FIELD_CITIZENSHIP_VERIFICATION = "statsborgerskab_verifikation";
    @JsonProperty(IO_FIELD_CITIZENSHIP_VERIFICATION)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<CitizenshipVerificationDataRecord> personCitizenshipVerificationDataSet = new HashSet<>();

    public static final String IO_FIELD_CIVILSTATUS = "civilstatus";
    @JsonProperty(IO_FIELD_CIVILSTATUS)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<CivilStatusDataRecord> personCivilstatusDataSet = new HashSet<>();

    public static final String IO_FIELD_CIVILSTATUS_AUTHORITYTEXT = "civilstatus_autoritetstekst";
    @JsonProperty(IO_FIELD_CIVILSTATUS_AUTHORITYTEXT)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<CivilStatusAuthorityTextDataRecord> personCivilstatusAuthorityTextDataSet = new HashSet<>();

    public static final String IO_FIELD_CIVILSTATUS_VERIFICATION = "civilstatus_verifikation";
    @JsonProperty(IO_FIELD_CIVILSTATUS_VERIFICATION)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<CivilStatusVerificationDataRecord> personCivilstatusVerificationDataSet = new HashSet<>();

    public static final String IO_FIELD_FOREIGN_ADDRESS = "udlandsadresse";
    @JsonProperty(IO_FIELD_FOREIGN_ADDRESS)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<ForeignAddressDataRecord> personForeignAddressDataSet = new HashSet<>();

    public static final String IO_FIELD_FOREIGN_ADDRESS_EMIGRATION = "udrejse";
    @JsonProperty(IO_FIELD_FOREIGN_ADDRESS_EMIGRATION)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<ForeignAddressEmigrationDataRecord> personForeignAddressEmigrationDataSet = new HashSet<>();

    public static final String IO_FIELD_MOVE_MUNICIPALITY = "kommuneflytning";
    @JsonProperty(IO_FIELD_MOVE_MUNICIPALITY)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<MoveMunicipalityDataRecord> personMoveMunicipalityDataSet = new HashSet<>();

    public static final String IO_FIELD_NAME = "navn";
    @JsonProperty(IO_FIELD_NAME)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<NameDataRecord> personNameDataSet = new HashSet<>();

    public static final String IO_FIELD_NAME_AUTHORITY_TEXT = "navn_autoritetstekst";
    @JsonProperty(IO_FIELD_NAME_AUTHORITY_TEXT)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<NameAuthorityTextDataRecord> personNameAuthorityTextDataSet = new HashSet<>();

    public static final String IO_FIELD_NAME_VERIFICATION = "navn_verifikation";
    @JsonProperty(IO_FIELD_NAME_VERIFICATION)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<NameVerificationDataRecord> personNameVerificationDataSet = new HashSet<>();

    public static final String IO_FIELD_MOTHER = "mor";
    @JsonProperty(IO_FIELD_MOTHER)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    @Where(clause = ParentDataRecord.DB_FIELD_IS_MOTHER + "=true")
    Set<ParentDataRecord> personMotherDataSet = new HashSet<>();

    public static final String IO_FIELD_MOTHER_VERIFICATION = "mor_verifikation";
    @JsonProperty(IO_FIELD_MOTHER_VERIFICATION)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    @Where(clause = ParentDataRecord.DB_FIELD_IS_MOTHER + "=true")
    Set<ParentVerificationDataRecord> personMotherVerificationDataSet = new HashSet<>();

    public static final String IO_FIELD_FATHER = "far";
    @JsonProperty(IO_FIELD_FATHER)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    @Where(clause = ParentDataRecord.DB_FIELD_IS_MOTHER + "=false")
    Set<ParentDataRecord> personFatherDataSet = new HashSet<>();

    public static final String IO_FIELD_FATHER_VERIFICATION = "far_verifikation";
    @JsonProperty(IO_FIELD_FATHER_VERIFICATION)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    @Where(clause = ParentDataRecord.DB_FIELD_IS_MOTHER + "=false")
    Set<ParentVerificationDataRecord> personFatherVerificationDataSet = new HashSet<>();

    public static final String IO_FIELD_CORE = "kernedata";
    @JsonProperty(IO_FIELD_CORE)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<PersonCoreDataRecord> personCoreDataSet = new HashSet<>();

    public static final String IO_FIELD_PNR = "personnummer";
    @JsonProperty(IO_FIELD_PNR)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<PersonNumberDataRecord> personNumberDataSet = new HashSet<>();

    public static final String IO_FIELD_POSITION = "stilling";
    @JsonProperty(IO_FIELD_POSITION)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<PersonPositionDataRecord> personPositionDataSet = new HashSet<>();

    public static final String IO_FIELD_STATUS = "status";
    @JsonProperty(IO_FIELD_STATUS)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<PersonStatusDataRecord> personStatusDataSet = new HashSet<>();

    public static final String IO_FIELD_PROTECTION = "beskyttelse";
    @JsonProperty(IO_FIELD_PROTECTION)
    @OneToMany(mappedBy = CprBitemporalPersonRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    Set<ProtectionDataRecord> personProtectionDataSet = new HashSet<>();


    public void addBitemporalRecord(CprBitemporalPersonRecord record, Session session) {
        if (record instanceof AddressConameDataRecord) {
            addItem(this.personAddressConameDataSet, record, session);
        }
        if (record instanceof AddressDataRecord) {
            addItem(this.personAddressDataSet, record, session);
        }
        if (record instanceof AddressNameDataRecord) {
            addItem(this.personAddressNameDataSet, record, session);
        }
        if (record instanceof BirthPlaceDataRecord) {
            addItem(this.personBirthPlaceDataSet, record, session);
        }
        if (record instanceof BirthPlaceVerificationDataRecord) {
            addItem(this.personBirthPlaceVerificationDataSet, record, session);
        }
        if (record instanceof BirthTimeDataRecord) {
            addItem(this.personBirthTimeDataSet, record, session);
        }
        if (record instanceof ChurchDataRecord) {
            addItem(this.personChurchDataSet, record, session);
        }
        if (record instanceof ChurchVerificationDataRecord) {
            addItem(this.personChurchVerificationDataSet, record, session);
        }
        if (record instanceof CitizenshipDataRecord) {
            addItem(this.personCitizenshipDataSet, record, session);
        }
        if (record instanceof CitizenshipVerificationDataRecord) {
            addItem(this.personCitizenshipVerificationDataSet, record, session);
        }
        if (record instanceof CivilStatusDataRecord) {
            addItem(this.personCivilstatusDataSet, record, session);
        }
        if (record instanceof CivilStatusAuthorityTextDataRecord) {
            addItem(this.personCivilstatusAuthorityTextDataSet, record, session);
        }
        if (record instanceof CivilStatusVerificationDataRecord) {
            addItem(this.personCivilstatusVerificationDataSet, record, session);
        }
        if (record instanceof ForeignAddressDataRecord) {
            addItem(this.personForeignAddressDataSet, record, session);
        }
        if (record instanceof ForeignAddressDataRecord) {
            addItem(this.personForeignAddressDataSet, record, session);
        }
        if (record instanceof ForeignAddressEmigrationDataRecord) {
            addItem(this.personForeignAddressEmigrationDataSet, record, session);
        }
        if (record instanceof MoveMunicipalityDataRecord) {
            addItem(this.personMoveMunicipalityDataSet, record, session);
        }
        if (record instanceof NameAuthorityTextDataRecord) {
            addItem(this.personNameAuthorityTextDataSet, record, session);
        }
        if (record instanceof NameDataRecord) {
            addItem(this.personNameDataSet, record, session);
        }
        if (record instanceof NameVerificationDataRecord) {
            addItem(this.personNameVerificationDataSet, record, session);
        }
        if (record instanceof ParentDataRecord) {
            ParentDataRecord pRecord = (ParentDataRecord) record;
            if (pRecord.isMother()) {
                addItem(this.personMotherDataSet, pRecord, session);
            } else {
                addItem(this.personFatherDataSet, pRecord, session);
            }
        }
        if (record instanceof ParentVerificationDataRecord) {
            ParentVerificationDataRecord pRecord = (ParentVerificationDataRecord) record;
            if (pRecord.isMother()) {
                addItem(this.personMotherVerificationDataSet, pRecord, session);
            } else {
                addItem(this.personFatherVerificationDataSet, pRecord, session);
            }
        }
        if (record instanceof PersonCoreDataRecord) {
            addItem(this.personCoreDataSet, record, session);
        }
        if (record instanceof PersonNumberDataRecord) {
            addItem(this.personNumberDataSet, record, session);
        }
        if (record instanceof PersonPositionDataRecord) {
            addItem(this.personPositionDataSet, record, session);
        }
        if (record instanceof PersonStatusDataRecord) {
            addItem(this.personStatusDataSet, record, session);
        }
        if (record instanceof ProtectionDataRecord) {
            addItem(this.personProtectionDataSet, record, session);
        }
        record.setEntity(this);
    }

    private static <E extends CprBitemporalPersonRecord> boolean addItem(Set<E> set, CprBitemporalPersonRecord newItem, Session session) {
        if (newItem != null) {
            for (E oldItem : set) {
                if (newItem.equalData(oldItem) && Objects.equals(newItem.getRegistrationFrom(), oldItem.getRegistrationFrom())) {
                    if (newItem.isHistoric() && !oldItem.isHistoric() && oldItem.getEffectTo() == null) {
                        //System.out.println("matching item, removing preexisting");
                        set.remove(oldItem);
                        session.delete(oldItem);
                        return set.add((E) newItem);
                    } else {
                        //System.out.println("matching item with same historicity, not adding");
                        return false;
                    }
                }
            }
            //System.out.println("nonmatching item");
            return set.add((E) newItem);
        }
        return false;
    }

    @JsonIgnore
    public Set<CprBitemporalPersonRecord> getBitemporalRecords() {
        HashSet<CprBitemporalPersonRecord> records = new HashSet<>();
        records.addAll(this.personAddressConameDataSet);
        records.addAll(this.personAddressDataSet);
        records.addAll(this.personAddressNameDataSet);
        records.addAll(this.personBirthPlaceDataSet);
        records.addAll(this.personBirthPlaceVerificationDataSet);
        records.addAll(this.personBirthTimeDataSet);
        records.addAll(this.personChurchDataSet);
        records.addAll(this.personChurchVerificationDataSet);
        records.addAll(this.personCitizenshipDataSet);
        records.addAll(this.personCitizenshipVerificationDataSet);
        records.addAll(this.personCivilstatusDataSet);
        records.addAll(this.personCivilstatusAuthorityTextDataSet);
        records.addAll(this.personCivilstatusVerificationDataSet);
        records.addAll(this.personForeignAddressDataSet);
        records.addAll(this.personForeignAddressEmigrationDataSet);
        records.addAll(this.personMoveMunicipalityDataSet);
        records.addAll(this.personNameAuthorityTextDataSet);
        records.addAll(this.personNameVerificationDataSet);
        records.addAll(this.personMotherDataSet);
        records.addAll(this.personMotherVerificationDataSet);
        records.addAll(this.personFatherDataSet);
        records.addAll(this.personFatherVerificationDataSet);
        records.addAll(this.personCoreDataSet);
        records.addAll(this.personNumberDataSet);
        records.addAll(this.personPositionDataSet);
        records.addAll(this.personStatusDataSet);
        records.addAll(this.personProtectionDataSet);
        return records;
    }
}

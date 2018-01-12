package dk.magenta.datafordeler.cpr.data.person.data;

import dk.magenta.datafordeler.core.database.DataItem;
import dk.magenta.datafordeler.core.database.DatabaseEntry;
import dk.magenta.datafordeler.core.database.LookupDefinition;
import dk.magenta.datafordeler.cpr.data.CprData;
import dk.magenta.datafordeler.cpr.data.DetailData;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import org.hibernate.Session;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Base class for Person data, linking to Effects and delegating storage to referred classes
 */
@Entity
@Table(name="cpr_person_data", indexes = {
        @Index(name = "cpr_person_lastUpdated", columnList = DataItem.DB_FIELD_LAST_UPDATED),
        @Index(name = "cpr_person_name", columnList = PersonBaseData.DB_FIELD_NAME + DatabaseEntry.REF),
        @Index(name = "cpr_person_address", columnList = PersonBaseData.DB_FIELD_ADDRESS + DatabaseEntry.REF)
})
public class PersonBaseData extends CprData<PersonEffect, PersonBaseData> {

    public static final String DB_FIELD_CORE = "coreData";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_CORE + DatabaseEntry.REF)
    private PersonCoreData coreData;

    public static final String DB_FIELD_STATUS = "status";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_STATUS + DatabaseEntry.REF)
    private PersonStatusData status;

    public static final String DB_FIELD_MOTHER = "mother";
    public static final String IO_FIELD_MOTHER = "mor";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_MOTHER + DatabaseEntry.REF)
    private PersonParentData mother;

    public static final String DB_FIELD_FATHER = "father";
    public static final String IO_FIELD_FATHER = "far";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_FATHER + DatabaseEntry.REF)
    private PersonParentData father;

    public static final String DB_FIELD_MOTHERVERIFICATION = "motherVerification";
    public static final String IO_FIELD_MOTHERVERIFICATION = "morVerification";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_MOTHERVERIFICATION + DatabaseEntry.REF)
    private PersonParentVerificationData motherVerification;

    public static final String DB_FIELD_FATHERVERIFICATION = "fatherVerification";
    public static final String IO_FIELD_FATHERVERIFICATION = "farVerification";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_FATHERVERIFICATION + DatabaseEntry.REF)
    private PersonParentVerificationData fatherVerification;

    public static final String DB_FIELD_POSITION = "position";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_POSITION + DatabaseEntry.REF)
    private PersonPositionData position;

    public static final String DB_FIELD_BIRTH = "birth";
    public static final String IO_FIELD_BIRTH = "fødsel";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_BIRTH + DatabaseEntry.REF)
    private PersonBirthData birth;

    public static final String DB_FIELD_ADDRESS = "address";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_ADDRESS + DatabaseEntry.REF)
    private PersonAddressData address;

    public static final String DB_FIELD_CONAME = "coname";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_CONAME + DatabaseEntry.REF)
    private PersonAddressConameData coname;

    public static final String DB_FIELD_MOVEMUNICIPALITY = "moveMunicipality";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_MOVEMUNICIPALITY + DatabaseEntry.REF)
    private PersonMoveMunicipalityData moveMunicipality;

    public static final String DB_FIELD_NAME = "name";
    public static final String IO_FIELD_NAME = "navn";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_NAME + DatabaseEntry.REF)
    private PersonNameData name;

    public static final String DB_FIELD_ADDRESSING_NAME = "addressingName";
    public static final String IO_FIELD_ADDRESSING_NAME = "adresseringsnavn";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_ADDRESSING_NAME + DatabaseEntry.REF)
    private PersonAddressNameData addressingName;

    public static final String DB_FIELD_NAME_VERIFICATION = "nameVerification";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_NAME_VERIFICATION + DatabaseEntry.REF)
    private PersonNameVerificationData nameVerification;

    public static final String DB_FIELD_NAME_AUTHORITY = "nameAuthority";
    public static final String IO_FIELD_NAME_AUTHORITY = "navnemyndighed";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_NAME_AUTHORITY + DatabaseEntry.REF)
    private PersonNameAuthorityTextData nameAuthority;

    public static final String DB_FIELD_PROTECTION = "protection";
    public static final String IO_FIELD_PROTECTION = "beskyttelse";
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "personBaseData")
    private Set<PersonProtectionData> protection = new HashSet<>();

    public static final String DB_FIELD_MIGRATION = "migration";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_MIGRATION + DatabaseEntry.REF)
    private PersonEmigrationData migration;

    public static final String DB_FIELD_FOREIGN_ADDRESS = "foreignAddress";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_FOREIGN_ADDRESS + DatabaseEntry.REF)
    private PersonForeignAddressData foreignAddress;

    public static final String DB_FIELD_CIVIL_STATUS = "civilStatus";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_CIVIL_STATUS + DatabaseEntry.REF)
    private PersonCivilStatusData civilStatus;

    public static final String DB_FIELD_CIVIL_STATUS_VERIFICATION = "civilStatusVerification";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_CIVIL_STATUS_VERIFICATION + DatabaseEntry.REF)
    private PersonCivilStatusVerificationData civilStatusVerification;

    public static final String DB_FIELD_CIVIL_STATUS_AUTHORITY = "civilStatusAuthority";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_CIVIL_STATUS_AUTHORITY + DatabaseEntry.REF)
    private PersonCivilStatusAuthorityTextData civilStatusAuthority;

    public static final String DB_FIELD_CPRNUMBER = "cprNumber";
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = DB_FIELD_CPRNUMBER + DatabaseEntry.REF)
    private PersonNumberData cprNumber;



    public PersonCoreData getCoreData() {
        return coreData;
    }

    public PersonStatusData getStatus() {
        return status;
    }

    public PersonParentData getMother() {
        return mother;
    }

    public PersonParentData getFather() {
        return father;
    }

    public PersonParentVerificationData getMotherVerification() {
        return motherVerification;
    }

    public PersonParentVerificationData getFatherVerification() {
        return fatherVerification;
    }

    public PersonPositionData getPosition() {
        return position;
    }

    public PersonBirthData getBirth() {
        return birth;
    }

    public PersonAddressData getAddress() {
        return address;
    }

    public PersonAddressConameData getConame() {
        return coname;
    }

    public PersonMoveMunicipalityData getMoveMunicipality() {
        return moveMunicipality;
    }

    public PersonNameData getName() {
        return name;
    }

    public PersonAddressNameData getAddressingName() {
        return addressingName;
    }

    public PersonNameVerificationData getNameVerification() {
        return nameVerification;
    }

    public PersonNameAuthorityTextData getNameAuthority() {
        return nameAuthority;
    }

    public Collection<PersonProtectionData> getProtection() {
        return protection;
    }

    public PersonEmigrationData getMigration() {
        return migration;
    }

    public PersonForeignAddressData getForeignAddress() {
        return foreignAddress;
    }

    public PersonCivilStatusData getCivilStatus() {
        return this.civilStatus;
    }

    public PersonCivilStatusVerificationData getCivilStatusVerification() {
        return this.civilStatusVerification;
    }

    public PersonCivilStatusAuthorityTextData getCivilStatusAuthority() {
        return this.civilStatusAuthority;
    }

    public PersonNumberData getCprNumber() {
        return this.cprNumber;
    }

    public void setPersonnummer(String personnummer) {
        if (this.coreData == null) {
            this.coreData = new PersonCoreData();
        }
        this.coreData.setCprNumber(personnummer);
    }

    public void setKoen(String koen) {
        if (this.coreData == null) {
            this.coreData = new PersonCoreData();
        }
        this.coreData.setKoen(koen);
    }

    public void setStartAuthority(int authority) {
        if (this.coreData == null) {
            this.coreData = new PersonCoreData();
        }
        this.coreData.setAuthority(authority);
    }

    public void setStatus(int status) {
        if (this.status == null) {
            this.status = new PersonStatusData();
        }
        this.status.setStatus(status);
    }

    public void setMother(String name, boolean nameMarking, String cprNumber, LocalDate birthDate, boolean birthDateUncertain, int authorityCode) {
        if (this.mother == null) {
            this.mother = new PersonParentData();
            this.mother.setMother(true);
        }
        this.mother.setName(name);
        this.mother.setNameMarking(nameMarking);
        this.mother.setCprNumber(cprNumber);
        this.mother.setBirthDate(birthDate);
        this.mother.setBirthDateUncertain(birthDateUncertain);
        this.mother.setAuthority(authorityCode);
    }

    public void setFather(String name, boolean nameMarking, String cprNumber, LocalDate birthDate, boolean birthDateUncertain, int authorityCode) {
        if (this.father == null) {
            this.father = new PersonParentData();
            this.father.setMother(false);
        }
        this.father.setName(name);
        this.father.setNameMarking(nameMarking);
        this.father.setCprNumber(cprNumber);
        this.father.setBirthDate(birthDate);
        this.father.setBirthDateUncertain(birthDateUncertain);
        this.father.setAuthority(authorityCode);
    }

    public void setMotherVerification(int authorityCode, boolean verified) {
        if (this.motherVerification == null) {
            this.motherVerification = new PersonParentVerificationData();
            this.motherVerification.setMother(true);
        }
        this.motherVerification.setAuthority(authorityCode);
        this.motherVerification.setVerified(verified);
    }

    public void setFatherVerification(int authorityCode, boolean verified) {
        if (this.fatherVerification == null) {
            this.fatherVerification = new PersonParentVerificationData();
            this.fatherVerification.setMother(false);
        }
        this.fatherVerification.setAuthority(authorityCode);
        this.fatherVerification.setVerified(verified);
    }

    public void setPosition(int authorityCode, String position) {
        if (this.position == null) {
            this.position = new PersonPositionData();
        }
        this.position.setAuthority(authorityCode);
        this.position.setPosition(position);
    }

    public void setBirth(LocalDateTime foedselsdato, boolean foedselsdatoUsikkerhedsmarkering,
                         String cprFoedselsregistreringsstedskode, String cprFoedselsregistreringsstedsnavn,
                         int foedselsraekkefoelge) {
        if (this.birth == null) {
            this.birth = new PersonBirthData();
        }
        this.birth.setBirthPlaceCode(cprFoedselsregistreringsstedskode);
        this.birth.setBirthPlaceName(cprFoedselsregistreringsstedsnavn);
        this.birth.setBirthDatetime(foedselsdato);
        this.birth.setBirthDatetimeUncertain(foedselsdatoUsikkerhedsmarkering);

        this.birth.setFoedselsraekkefoelge(foedselsraekkefoelge);
    }

    public void setAddress(int authority, String bygningsnummer, String bynavn, int cprKommunekode,
                           String cprKommunenavn, int cprVejkode, String darAdresse, String etage,
                           String husnummer, String postdistrikt, String postnummer, String sideDoer,
                           String adresselinie1, String adresselinie2, String adresselinie3, String adresselinie4,
                           String adresselinie5, int addressTextType, int startAuthority) {
        if (this.address == null) {
            this.address = new PersonAddressData();
        }
        this.address.setAuthority(authority);
        this.address.setBuildingNumber(bygningsnummer);
        this.address.setCityName(bynavn);
        this.address.setMunicipalityCode(cprKommunekode);
        this.address.setMunicipalityName(cprKommunenavn);
        this.address.setRoadCode(cprVejkode);
        this.address.setDarAddress(darAdresse);
        this.address.setFloor(etage);
        this.address.setHouseNumber(husnummer);
        this.address.setPostalDistrict(postdistrikt);
        this.address.setPostalCode(postnummer);
        this.address.setDoor(sideDoer);

        this.address.setRoadAddressLine1(adresselinie1);
        this.address.setRoadAddressLine2(adresselinie2);
        this.address.setRoadAddressLine3(adresselinie3);
        this.address.setRoadAddressLine4(adresselinie4);
        this.address.setRoadAddressLine5(adresselinie5);

        this.address.setAdressetekststype(addressTextType);
        this.address.setStartautoritet(startAuthority);
    }

    public void setCoName(String coName) {
        if (this.coname == null) {
            this.coname = new PersonAddressConameData();
        }
        this.coname.setConame(coName);
    }

    public void setMoveMunicipality(int authority, LocalDateTime fraflytningsdatoKommune,
                                    boolean fraflytningsdatoKommuneUsikkerhedsmarkering, int fraflytningskommunekode,
                                    LocalDateTime tilflytningsdatoKommune,
                                    boolean tilflytningsdatoKommuneUsikkerhedsmarkering) {
        if (this.moveMunicipality == null) {
            this.moveMunicipality = new PersonMoveMunicipalityData();
        }
        this.moveMunicipality.setAuthority(authority);
        this.moveMunicipality.setOutDatetime(fraflytningsdatoKommune);
        this.moveMunicipality.setOutDatetimeUncertain(fraflytningsdatoKommuneUsikkerhedsmarkering);
        this.moveMunicipality.setOutMunicipality(fraflytningskommunekode);
        this.moveMunicipality.setInDatetime(tilflytningsdatoKommune);
        this.moveMunicipality.setInDatetimeUncertain(tilflytningsdatoKommuneUsikkerhedsmarkering);
    }

    public void setName(int authority, String adresseringsnavn, String efternavn, String fornavne, String mellemnavn,
                        boolean efternavnMarkering, boolean fornavneMarkering, boolean mellemnavnMarkering,
                        String egetEfternavn, boolean ownLastNameMarking, boolean reportNames) {
        if (this.name == null) {
            this.name = new PersonNameData();
        }
        this.name.setAuthority(authority);
        this.name.setAddressingName(adresseringsnavn);
        this.name.setLastName(efternavn);
        this.name.setFirstNames(fornavne);
        this.name.setMiddleName(mellemnavn);


        this.name.setLastNameMarking(efternavnMarkering);
        this.name.setFirstNamesMarking(fornavneMarkering);
        this.name.setMiddleNameMarking(mellemnavnMarkering);

        this.name.setEgetEfternavn(egetEfternavn);
        this.name.setEgetEfternavnMarkering(ownLastNameMarking);
        this.name.setRapportnavne(reportNames);
    }

    public void setAddressName(int authority, String addressName) {
        if (this.addressingName == null) {
            this.addressingName = new PersonAddressNameData();
        }
        this.addressingName.setAuthority(authority);
        this.addressingName.setAddressName(addressName);
    }

    public void setNameVerification(int authority, boolean verification) {
        if (this.nameVerification == null) {
            this.nameVerification = new PersonNameVerificationData();
        }
        this.nameVerification.setAuthority(authority);
        this.nameVerification.setVerified(verification);
    }

    public void setNameAuthorityText(int authority, String text) {
        if (this.nameAuthority == null) {
            this.nameAuthority = new PersonNameAuthorityTextData();
        }
        this.nameAuthority.setAuthority(authority);
        this.nameAuthority.setText(text);
    }

    public void addProtection(int authority, int beskyttelsestype, boolean reportMarking) {
        for (PersonProtectionData existing : this.protection) {
            if (existing.getProtectionType() == beskyttelsestype && existing.getReportMarking() == reportMarking) {
                return;
            }
        }
        PersonProtectionData protection = new PersonProtectionData();
        protection.setAuthority(authority);
        protection.setProtectionType(beskyttelsestype);
        protection.setReportMarking(reportMarking);
        protection.setBaseData(this);
        this.protection.add(protection);
    }

    public void setEmigration(int authority, int countryCode) {
        if (this.migration == null) {
            this.migration = new PersonEmigrationData();
        }
        this.migration.setAuthority(authority);
        this.migration.setCountryCode(countryCode);
    }

    public void setForeignAddress(int authority, String adresselinie1, String adresselinie2, String adresselinie3, String adresselinie4, String adresselinie5) {
        if (this.foreignAddress == null) {
            this.foreignAddress = new PersonForeignAddressData();
        }
        this.foreignAddress.setAuthority(authority);
        this.foreignAddress.setAddressLine1(adresselinie1);
        this.foreignAddress.setAddressLine2(adresselinie2);
        this.foreignAddress.setAddressLine3(adresselinie3);
        this.foreignAddress.setAddressLine4(adresselinie4);
        this.foreignAddress.setAddressLine5(adresselinie5);
    }

    public void setCivilStatus(int authority, String civilStatus, String spouseCpr, LocalDate spouseBirthdate, boolean spouseBirthdateUncertain, String spouseName, boolean spouseNameMarking) {
        this.setCivilStatus(authority, civilStatus, spouseCpr, spouseBirthdate, spouseBirthdateUncertain, spouseName, spouseNameMarking, null);
    }

    public void setCivilStatus(int authority, String civilStatus, String spouseCpr, LocalDate spouseBirthdate, boolean spouseBirthdateUncertain, String spouseName, boolean spouseNameMarking, String correctionMarking) {
        if (this.civilStatus == null) {
            this.civilStatus = new PersonCivilStatusData();
        }
        this.civilStatus.setAuthority(authority);
        this.civilStatus.setCorrectionMarking(correctionMarking);
        this.civilStatus.setCivilStatus(civilStatus);
        this.civilStatus.setSpouseCpr(spouseCpr);
        this.civilStatus.setSpouseBirthdate(spouseBirthdate);
        this.civilStatus.setSpouseBirthdateUncertain(spouseBirthdateUncertain);
        this.civilStatus.setSpouseName(spouseName);
        this.civilStatus.setSpouseNameMarking(spouseNameMarking);
    }


    public void setCivilStatusVerification(int authority, boolean verification) {
        this.setCivilStatusVerification(authority, verification, null);
    }
    public void setCivilStatusVerification(int authority, boolean verification, String correctionMarking) {
        if (this.civilStatusVerification == null) {
            this.civilStatusVerification = new PersonCivilStatusVerificationData();
        }
        this.civilStatusVerification.setAuthority(authority);
        this.civilStatusVerification.setVerified(verification);
        this.civilStatusVerification.setCorrectionMarking(correctionMarking);
    }

    public void setCivilStatusAuthorityText(int authority, String text) {
        this.setCivilStatusAuthorityText(authority, text, null);
    }
    public void setCivilStatusAuthorityText(int authority, String text, String correctionMarking) {
        if (this.civilStatusAuthority == null) {
            this.civilStatusAuthority = new PersonCivilStatusAuthorityTextData();
        }
        this.civilStatusAuthority.setAuthority(authority);
        this.civilStatusAuthority.setText(text);
        this.civilStatusAuthority.setCorrectionMarking(correctionMarking);
    }

    public void setCprNumber(int authority, String cprNumber) {
        if (this.cprNumber == null) {
            this.cprNumber = new PersonNumberData();
        }
        this.cprNumber.setAuthority(authority);
        this.cprNumber.setCprNumber(cprNumber);
    }

    /**
     * Return a map of attributes, including those from the superclass
     * @return
     */
    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        if (this.coreData != null) {
            map.putAll(this.coreData.asMap());
        }
        if (this.status != null) {
            map.put("status", this.status.getStatus());
        }
        if (this.mother != null) {
            map.put("mother", this.mother);
        }
        if (this.father != null) {
            map.put("father", this.father);
        }
        if (this.motherVerification != null) {
            map.put("motherVerification", this.motherVerification);
        }
        if (this.fatherVerification != null) {
            map.put("fatherVerification", this.fatherVerification);
        }
        if (this.position != null) {
            map.put("position", this.position);
        }
        if (this.birth != null) {
            map.put("birth", this.birth);
        }
        if (this.address != null) {
            map.put("address", this.address);
        }
        if (this.name != null) {
            map.put("name", this.name);
        }
        if (this.addressingName != null) {
            map.put("addressingName", this.addressingName);
        }
        if (this.nameVerification != null) {
            map.put("nameVerification", this.nameVerification);
        }
        if (this.nameAuthority != null) {
            map.put("nameAuthority", this.nameAuthority);
        }
        if (this.protection != null) {
            map.put("protection", this.protection);
        }
        if (this.migration != null) {
            map.put("migration", this.migration);
        }
        if (this.foreignAddress != null) {
            map.put("foreignAddress", this.foreignAddress);
        }
        if (this.civilStatus != null) {
            map.put("civilStatus", this.civilStatus);
        }
        if (this.civilStatusVerification != null) {
            map.put("civilStatusVerification", this.civilStatusVerification);
        }
        if (this.civilStatusAuthority != null) {
            map.put("civilStatusAuthority", this.civilStatusAuthority);
        }
        if (this.cprNumber != null) {
            map.put("cprNumber", this.cprNumber);
        }

        return map;
    }


    @Override
    public LookupDefinition getLookupDefinition() {
        LookupDefinition lookupDefinition = new LookupDefinition(PersonBaseData.class);
        lookupDefinition.setMatchNulls(true);

        if (this.coreData != null) {
            lookupDefinition.putAll(DB_FIELD_CORE, this.coreData.databaseFields());
        }
        if (this.status != null) {
            lookupDefinition.putAll("status", this.status.databaseFields());
        }
        if (this.mother != null) {
            lookupDefinition.putAll("mother", this.mother.databaseFields());
        }
        if (this.father != null) {
            lookupDefinition.putAll("father", this.father.databaseFields());
        }
        if (this.motherVerification != null) {
            lookupDefinition.putAll("motherVerification", this.motherVerification.databaseFields());
        }
        if (this.fatherVerification != null) {
            lookupDefinition.putAll("fatherVerification", this.fatherVerification.databaseFields());
        }
        if (this.position != null) {
            lookupDefinition.putAll("position", this.position.databaseFields());
        }
        if (this.birth != null) {
            lookupDefinition.putAll("birth", this.birth.databaseFields());
        }
        if (this.address != null) {
            lookupDefinition.putAll("address", this.address.databaseFields());
        }
        if (this.coname != null) {
            lookupDefinition.putAll("coname", this.coname.databaseFields());
        }
        if (this.moveMunicipality != null) {
            lookupDefinition.putAll("moveMunicipality", this.moveMunicipality.databaseFields());
        }
        if (this.name != null) {
            lookupDefinition.putAll("name", this.name.databaseFields());
        }
        if (this.addressingName != null) {
            lookupDefinition.putAll("addressingName", this.addressingName.databaseFields());
        }
        if (this.nameVerification != null) {
            lookupDefinition.putAll("nameVerification", this.nameVerification.databaseFields());
        }
        if (this.nameAuthority != null) {
            lookupDefinition.putAll("nameAuthority", this.nameAuthority.databaseFields());
        }
        if (this.protection != null) {
            lookupDefinition.putAll("protection", DetailData.listDatabaseFields(this.protection));
        }
        if (this.migration != null) {
            lookupDefinition.putAll("migration", this.migration.databaseFields());
        }
        if (this.foreignAddress != null) {
            lookupDefinition.putAll("foreignAddress", this.foreignAddress.databaseFields());
        }
        if (this.civilStatus != null) {
            lookupDefinition.putAll("civilStatus", this.civilStatus.databaseFields());
        }
        if (this.civilStatusVerification != null) {
            lookupDefinition.putAll("civilStatusVerification", this.civilStatusVerification.databaseFields());
        }
        if (this.civilStatusAuthority != null) {
            lookupDefinition.putAll("civilStatusAuthority", this.civilStatusAuthority.databaseFields());
        }
        if (this.cprNumber != null) {
            lookupDefinition.putAll("cprNumber", this.cprNumber.databaseFields());
        }
        return lookupDefinition;
    }

    @Override
    public void forceLoad(Session session) {

    }
}

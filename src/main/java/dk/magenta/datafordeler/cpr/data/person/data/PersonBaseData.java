package dk.magenta.datafordeler.cpr.data.person.data;

import dk.magenta.datafordeler.core.database.LookupDefinition;
import dk.magenta.datafordeler.cpr.data.CprData;
import dk.magenta.datafordeler.cpr.data.person.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.Session;

/**
 * Created by lars on 16-05-17.
 */
@Entity
@Table(name="cpr_person_data")
public class PersonBaseData extends CprData<PersonEffect, PersonBaseData> {

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonCoreData coreData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonStatusData statusData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonParentData motherData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonParentData fatherData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonParentVerificationData motherVerificationData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonParentVerificationData fatherVerificationData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonPositionData positionData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonBirthData birthData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonAddressData addressData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonAddressConameData coNameData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonMoveMunicipalityData moveMunicipalityData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonNameData nameData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonAddressNameData addressNameData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonNameVerificationData nameVerificationData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonNameAuthorityTextData nameAuthorityTextData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonProtectionData protectionData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonEmigrationData emigrationData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonForeignAddressData foreignAddressData;


    public void setCurrentCprNumber(String personnummer) {
        if (this.coreData == null) {
            this.coreData = new PersonCoreData();
        }
        this.coreData.setPersonnummer(personnummer);
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

    public void setStatus(String status) {
        if (this.statusData == null) {
            this.statusData = new PersonStatusData();
        }
        this.statusData.setStatus(status);
    }

    public void setMother(String name, boolean nameMarking, String cprNumber, LocalDate birthDate, boolean birthDateUncertain, int authorityCode) {
        if (this.motherData == null) {
            this.motherData = new PersonParentData();
            this.motherData.setMother(true);
        }
        this.motherData.setName(name);
        this.motherData.setNameMarking(nameMarking);
        this.motherData.setCprNumber(cprNumber);
        this.motherData.setBirthDate(birthDate);
        this.motherData.setBirthDateUncertain(birthDateUncertain);
        this.motherData.setAuthority(authorityCode);
    }

    public void setFather(String name, boolean nameMarking, String cprNumber, LocalDate birthDate, boolean birthDateUncertain, int authorityCode) {
        if (this.fatherData == null) {
            this.fatherData = new PersonParentData();
            this.fatherData.setMother(false);
        }
        this.fatherData.setName(name);
        this.fatherData.setNameMarking(nameMarking);
        this.fatherData.setCprNumber(cprNumber);
        this.fatherData.setBirthDate(birthDate);
        this.fatherData.setBirthDateUncertain(birthDateUncertain);
        this.fatherData.setAuthority(authorityCode);
    }

    public void setMotherVerification(int authorityCode, boolean verified) {
        if (this.motherVerificationData == null) {
            this.motherVerificationData = new PersonParentVerificationData();
            this.motherVerificationData.setMother(true);
        }
        this.motherVerificationData.setAuthority(authorityCode);
        this.motherVerificationData.setVerified(verified);
    }

    public void setFatherVerification(int authorityCode, boolean verified) {
        if (this.fatherVerificationData == null) {
            this.fatherVerificationData = new PersonParentVerificationData();
            this.fatherVerificationData.setMother(false);
        }
        this.fatherVerificationData.setAuthority(authorityCode);
        this.fatherVerificationData.setVerified(verified);
    }

    public void setPosition(int authorityCode, String position) {
        if (this.positionData == null) {
            this.positionData = new PersonPositionData();
        }
        this.positionData.setAuthority(authorityCode);
        this.positionData.setPosition(position);
    }

    public void setBirth(LocalDateTime foedselsdato, boolean foedselsdatoUsikkerhedsmarkering,
                         String cprFoedselsregistreringsstedskode, String cprFoedselsregistreringsstedsnavn,
                         int foedselsraekkefoelge) {
        if (this.birthData == null) {
            this.birthData = new PersonBirthData();
        }
        this.birthData.setCprFoedselsregistreringsstedskode(cprFoedselsregistreringsstedskode);
        this.birthData.setCprFoedselsregistreringsstedsnavn(cprFoedselsregistreringsstedsnavn);
        this.birthData.setFoedselsdato(foedselsdato);
        this.birthData.setFoedselsdatoUsikkerhedsmarkering(foedselsdatoUsikkerhedsmarkering);

        this.birthData.setFoedselsraekkefoelge(foedselsraekkefoelge);
    }

    public void setAddress(int authority, String bygningsnummer, String bynavn, String cprKommunekode,
                           String cprKommunenavn, String cprVejkode, String darAdresse, String etage,
                           String husnummer, String postdistrikt, String postnummer, String sideDoer,
                           String adresselinie1, String adresselinie2, String adresselinie3, String adresselinie4,
                           String adresselinie5, int addressTextType, int startAuthority) {
        if (this.addressData == null) {
            this.addressData = new PersonAddressData();
        }
        this.addressData.setAuthority(authority);
        this.addressData.setBygningsnummer(bygningsnummer);
        this.addressData.setBynavn(bynavn);
        this.addressData.setCprKommunekode(cprKommunekode);
        this.addressData.setCprKommunenavn(cprKommunenavn);
        this.addressData.setCprVejkode(cprVejkode);
        this.addressData.setDarAdresse(darAdresse);
        this.addressData.setEtage(etage);
        this.addressData.setHusnummer(husnummer);
        this.addressData.setPostdistrikt(postdistrikt);
        this.addressData.setPostnummer(postnummer);
        this.addressData.setSideDoer(sideDoer);

        this.addressData.setAdresselinie1(adresselinie1);
        this.addressData.setAdresselinie2(adresselinie2);
        this.addressData.setAdresselinie3(adresselinie3);
        this.addressData.setAdresselinie4(adresselinie4);
        this.addressData.setAdresselinie5(adresselinie5);

        this.addressData.setAddressTextType(addressTextType);
        this.addressData.setStartAuthority(startAuthority);
    }

    public void setCoName(String coName) {
        if (this.coNameData == null) {
            this.coNameData = new PersonAddressConameData();
        }
        this.coNameData.setConavn(coName);
    }

    public void setMoveMunicipality(int authority, LocalDateTime fraflytningsdatoKommune,
                                    boolean fraflytningsdatoKommuneUsikkerhedsmarkering, int fraflytningskommunekode,
                                    LocalDateTime tilflytningsdatoKommune,
                                    boolean tilflytningsdatoKommuneUsikkerhedsmarkering) {
        if (this.moveMunicipalityData == null) {
            this.moveMunicipalityData = new PersonMoveMunicipalityData();
        }
        this.moveMunicipalityData.setAuthority(authority);
        this.moveMunicipalityData.setFraflytningsdatoKommune(fraflytningsdatoKommune);
        this.moveMunicipalityData.setFraflytningsdatoKommuneUsikkerhedsmarkering(fraflytningsdatoKommuneUsikkerhedsmarkering);
        this.moveMunicipalityData.setFraflytningskommunekode(fraflytningskommunekode);
        this.moveMunicipalityData.setTilflytningsdatoKommune(tilflytningsdatoKommune);
        this.moveMunicipalityData.setTilflytningsdatoKommuneUsikkerhedsmarkering(tilflytningsdatoKommuneUsikkerhedsmarkering);
    }

    public void setName(int authority, String adresseringsnavn, String efternavn, String fornavne, String mellemnavn,
                        boolean efternavnMarkering, boolean fornavneMarkering, boolean mellemnavnMarkering,
                        String egetEfternavn, boolean ownLastNameMarking, boolean reportNames) {
        if (this.nameData == null) {
            this.nameData = new PersonNameData();
        }
        this.nameData.setAuthority(authority);
        this.nameData.setAdresseringsnavn(adresseringsnavn);
        this.nameData.setEfternavn(efternavn);
        this.nameData.setFornavne(fornavne);
        this.nameData.setMellemnavn(mellemnavn);


        this.nameData.setEfternavnMarkering(efternavnMarkering);
        this.nameData.setFornavneMarkering(fornavneMarkering);
        this.nameData.setMellemnavnMarkering(mellemnavnMarkering);

        this.nameData.setEgetEfternavn(egetEfternavn);
        this.nameData.setEgetEfternavnMarkering(ownLastNameMarking);
        this.nameData.setReportNames(reportNames);
    }

    public void setAddressName(int authority, String addressName) {
        if (this.addressNameData == null) {
            this.addressNameData = new PersonAddressNameData();
        }
        this.addressNameData.setAuthority(authority);
        this.addressNameData.setAdressenavn(addressName);
        System.out.println("this.addressNameData: "+this.addressNameData);
    }

    public void setNameVerification(int authority, boolean verification) {
        if (this.nameVerificationData == null) {
            this.nameVerificationData = new PersonNameVerificationData();
        }
        this.nameVerificationData.setAuthority(authority);
        this.nameVerificationData.setVerification(verification);
    }

    public void setNameAuthorityText(int authority, String text) {
        if (this.nameAuthorityTextData == null) {
            this.nameAuthorityTextData = new PersonNameAuthorityTextData();
        }
        this.nameAuthorityTextData.setAuthority(authority);
        this.nameAuthorityTextData.setText(text);
    }

    public void setProtection(int authority, int beskyttelsestype, boolean reportMarking) {
        if (this.protectionData == null) {
            this.protectionData = new PersonProtectionData();
        }
        this.protectionData.setAuthority(authority);
        this.protectionData.setBeskyttelsestype(beskyttelsestype);
        this.protectionData.setReportMarking(reportMarking);
    }

    public void setEmigration(int authority, int countryCode) {
        if (this.emigrationData == null) {
            this.emigrationData = new PersonEmigrationData();
        }
        this.emigrationData.setAuthority(authority);
        this.emigrationData.setLandekode(countryCode);;
    }

    public void setForeignAddress(int authority, String adresselinie1, String adresselinie2, String adresselinie3, String adresselinie4, String adresselinie5) {
        if (this.foreignAddressData == null) {
            this.foreignAddressData = new PersonForeignAddressData();
        }
        this.foreignAddressData.setAuthority(authority);
        this.foreignAddressData.setAdresselinie1(adresselinie1);
        this.foreignAddressData.setAdresselinie2(adresselinie2);
        this.foreignAddressData.setAdresselinie3(adresselinie3);
        this.foreignAddressData.setAdresselinie4(adresselinie4);
        this.foreignAddressData.setAdresselinie5(adresselinie5);
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
        if (this.statusData != null) {
            map.put("status", this.statusData.getStatus());
        }
        if (this.motherData != null) {
            map.put("mor", this.motherData);
        }
        if (this.fatherData != null) {
            map.put("far", this.fatherData);
        }
        if (this.motherVerificationData != null) {
            map.put("morVerifikation", this.motherVerificationData);
        }
        if (this.fatherVerificationData != null) {
            map.put("farVerifikation", this.fatherVerificationData);
        }
        if (this.positionData != null) {
            map.put("position", this.positionData);
        }
        if (this.birthData != null) {
            map.put("fødsel", this.birthData);
        }
        if (this.addressData != null) {
            map.put("adresse", this.addressData);
        }
        if (this.nameData != null) {
            map.put("navn", this.nameData);
        }
        if (this.addressNameData != null) {
            map.put("addressNameData", this.addressNameData);
        }
        if (this.nameVerificationData != null) {
            map.put("navneverifikation", this.nameVerificationData);
        }
        if (this.nameAuthorityTextData != null) {
            map.put("navnemyndighed", this.nameAuthorityTextData);
        }
        if (this.protectionData != null) {
            map.put("beskyttelse", this.protectionData);
        }
        if (this.emigrationData != null) {
            map.put("udrejse", this.emigrationData);
        }
        if (this.foreignAddressData != null) {
            map.put("udlandsadresse", this.foreignAddressData);
        }

        return map;
    }


    @Override
    public LookupDefinition getLookupDefinition() {
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setMatchNulls(true);

        if (this.coreData != null) {
            lookupDefinition.putAll("coreData", this.coreData.databaseFields());
        }
        if (this.statusData != null) {
            lookupDefinition.putAll("statusData", this.statusData.databaseFields());
        }
        if (this.motherData != null) {
            lookupDefinition.putAll("motherData", this.motherData.databaseFields());
        }
        if (this.fatherData != null) {
            lookupDefinition.putAll("fatherData", this.fatherData.databaseFields());
        }
        if (this.motherVerificationData != null) {
            lookupDefinition.putAll("motherVerificationData", this.motherVerificationData.databaseFields());
        }
        if (this.fatherVerificationData != null) {
            lookupDefinition.putAll("fatherVerificationData", this.fatherVerificationData.databaseFields());
        }
        if (this.positionData != null) {
            lookupDefinition.putAll("positionData", this.positionData.databaseFields());
        }
        if (this.birthData != null) {
            lookupDefinition.putAll("birthData", this.birthData.databaseFields());
        }
        if (this.addressData != null) {
            lookupDefinition.putAll("addressData", this.addressData.databaseFields());
        }
        if (this.coNameData != null) {
            lookupDefinition.putAll("coNameData", this.coNameData.databaseFields());
        }
        if (this.moveMunicipalityData != null) {
            lookupDefinition.putAll("moveMunicipalityData", this.moveMunicipalityData.databaseFields());
        }
        if (this.nameData != null) {
            lookupDefinition.putAll("nameData", this.nameData.databaseFields());
        }
        if (this.addressNameData != null) {
            lookupDefinition.putAll("addressNameData", this.addressNameData.databaseFields());
        }
        if (this.nameVerificationData != null) {
            lookupDefinition.putAll("nameVerificationData", this.nameVerificationData.databaseFields());
        }
        if (this.nameAuthorityTextData != null) {
            lookupDefinition.putAll("nameAuthorityTextData", this.nameAuthorityTextData.databaseFields());
        }
        if (this.protectionData != null) {
            lookupDefinition.putAll("protectionData", this.protectionData.databaseFields());
        }
        if (this.emigrationData != null) {
            lookupDefinition.putAll("emigrationData", this.emigrationData.databaseFields());
        }
        if (this.foreignAddressData != null) {
            lookupDefinition.putAll("foreignAddressData", this.foreignAddressData.databaseFields());
        }
        return lookupDefinition;
    }

    @Override
    public void forceLoad(Session session) {

    }
}

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
    private PersonCoreData kerneData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonStatusData status;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonParentData mor;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonParentData far;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonParentVerificationData morVerifikation;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonParentVerificationData farVerifikation;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonPositionData stilling;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonBirthData foedsel;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonAddressData adresse;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonAddressConameData conavn;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonMoveMunicipalityData flytKommune;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonNameData navn;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonAddressNameData adressenavn;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonNameVerificationData navneverifikation;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonNameAuthorityTextData navnemyndighed;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonProtectionData beskyttelse;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonEmigrationData udrejseIndrejse;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonForeignAddressData udenlandsadresse;


    public PersonCoreData getKerneData() {
        return kerneData;
    }

    public PersonStatusData getStatus() {
        return status;
    }

    public PersonParentData getMor() {
        return mor;
    }

    public PersonParentData getFar() {
        return far;
    }

    public PersonParentVerificationData getMorVerifikation() {
        return morVerifikation;
    }

    public PersonParentVerificationData getFarVerifikation() {
        return farVerifikation;
    }

    public PersonPositionData getStilling() {
        return stilling;
    }

    public PersonBirthData getFoedsel() {
        return foedsel;
    }

    public PersonAddressData getAdresse() {
        return adresse;
    }

    public PersonAddressConameData getConavn() {
        return conavn;
    }

    public PersonMoveMunicipalityData getFlytKommune() {
        return flytKommune;
    }

    public PersonNameData getNavn() {
        return navn;
    }

    public PersonAddressNameData getAdressenavn() {
        return adressenavn;
    }

    public PersonNameVerificationData getNavneverifikation() {
        return navneverifikation;
    }

    public PersonNameAuthorityTextData getNavnemyndighed() {
        return navnemyndighed;
    }

    public PersonProtectionData getBeskyttelse() {
        return beskyttelse;
    }

    public PersonEmigrationData getUdrejseIndrejse() {
        return udrejseIndrejse;
    }

    public PersonForeignAddressData getUdenlandsadresse() {
        return udenlandsadresse;
    }

    public void setPersonnummer(String personnummer) {
        if (this.kerneData == null) {
            this.kerneData = new PersonCoreData();
        }
        this.kerneData.setPersonnummer(personnummer);
    }

    public void setKoen(String koen) {
        if (this.kerneData == null) {
            this.kerneData = new PersonCoreData();
        }
        this.kerneData.setKoen(koen);
    }

    public void setStartAuthority(int authority) {
        if (this.kerneData == null) {
            this.kerneData = new PersonCoreData();
        }
        this.kerneData.setMyndighed(authority);
    }

    public void setStatus(String status) {
        if (this.status == null) {
            this.status = new PersonStatusData();
        }
        this.status.setStatus(status);
    }

    public void setMother(String name, boolean nameMarking, String cprNumber, LocalDate birthDate, boolean birthDateUncertain, int authorityCode) {
        if (this.mor == null) {
            this.mor = new PersonParentData();
            this.mor.setMother(true);
        }
        this.mor.setNavn(name);
        this.mor.setNavneMarkering(nameMarking);
        this.mor.setCprNumber(cprNumber);
        this.mor.setBirthDate(birthDate);
        this.mor.setBirthDateUncertain(birthDateUncertain);
        this.mor.setMyndighed(authorityCode);
    }

    public void setFather(String name, boolean nameMarking, String cprNumber, LocalDate birthDate, boolean birthDateUncertain, int authorityCode) {
        if (this.far == null) {
            this.far = new PersonParentData();
            this.far.setMother(false);
        }
        this.far.setNavn(name);
        this.far.setNavneMarkering(nameMarking);
        this.far.setCprNumber(cprNumber);
        this.far.setBirthDate(birthDate);
        this.far.setBirthDateUncertain(birthDateUncertain);
        this.far.setMyndighed(authorityCode);
    }

    public void setMotherVerification(int authorityCode, boolean verified) {
        if (this.morVerifikation == null) {
            this.morVerifikation = new PersonParentVerificationData();
            this.morVerifikation.setMor(true);
        }
        this.morVerifikation.setMyndighed(authorityCode);
        this.morVerifikation.setVerificeret(verified);
    }

    public void setFatherVerification(int authorityCode, boolean verified) {
        if (this.farVerifikation == null) {
            this.farVerifikation = new PersonParentVerificationData();
            this.farVerifikation.setMor(false);
        }
        this.farVerifikation.setMyndighed(authorityCode);
        this.farVerifikation.setVerificeret(verified);
    }

    public void setPosition(int authorityCode, String position) {
        if (this.stilling == null) {
            this.stilling = new PersonPositionData();
        }
        this.stilling.setMyndighed(authorityCode);
        this.stilling.setStilling(position);
    }

    public void setBirth(LocalDateTime foedselsdato, boolean foedselsdatoUsikkerhedsmarkering,
                         String cprFoedselsregistreringsstedskode, String cprFoedselsregistreringsstedsnavn,
                         int foedselsraekkefoelge) {
        if (this.foedsel == null) {
            this.foedsel = new PersonBirthData();
        }
        this.foedsel.setCprFoedselsregistreringsstedskode(cprFoedselsregistreringsstedskode);
        this.foedsel.setCprFoedselsregistreringsstedsnavn(cprFoedselsregistreringsstedsnavn);
        this.foedsel.setFoedselsdato(foedselsdato);
        this.foedsel.setFoedselsdatoUsikkerhedsmarkering(foedselsdatoUsikkerhedsmarkering);

        this.foedsel.setFoedselsraekkefoelge(foedselsraekkefoelge);
    }

    public void setAddress(int authority, String bygningsnummer, String bynavn, String cprKommunekode,
                           String cprKommunenavn, String cprVejkode, String darAdresse, String etage,
                           String husnummer, String postdistrikt, String postnummer, String sideDoer,
                           String adresselinie1, String adresselinie2, String adresselinie3, String adresselinie4,
                           String adresselinie5, int addressTextType, int startAuthority) {
        if (this.adresse == null) {
            this.adresse = new PersonAddressData();
        }
        this.adresse.setMyndighed(authority);
        this.adresse.setBygningsnummer(bygningsnummer);
        this.adresse.setBynavn(bynavn);
        this.adresse.setCprKommunekode(cprKommunekode);
        this.adresse.setCprKommunenavn(cprKommunenavn);
        this.adresse.setCprVejkode(cprVejkode);
        this.adresse.setDarAdresse(darAdresse);
        this.adresse.setEtage(etage);
        this.adresse.setHusnummer(husnummer);
        this.adresse.setPostdistrikt(postdistrikt);
        this.adresse.setPostnummer(postnummer);
        this.adresse.setSideDoer(sideDoer);

        this.adresse.setAdresselinie1(adresselinie1);
        this.adresse.setAdresselinie2(adresselinie2);
        this.adresse.setAdresselinie3(adresselinie3);
        this.adresse.setAdresselinie4(adresselinie4);
        this.adresse.setAdresselinie5(adresselinie5);

        this.adresse.setAdressetekststype(addressTextType);
        this.adresse.setStartautoritet(startAuthority);
    }

    public void setCoName(String coName) {
        if (this.conavn == null) {
            this.conavn = new PersonAddressConameData();
        }
        this.conavn.setConavn(coName);
    }

    public void setMoveMunicipality(int authority, LocalDateTime fraflytningsdatoKommune,
                                    boolean fraflytningsdatoKommuneUsikkerhedsmarkering, int fraflytningskommunekode,
                                    LocalDateTime tilflytningsdatoKommune,
                                    boolean tilflytningsdatoKommuneUsikkerhedsmarkering) {
        if (this.flytKommune == null) {
            this.flytKommune = new PersonMoveMunicipalityData();
        }
        this.flytKommune.setMyndighed(authority);
        this.flytKommune.setFraflytningsdatoKommune(fraflytningsdatoKommune);
        this.flytKommune.setFraflytningsdatoKommuneUsikkerhedsmarkering(fraflytningsdatoKommuneUsikkerhedsmarkering);
        this.flytKommune.setFraflytningskommunekode(fraflytningskommunekode);
        this.flytKommune.setTilflytningsdatoKommune(tilflytningsdatoKommune);
        this.flytKommune.setTilflytningsdatoKommuneUsikkerhedsmarkering(tilflytningsdatoKommuneUsikkerhedsmarkering);
    }

    public void setName(int authority, String adresseringsnavn, String efternavn, String fornavne, String mellemnavn,
                        boolean efternavnMarkering, boolean fornavneMarkering, boolean mellemnavnMarkering,
                        String egetEfternavn, boolean ownLastNameMarking, boolean reportNames) {
        if (this.navn == null) {
            this.navn = new PersonNameData();
        }
        this.navn.setMyndighed(authority);
        this.navn.setAdresseringsnavn(adresseringsnavn);
        this.navn.setEfternavn(efternavn);
        this.navn.setFornavne(fornavne);
        this.navn.setMellemnavn(mellemnavn);


        this.navn.setEfternavnMarkering(efternavnMarkering);
        this.navn.setFornavneMarkering(fornavneMarkering);
        this.navn.setMellemnavnMarkering(mellemnavnMarkering);

        this.navn.setEgetEfternavn(egetEfternavn);
        this.navn.setEgetEfternavnMarkering(ownLastNameMarking);
        this.navn.setRapportnavne(reportNames);
    }

    public void setAddressName(int authority, String addressName) {
        if (this.adressenavn == null) {
            this.adressenavn = new PersonAddressNameData();
        }
        this.adressenavn.setMyndighed(authority);
        this.adressenavn.setAdressenavn(addressName);
    }

    public void setNameVerification(int authority, boolean verification) {
        if (this.navneverifikation == null) {
            this.navneverifikation = new PersonNameVerificationData();
        }
        this.navneverifikation.setMyndighed(authority);
        this.navneverifikation.setVerificeret(verification);
    }

    public void setNameAuthorityText(int authority, String text) {
        if (this.navnemyndighed == null) {
            this.navnemyndighed = new PersonNameAuthorityTextData();
        }
        this.navnemyndighed.setMyndighed(authority);
        this.navnemyndighed.setTekst(text);
    }

    public void setProtection(int authority, int beskyttelsestype, boolean reportMarking) {
        if (this.beskyttelse == null) {
            this.beskyttelse = new PersonProtectionData();
        }
        this.beskyttelse.setMyndighed(authority);
        this.beskyttelse.setBeskyttelsestype(beskyttelsestype);
        this.beskyttelse.setRapportMarkering(reportMarking);
    }

    public void setEmigration(int authority, int countryCode) {
        if (this.udrejseIndrejse == null) {
            this.udrejseIndrejse = new PersonEmigrationData();
        }
        this.udrejseIndrejse.setMyndighed(authority);
        this.udrejseIndrejse.setLandekode(countryCode);;
    }

    public void setForeignAddress(int authority, String adresselinie1, String adresselinie2, String adresselinie3, String adresselinie4, String adresselinie5) {
        if (this.udenlandsadresse == null) {
            this.udenlandsadresse = new PersonForeignAddressData();
        }
        this.udenlandsadresse.setMyndighed(authority);
        this.udenlandsadresse.setAdresselinie1(adresselinie1);
        this.udenlandsadresse.setAdresselinie2(adresselinie2);
        this.udenlandsadresse.setAdresselinie3(adresselinie3);
        this.udenlandsadresse.setAdresselinie4(adresselinie4);
        this.udenlandsadresse.setAdresselinie5(adresselinie5);
    }

    /**
     * Return a map of attributes, including those from the superclass
     * @return
     */
    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        if (this.kerneData != null) {
            map.putAll(this.kerneData.asMap());
        }
        if (this.status != null) {
            map.put("status", this.status.getStatus());
        }
        if (this.mor != null) {
            map.put("mor", this.mor);
        }
        if (this.far != null) {
            map.put("far", this.far);
        }
        if (this.morVerifikation != null) {
            map.put("morVerifikation", this.morVerifikation);
        }
        if (this.farVerifikation != null) {
            map.put("farVerifikation", this.farVerifikation);
        }
        if (this.stilling != null) {
            map.put("stilling", this.stilling);
        }
        if (this.foedsel != null) {
            map.put("foedsel", this.foedsel);
        }
        if (this.adresse != null) {
            map.put("adresse", this.adresse);
        }
        if (this.navn != null) {
            map.put("navn", this.navn);
        }
        if (this.adressenavn != null) {
            map.put("adressenavn", this.adressenavn);
        }
        if (this.navneverifikation != null) {
            map.put("navneverifikation", this.navneverifikation);
        }
        if (this.navnemyndighed != null) {
            map.put("navnemyndighed", this.navnemyndighed);
        }
        if (this.beskyttelse != null) {
            map.put("beskyttelse", this.beskyttelse);
        }
        if (this.udrejseIndrejse != null) {
            map.put("udrejseIndrejse", this.udrejseIndrejse);
        }
        if (this.udenlandsadresse != null) {
            map.put("udenlandsadresse", this.udenlandsadresse);
        }

        return map;
    }


    @Override
    public LookupDefinition getLookupDefinition() {
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setMatchNulls(true);

        if (this.kerneData != null) {
            lookupDefinition.putAll("kerneData", this.kerneData.databaseFields());
        }
        if (this.status != null) {
            lookupDefinition.putAll("status", this.status.databaseFields());
        }
        if (this.mor != null) {
            lookupDefinition.putAll("mor", this.mor.databaseFields());
        }
        if (this.far != null) {
            lookupDefinition.putAll("far", this.far.databaseFields());
        }
        if (this.morVerifikation != null) {
            lookupDefinition.putAll("morVerifikation", this.morVerifikation.databaseFields());
        }
        if (this.farVerifikation != null) {
            lookupDefinition.putAll("farVerifikation", this.farVerifikation.databaseFields());
        }
        if (this.stilling != null) {
            lookupDefinition.putAll("stilling", this.stilling.databaseFields());
        }
        if (this.foedsel != null) {
            lookupDefinition.putAll("foedsel", this.foedsel.databaseFields());
        }
        if (this.adresse != null) {
            lookupDefinition.putAll("adresse", this.adresse.databaseFields());
        }
        if (this.conavn != null) {
            lookupDefinition.putAll("conavn", this.conavn.databaseFields());
        }
        if (this.flytKommune != null) {
            lookupDefinition.putAll("flytKommune", this.flytKommune.databaseFields());
        }
        if (this.navn != null) {
            lookupDefinition.putAll("navn", this.navn.databaseFields());
        }
        if (this.adressenavn != null) {
            lookupDefinition.putAll("adressenavn", this.adressenavn.databaseFields());
        }
        if (this.navneverifikation != null) {
            lookupDefinition.putAll("navneverifikation", this.navneverifikation.databaseFields());
        }
        if (this.navnemyndighed != null) {
            lookupDefinition.putAll("navnemyndighed", this.navnemyndighed.databaseFields());
        }
        if (this.beskyttelse != null) {
            lookupDefinition.putAll("beskyttelse", this.beskyttelse.databaseFields());
        }
        if (this.udrejseIndrejse != null) {
            lookupDefinition.putAll("udrejseIndrejse", this.udrejseIndrejse.databaseFields());
        }
        if (this.udenlandsadresse != null) {
            lookupDefinition.putAll("udenlandsadresse", this.udenlandsadresse.databaseFields());
        }
        return lookupDefinition;
    }

    @Override
    public void forceLoad(Session session) {

    }
}

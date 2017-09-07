package dk.magenta.datafordeler.cpr.data.person.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 22-06-17.
 */
@Entity
@Table(name = "cpr_person_address")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PersonAddressData extends AuthorityDetailData {

    @Column
    @JsonProperty(value = "bygningsnummer")
    @XmlElement(name = "bygningsnummer")
    private String bygningsnummer;

    public String getBygningsnummer() {
        return this.bygningsnummer;
    }

    public void setBygningsnummer(String bygningsnummer) {
        this.bygningsnummer = bygningsnummer;
    }



    @Column
    @JsonProperty(value = "bynavn")
    @XmlElement(name = "bynavn")
    private String bynavn;

    public String getBynavn() {
        return this.bynavn;
    }

    public void setBynavn(String bynavn) {
        this.bynavn = bynavn;
    }



    @Column
    @JsonProperty(value = "cprKommunekode")
    @XmlElement(name = "cprKommunekode")
    private String cprKommunekode;

    public String getCprKommunekode() {
        return this.cprKommunekode;
    }

    public void setCprKommunekode(String cprKommunekode) {
        this.cprKommunekode = cprKommunekode;
    }



    @Column
    @JsonProperty(value = "cprKommunenavn")
    @XmlElement(name = "cprKommunenavn")
    private String cprKommunenavn;

    public String getCprKommunenavn() {
        return this.cprKommunenavn;
    }

    public void setCprKommunenavn(String cprKommunenavn) {
        this.cprKommunenavn = cprKommunenavn;
    }



    @Column
    @JsonProperty(value = "cprVejkode")
    @XmlElement(name = "cprVejkode")
    private String cprVejkode;

    public String getCprVejkode() { return this.cprVejkode; }

    public void setCprVejkode(String cprVejkode) {
        this.cprVejkode = cprVejkode;
    }



    @Column
    @JsonProperty(value = "darAdresse")
    @XmlElement(name = "darAdresse")
    private String darAdresse;

    public String getDarAdresse() {
        return this.darAdresse;
    }

    public void setDarAdresse(String darAdresse) {
        this.darAdresse = darAdresse;
    }



    @Column
    @JsonProperty(value = "etage")
    @XmlElement(name = "etage")
    private String etage;

    public String getEtage() {
        return this.etage;
    }

    public void setEtage(String etage) {
        this.etage = etage;
    }



    @Column
    @JsonProperty(value = "husnummer")
    @XmlElement(name = "husnummer")
    private String husnummer;

    public String getHusnummer() {
        return this.husnummer;
    }

    public void setHusnummer(String husnummer) {
        this.husnummer = husnummer;
    }



    @Column
    @JsonProperty(value = "postdistrikt")
    @XmlElement(name = "postdistrikt")
    private String postdistrikt;

    public String getPostdistrikt() {
        return this.postdistrikt;
    }

    public void setPostdistrikt(String postdistrikt) {
        this.postdistrikt = postdistrikt;
    }



    @Column
    @JsonProperty(value = "postnummer")
    @XmlElement(name = "postnummer")
    private String postnummer;

    public String getPostnummer() {
        return this.postnummer;
    }

    public void setPostnummer(String postnummer) {
        this.postnummer = postnummer;
    }



    @Column
    @JsonProperty(value = "sideDoer")
    @XmlElement(name = "sideDoer")
    private String sideDoer;

    public String getSideDoer() {
        return this.sideDoer;
    }

    public void setSideDoer(String sideDoer) {
        this.sideDoer = sideDoer;
    }



    @Column
    @JsonProperty(value = "vejadresseringsnavn")
    @XmlElement(name = "vejadresseringsnavn")
    private String vejadresseringsnavn;

    public String getVejadresseringsnavn() {
        return this.vejadresseringsnavn;
    }

    public void setVejadresseringsnavn(String vejadresseringsnavn) {
        this.vejadresseringsnavn = vejadresseringsnavn;
    }


    @Column
    @JsonProperty(value = "adresselinie1")
    @XmlElement(name = "adresselinie1")
    private String adresselinie1;

    public String getAdresselinie1() {
        return this.adresselinie1;
    }

    public void setAdresselinie1(String adresselinie1) {
        this.adresselinie1 = adresselinie1;
    }



    @Column
    @JsonProperty(value = "adresselinie2")
    @XmlElement(name = "adresselinie2")
    private String adresselinie2;

    public String getAdresselinie2() {
        return this.adresselinie2;
    }

    public void setAdresselinie2(String adresselinie2) {
        this.adresselinie2 = adresselinie2;
    }



    @Column
    @JsonProperty(value = "adresselinie3")
    @XmlElement(name = "adresselinie3")
    private String adresselinie3;

    public String getAdresselinie3() {
        return this.adresselinie3;
    }

    public void setAdresselinie3(String adresselinie3) {
        this.adresselinie3 = adresselinie3;
    }



    @Column
    @JsonProperty(value = "adresselinie4")
    @XmlElement(name = "adresselinie4")
    private String adresselinie4;

    public String getAdresselinie4() {
        return this.adresselinie4;
    }

    public void setAdresselinie4(String adresselinie4) {
        this.adresselinie4 = adresselinie4;
    }



    @Column
    @JsonProperty(value = "adresselinie5")
    @XmlElement(name = "adresselinie5")
    private String adresselinie5;

    public String getAdresselinie5() {
        return this.adresselinie5;
    }

    public void setAdresselinie5(String adresselinie5) {
        this.adresselinie5 = adresselinie5;
    }



    //Ikke i grunddatamodellen

    @Transient
    private int adressetekststype;

    public int getAdressetekststype() {
        return this.adressetekststype;
    }

    public void setAdressetekststype(int adressetekststype) {
        this.adressetekststype = adressetekststype;
    }


    @Transient
    private int startautoritet;

    public int getStartautoritet() {
        return this.startautoritet;
    }

    public void setStartautoritet(int startautoritet) {
        this.startautoritet = startautoritet;
    }


    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>(super.asMap());
        //CprAdresse
        map.put("bygningsnummer", this.bygningsnummer);
        map.put("bynavn", this.bynavn);
        map.put("cprKommunekode", this.cprKommunekode);
        map.put("cprKommunenavn", this.cprKommunenavn);
        map.put("cprVejkode", this.cprVejkode);
        map.put("darAdresse", this.darAdresse);
        map.put("etage", this.etage);
        map.put("husnummer", this.husnummer);
        map.put("postdistrikt", this.postdistrikt);
        map.put("postnummer", this.postnummer);
        map.put("sideDoer", this.sideDoer);
        map.put("vejadresseringsnavn", this.vejadresseringsnavn);

        //SimpelAdresse
        map.put("adresselinie1", this.adresselinie1);
        map.put("adresselinie2", this.adresselinie2);
        map.put("adresselinie3", this.adresselinie3);
        map.put("adresselinie4", this.adresselinie4);
        map.put("adresselinie5", this.adresselinie5);

        //Ikke i grunddatamodellen
        //map.put("adressetekststype", this.adressetekststype);
        //map.put("startautoritet", this.startautoritet);
        return map;
    }
}

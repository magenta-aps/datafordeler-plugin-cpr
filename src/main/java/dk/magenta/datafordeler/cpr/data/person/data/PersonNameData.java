package dk.magenta.datafordeler.cpr.data.person.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 22-06-17.
 */
@Entity
@Table(name = "cpr_person_name")
public class PersonNameData extends AuthorityDetailData {


    @Column
    @JsonProperty(value = "adresseringsnavn")
    @XmlElement(name = "adresseringsnavn")
    private String adresseringsnavn;

    public String getAdresseringsnavn() {
        return this.adresseringsnavn;
    }

    public void setAdresseringsnavn(String adresseringsnavn) {
        this.adresseringsnavn = adresseringsnavn;
    }


    @Column
    @JsonProperty(value = "fornavne")
    @XmlElement(name = "fornavne")
    private String fornavne;

    public String getFornavne() {
        return this.fornavne;
    }

    public void setFornavne(String fornavne) {
        this.fornavne = fornavne;
    }

    @Column
    @JsonProperty(value = "fornavneMarkering")
    @XmlElement(name = "fornavneMarkering")
    private boolean fornavneMarkering;

    public boolean isFornavneMarkering() {
        return this.fornavneMarkering;
    }

    public void setFornavneMarkering(boolean fornavneMarkering) {
        this.fornavneMarkering = fornavneMarkering;
    }

    @Column
    @JsonProperty(value = "mellemnavn")
    @XmlElement(name = "mellemnavn")
    private String mellemnavn;

    public String getMellemnavn() {
        return this.mellemnavn;
    }

    public void setMellemnavn(String mellemnavn) {
        this.mellemnavn = mellemnavn;
    }

    @Column
    @JsonProperty(value = "mellemnavnMarkering")
    @XmlElement(name = "mellemnavnMarkering")
    private boolean mellemnavnMarkering;

    public boolean isMellemnavnMarkering() {
        return this.mellemnavnMarkering;
    }

    public void setMellemnavnMarkering(boolean mellemnavnMarkering) {
        this.mellemnavnMarkering = mellemnavnMarkering;
    }

    @Column
    @JsonProperty(value = "efternavn")
    @XmlElement(name = "efternavn")
    private String efternavn;

    public String getEfternavn() {
        return this.efternavn;
    }

    public void setEfternavn(String efternavn) {
        this.efternavn = efternavn;
    }

    @Column
    @JsonProperty(value = "efternavnMarkering")
    @XmlElement(name = "efternavnMarkering")
    private boolean efternavnMarkering;

    public boolean isEfternavnMarkering() {
        return this.efternavnMarkering;
    }

    public void setEfternavnMarkering(boolean efternavnMarkering) {
        this.efternavnMarkering = efternavnMarkering;
    }


    //Ikke i grunddatamodellen

    private String egetEfternavn;

    public String getEgetEfternavn() {
        return this.egetEfternavn;
    }

    public void setEgetEfternavn(String egetEfternavn) {
        this.egetEfternavn = egetEfternavn;
    }


    private boolean egetEfternavnMarkering;

    public boolean isEgetEfternavnMarkering() {
        return this.egetEfternavnMarkering;
    }

    public void setEgetEfternavnMarkering(boolean egetEfternavnMarkering) {
        this.egetEfternavnMarkering = egetEfternavnMarkering;
    }


    @Column
    @JsonProperty
    @XmlElement
    private boolean reportNames;

    public boolean isReportNames() {
        return this.reportNames;
    }

    public void setReportNames(boolean reportNames) {
        this.reportNames = reportNames;
    }

    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>(super.asMap());
        //Navn
        map.put("adresseringsnavn", this.adresseringsnavn);
        map.put("efternavn", this.efternavn);
        map.put("fornavne", this.fornavne);
        map.put("mellemnavn", this.mellemnavn);

        //NavneMarkering
        map.put("efternavnMarkering", this.efternavnMarkering);
        map.put("fornavneMarkering", this.fornavneMarkering);
        map.put("mellemnavnMarkering", this.mellemnavnMarkering);

        //Ikke i grunddatamodellen
        map.put("reportNames", this.reportNames);
        map.put("egetEfternavnMarkering", this.egetEfternavnMarkering);
        map.put("egetEfternavn", this.egetEfternavn);

        //OBS: Virkning fra og til mangler i forhold til grunddatamodellen
        return map;
    }
}

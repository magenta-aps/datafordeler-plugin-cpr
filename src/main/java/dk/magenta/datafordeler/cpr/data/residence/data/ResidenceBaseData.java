package dk.magenta.datafordeler.cpr.data.residence.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.data.CprData;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceEffect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.Session;

/**
 * Created by lars on 29-06-17.
 */
@Entity
@Table(name="cpr_residence_data")
public class ResidenceBaseData extends CprData<ResidenceEffect, ResidenceBaseData> {

    @Column
    @JsonProperty(value = "kommunekode")
    @XmlElement(name = "kommuneKode")
    private int kommunekode;

    public int getKommunekode() {
        return this.kommunekode;
    }

    public void setKommunekode(int kommunekode) {
        this.kommunekode = kommunekode;
    }

    @Column
    @JsonProperty(value = "vejkode")
    @XmlElement(name = "vejkode")
    private int vejkode;

    public int getVejkode() {
        return this.vejkode;
    }

    public void setVejkode(int vejkode) {
        this.vejkode = vejkode;
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
    @JsonProperty(value = "lokalitet")
    @XmlElement(name = "lokalitet")
    private String lokalitet;

    public String getLokalitet() {
        return this.lokalitet;
    }

    public void setLokalitet(String lokalitet) {
        this.lokalitet = lokalitet;
    }

    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("kommunekode", this.kommunekode);
        map.put("vejkode", this.vejkode);
        map.put("husnummer", this.husnummer);
        map.put("etage", this.etage);
        map.put("sideDoer", this.sideDoer);
        map.put("lokalitet", this.lokalitet);
        return map;
    }

    @Override
    public void forceLoad(Session session) {

    }
}

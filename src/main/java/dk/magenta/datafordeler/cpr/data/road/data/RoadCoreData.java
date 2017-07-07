package dk.magenta.datafordeler.cpr.data.road.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.data.DetailData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 16-05-17.
 */
@Entity
@Table(name="cpr_road_core")
public class RoadCoreData extends DetailData {


    @Column
    @JsonProperty(value = "tilKommunekode")
    @XmlElement(name = "tilKommunekode")
    private int tilKommunekode;

    public int getTilKommunekode() {
        return this.tilKommunekode;
    }

    public void setTilKommunekode(int tilKommunekode) {
        this.tilKommunekode = tilKommunekode;
    }

    @Column
    @JsonProperty(value = "tilVejkode")
    @XmlElement(name = "tilVejkode")
    private int tilVejkode;

    public int getTilVejkode() {
        return this.tilVejkode;
    }

    public void setTilVejkode(int tilVejkode) {
        this.tilVejkode = tilVejkode;
    }

    @Column
    @JsonProperty(value = "fraKommunekode")
    @XmlElement(name = "fraKommunekode")
    private int fraKommunekode;

    public int getFraKommunekode() {
        return this.fraKommunekode;
    }

    public void setFraKommunekode(int fraKommunekode) {
        this.fraKommunekode = fraKommunekode;
    }

    @Column
    @JsonProperty(value = "fraVejkode")
    @XmlElement(name = "fraVejkode")
    private int fraVejkode;

    public int getFraVejkode() {
        return this.fraVejkode;
    }

    public void setFraVejkode(int fraVejkode) {
        this.fraVejkode = fraVejkode;
    }

    @Column
    @JsonProperty(value = "addresseringsnavn")
    @XmlElement(name = "addresseringsnavn")
    private String addresseringsnavn;

    public String getAddresseringsnavn() {
        return this.addresseringsnavn;
    }

    public void setAddresseringsnavn(String addresseringsnavn) {
        this.addresseringsnavn = addresseringsnavn;
    }

    @Column
    @JsonProperty(value = "vejnavn")
    @XmlElement(name = "vejnavn")
    private String vejnavn;

    public String getVejnavn() {
        return this.vejnavn;
    }

    public void setVejnavn(String vejnavn) {
        this.vejnavn = vejnavn;
    }

    /**
     * Return a map of attributes, including those from the superclass
     * @return
     */
    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();

        if (this.tilKommunekode != 0) {
            map.put("tilKommunekode", this.tilKommunekode);
        }
        if (this.tilVejkode != 0) {
            map.put("tilVejkode", this.tilVejkode);
        }
        if (this.fraKommunekode != 0) {
            map.put("fraKommunekode", this.fraKommunekode);
        }
        if (this.fraVejkode != 0) {
            map.put("fraVejkode", this.fraVejkode);
        }
        if (this.addresseringsnavn != null) {
            map.put("addresseringsnavn", this.addresseringsnavn);
        }
        if (this.vejnavn != null) {
            map.put("vejnavn", this.vejnavn);
        }
        return map;
    }

}

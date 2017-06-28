package dk.magenta.datafordeler.cpr.data.road.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.core.database.LookupDefinition;
import dk.magenta.datafordeler.cpr.data.CprData;
import dk.magenta.datafordeler.cpr.data.DetailData;
import dk.magenta.datafordeler.cpr.data.road.RoadEffect;

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
    @JsonProperty(value = "tilKommuneKode")
    @XmlElement(name = "tilKommuneKode")
    private int toMunicipalityCode;

    public int getToMunicipalityCode() {
        return this.toMunicipalityCode;
    }

    public void setToMunicipalityCode(int toMunicipalityCode) {
        this.toMunicipalityCode = toMunicipalityCode;
    }

    @Column
    @JsonProperty(value = "tilVejKode")
    @XmlElement(name = "tilVejKode")
    private int toRoadCode;

    public int getToRoadCode() {
        return this.toRoadCode;
    }

    public void setToRoadCode(int toRoadCode) {
        this.toRoadCode = toRoadCode;
    }

    @Column
    @JsonProperty(value = "fraKommuneKode")
    @XmlElement(name = "fraKommuneKode")
    private int fromMunicipalityCode;

    public int getFromMunicipalityCode() {
        return this.fromMunicipalityCode;
    }

    public void setFromMunicipalityCode(int fromMunicipalityCode) {
        this.fromMunicipalityCode = fromMunicipalityCode;
    }

    @Column
    @JsonProperty(value = "fraVejKode")
    @XmlElement(name = "fraVejKode")
    private int fromRoadCode;

    public int getFromRoadCode() {
        return this.fromRoadCode;
    }

    public void setFromRoadCode(int fromRoadCode) {
        this.fromRoadCode = fromRoadCode;
    }

    @Column
    @JsonProperty(value = "addresseringsNavn")
    @XmlElement(name = "addresseringsNavn")
    private String addressingName;

    public String getAddressingName() {
        return this.addressingName;
    }

    public void setAddressingName(String addressingName) {
        this.addressingName = addressingName;
    }

    @Column
    @JsonProperty(value = "vejNavn")
    @XmlElement(name = "vejNavn")
    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return a map of attributes, including those from the superclass
     * @return
     */
    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();

        if (this.toMunicipalityCode != 0) {
            map.put("toMunicipalityCode", this.toMunicipalityCode);
        }
        if (this.toRoadCode != 0) {
            map.put("toRoadCode", this.toRoadCode);
        }
        if (this.fromMunicipalityCode != 0) {
            map.put("fromMunicipalityCode", this.fromMunicipalityCode);
        }
        if (this.fromRoadCode != 0) {
            map.put("fromRoadCode", this.fromRoadCode);
        }
        if (this.addressingName != null) {
            map.put("addressingName", this.addressingName);
        }
        if (this.name != null) {
            map.put("name", this.name);
        }
        return map;
    }

}

package dk.magenta.datafordeler.cpr.data.residence.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.data.CprData;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceEffect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 29-06-17.
 */
@Entity
@Table(name="cpr_residence_data")
public class ResidenceBaseData extends CprData<ResidenceEffect, ResidenceBaseData> {

    @Column
    @JsonProperty(value = "kommuneKode")
    @XmlElement(name = "kommuneKode")
    private int municipalityCode;

    public int getMunicipalityCode() {
        return this.municipalityCode;
    }

    public void setMunicipalityCode(int municipalityCode) {
        this.municipalityCode = municipalityCode;
    }

    @Column
    @JsonProperty(value = "vejKode")
    @XmlElement(name = "vejKode")
    private int roadCode;

    public int getRoadCode() {
        return this.roadCode;
    }

    public void setRoadCode(int roadCode) {
        this.roadCode = roadCode;
    }

    @Column
    @JsonProperty(value = "husnummer")
    @XmlElement(name = "husnummer")
    private String houseNumber;

    public String getHouseNumber() {
        return this.houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    @Column
    @JsonProperty(value = "etage")
    @XmlElement(name = "etage")
    private String floor;

    public String getFloor() {
        return this.floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    @Column
    @JsonProperty(value = "sidedør")
    @XmlElement(name = "sidedør")
    private String door;

    public String getDoor() {
        return this.door;
    }

    public void setDoor(String door) {
        this.door = door;
    }

    @Column
    @JsonProperty(value = "lokalitet")
    @XmlElement(name = "lokalitet")
    private String location;

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("municipalityCode", this.municipalityCode);
        map.put("roadCode", this.roadCode);
        map.put("houseNumber", this.houseNumber);
        map.put("floor", this.floor);
        map.put("door", this.door);
        map.put("location", this.location);
        return map;
    }
}

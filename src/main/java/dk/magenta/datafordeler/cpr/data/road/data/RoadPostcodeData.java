package dk.magenta.datafordeler.cpr.data.road.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.data.DetailData;
import dk.magenta.datafordeler.cpr.data.unversioned.PostCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 29-06-17.
 */
@Entity
@Table(name="cpr_road_postcode")
public class RoadPostcodeData extends DetailData {


    @ManyToOne(targetEntity = RoadBaseData.class)
    private RoadBaseData roadBaseData;

    public RoadBaseData getRoadBaseData() {
        return this.roadBaseData;
    }

    public void setRoadBaseData(RoadBaseData roadBaseData) {
        this.roadBaseData = roadBaseData;
    }

    public static final String DB_FIELD_HOUSENUMBER_FROM = "houseNumberFrom";
    public static final String IO_FIELD_HOUSENUMBER_FROM = "husnummerFra";
    @Column(name = DB_FIELD_HOUSENUMBER_FROM)
    @JsonProperty(value = IO_FIELD_HOUSENUMBER_FROM)
    @XmlElement(name = IO_FIELD_HOUSENUMBER_FROM)
    private String houseNumberFrom;

    public String getHouseNumberFrom() {
        return this.houseNumberFrom;
    }

    public void setHouseNumberFrom(String houseNumberFrom) {
        this.houseNumberFrom = houseNumberFrom;
    }

    public static final String DB_FIELD_HOUSENUMBER_TO = "houseNumberTo";
    public static final String IO_FIELD_HOUSENUMBER_TO = "husnummerTil";
    @Column(name = DB_FIELD_HOUSENUMBER_TO)
    @JsonProperty(value = IO_FIELD_HOUSENUMBER_TO)
    @XmlElement(name = IO_FIELD_HOUSENUMBER_TO)
    private String houseNumberTo;

    public String getHouseNumberTo() {
        return this.houseNumberTo;
    }

    public void setHouseNumberTo(String houseNumberTo) {
        this.houseNumberTo = houseNumberTo;
    }

    public static final String DB_FIELD_EVEN = "even";
    public static final String IO_FIELD_EVEN = "lige";
    @Column(name = DB_FIELD_EVEN)
    @JsonProperty(value = IO_FIELD_EVEN)
    @XmlElement(name = IO_FIELD_EVEN)
    private boolean even;

    public boolean isEven() {
        return this.even;
    }

    public void setEven(boolean even) {
        this.even = even;
    }

    public static final String DB_FIELD_POSTCODE = "postCode";
    public static final String IO_FIELD_POSTCODE = "postNummer";
    @ManyToOne
    @JsonProperty(value = IO_FIELD_POSTCODE)
    @XmlElement(name = IO_FIELD_POSTCODE)
    private PostCode postCode;

    public PostCode getPostCode() {
        return this.postCode;
    }

    public void setPostCode(PostCode postCode) {
        this.postCode = postCode;
    }

    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();

        if (this.houseNumberFrom != null) {
            map.put(IO_FIELD_HOUSENUMBER_FROM, this.houseNumberFrom);
        }
        if (this.houseNumberTo != null) {
            map.put(IO_FIELD_HOUSENUMBER_TO, this.houseNumberTo);
        }
        map.put(IO_FIELD_EVEN, this.even);
        if (this.postCode != null) {
            map.put(IO_FIELD_POSTCODE, this.postCode);
        }
        return map;
    }


    @JsonIgnore
    public Map<String, Object> databaseFields() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(DB_FIELD_POSTCODE, this.postCode);
        map.put(DB_FIELD_EVEN, this.even);
        map.put(DB_FIELD_HOUSENUMBER_FROM, this.houseNumberFrom);
        map.put(DB_FIELD_HOUSENUMBER_TO, this.houseNumberTo);
        return map;
    }
}

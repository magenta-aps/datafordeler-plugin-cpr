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
@Table(name="cpr_road_city")
public class RoadCityData extends DetailData {

    public static final String DB_FIELD_HOUSENUMBER_FROM = "housenumberFrom";
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

    public static final String DB_FIELD_HOUSENUMBER_TO = "housenumberTo";
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

    public static final String DB_FIELD_CITYNAME = "cityName";
    public static final String IO_FIELD_CITYNAME = "bynavn";
    @Column(name = DB_FIELD_CITYNAME)
    @JsonProperty(value = IO_FIELD_CITYNAME)
    @XmlElement(name = IO_FIELD_CITYNAME)
    private String cityName;

    public String getCityName() {
        return this.cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();

        if (this.houseNumberFrom != null) {
            map.put("houseNumberFrom", this.houseNumberFrom);
        }
        if (this.houseNumberTo != null) {
            map.put("houseNumberTo", this.houseNumberTo);
        }
        map.put("even", this.even);
        if (this.cityName != null) {
            map.put("cityName", this.cityName);
        }
        return map;
    }

    @JsonIgnore
    public Map<String, Object> databaseFields() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(DB_FIELD_CITYNAME, this.cityName);
        map.put(DB_FIELD_EVEN, this.even);
        map.put(DB_FIELD_HOUSENUMBER_FROM, this.houseNumberFrom);
        map.put(DB_FIELD_HOUSENUMBER_TO, this.houseNumberTo);
        return map;
    }

}

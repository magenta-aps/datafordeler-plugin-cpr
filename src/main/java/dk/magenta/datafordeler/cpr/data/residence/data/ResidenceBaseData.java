package dk.magenta.datafordeler.cpr.data.residence.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.core.database.DataItem;
import dk.magenta.datafordeler.core.database.LookupDefinition;
import dk.magenta.datafordeler.cpr.data.CprData;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceEffect;
import org.hibernate.Session;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for Residence data, linking to Effects and delegating storage to referred classes
 */
@Entity
@Table(name="cpr_residence_data", indexes = {
        @Index(name = "cpr_residence_lastUpdated", columnList = DataItem.DB_FIELD_LAST_UPDATED),
        @Index(name = "cpr_residence_municipality_code", columnList = ResidenceBaseData.DB_FIELD_MUNICIPALITY_CODE),
        @Index(name = "cpr_residence_road_code", columnList = ResidenceBaseData.DB_FIELD_ROAD_CODE),
        @Index(name = "cpr_residence_housenumber", columnList = ResidenceBaseData.DB_FIELD_HOUSENUMBER),
        @Index(name = "cpr_residence_floor", columnList = ResidenceBaseData.DB_FIELD_FLOOR),
        @Index(name = "cpr_residence_door", columnList = ResidenceBaseData.DB_FIELD_DOOR),
})
public class ResidenceBaseData extends CprData<ResidenceEffect, ResidenceBaseData> {

    public static final String DB_FIELD_MUNICIPALITY_CODE = "municipalityCode";
    public static final String IO_FIELD_MUNICIPALITY_CODE = "kommunekode";
    @Column(name = DB_FIELD_MUNICIPALITY_CODE)
    @JsonProperty(value = IO_FIELD_MUNICIPALITY_CODE)
    @XmlElement(name = IO_FIELD_MUNICIPALITY_CODE)
    private int municipalityCode;

    public int getKommunekode() {
        return this.municipalityCode;
    }

    public void setKommunekode(int kommunekode) {
        this.municipalityCode = kommunekode;
    }

    public static final String DB_FIELD_ROAD_CODE = "roadCode";
    public static final String IO_FIELD_ROAD_CODE = "vejkode";
    @Column(name = DB_FIELD_ROAD_CODE)
    @JsonProperty(value = IO_FIELD_ROAD_CODE)
    @XmlElement(name = IO_FIELD_ROAD_CODE)
    private int roadCode;

    public int getVejkode() {
        return this.roadCode;
    }

    public void setVejkode(int vejkode) {
        this.roadCode = vejkode;
    }

    public static final String DB_FIELD_HOUSENUMBER = "houseNumber";
    public static final String IO_FIELD_HOUSENUMBER = "husnummer";
    @Column(name = DB_FIELD_HOUSENUMBER)
    @JsonProperty(value = IO_FIELD_HOUSENUMBER)
    @XmlElement(name = IO_FIELD_HOUSENUMBER)
    private String houseNumber;

    public String getHusnummer() {
        return this.houseNumber;
    }

    public void setHusnummer(String husnummer) {
        this.houseNumber = husnummer;
    }

    public static final String DB_FIELD_FLOOR = "floor";
    public static final String IO_FIELD_FLOOR = "etage";
    @Column(name = DB_FIELD_FLOOR)
    @JsonProperty(value = IO_FIELD_FLOOR)
    @XmlElement(name = IO_FIELD_FLOOR)
    private String floor;

    public String getEtage() {
        return this.floor;
    }

    public void setEtage(String etage) {
        this.floor = etage;
    }

    public static final String DB_FIELD_DOOR = "door";
    public static final String IO_FIELD_DOOR = "sidedør";
    @Column(name = DB_FIELD_DOOR)
    @JsonProperty(value = IO_FIELD_DOOR)
    @XmlElement(name = IO_FIELD_DOOR)
    private String door;

    public String getSideDoer() {
        return this.door;
    }

    public void setSideDoer(String sideDoer) {
        this.door = sideDoer;
    }

    public static final String DB_FIELD_LOCALITY = "locality";
    public static final String IO_FIELD_LOCALITY = "lokalitet";
    @Column(name = DB_FIELD_LOCALITY)
    @JsonProperty(value = IO_FIELD_LOCALITY)
    @XmlElement(name = IO_FIELD_LOCALITY)
    private String locality;

    public String getLokalitet() {
        return this.locality;
    }

    public void setLokalitet(String lokalitet) {
        this.locality = lokalitet;
    }

    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(IO_FIELD_MUNICIPALITY_CODE, this.municipalityCode);
        map.put(IO_FIELD_ROAD_CODE, this.roadCode);
        map.put(IO_FIELD_HOUSENUMBER, this.houseNumber);
        map.put(IO_FIELD_FLOOR, this.floor);
        map.put(IO_FIELD_DOOR, this.door);
        map.put(IO_FIELD_LOCALITY, this.locality);
        return map;
    }

    public LookupDefinition getLookupDefinition() {
        LookupDefinition lookupDefinition = new LookupDefinition(ResidenceBaseData.class);
        lookupDefinition.setMatchNulls(true);

        lookupDefinition.put(DB_FIELD_MUNICIPALITY_CODE, this.municipalityCode);
        lookupDefinition.put(DB_FIELD_ROAD_CODE, this.roadCode);
        lookupDefinition.put(DB_FIELD_LOCALITY, this.locality);
        lookupDefinition.put(DB_FIELD_HOUSENUMBER, this.houseNumber);
        lookupDefinition.put(DB_FIELD_FLOOR, this.floor);
        lookupDefinition.put(DB_FIELD_DOOR, this.door);

        return lookupDefinition;
    }

    @Override
    public void forceLoad(Session session) {

    }
}

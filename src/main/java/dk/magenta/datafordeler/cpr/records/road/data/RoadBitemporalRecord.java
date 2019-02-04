package dk.magenta.datafordeler.cpr.records.road.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.records.person.CprBitemporalPersonRecord;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

@MappedSuperclass
public class RoadBitemporalRecord extends CprBitemporalPersonRecord<RoadBitemporalRecord> {

    public static final String TABLE_NAME = "road_record";

    public RoadBitemporalRecord() {
    }


    // Timestamp
    public static final String DB_FIELD_TIMESTAMP = "timestamp";
    public static final String IO_FIELD_TO_TIMESTAMP = "tidsstempel";
    @Column(name = DB_FIELD_TIMESTAMP)
    @JsonProperty(value = IO_FIELD_TO_TIMESTAMP)
    @XmlElement(name = IO_FIELD_TO_TIMESTAMP)
    private String timestamp;

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    // Til kommunekode
    public static final String DB_FIELD_TO_MUNIPALITY_CODE = "toMunicipalityCode";
    public static final String IO_FIELD_TO_MUNIPALITY_CODE = "tilKommuneKode";
    @Column(name = DB_FIELD_TO_MUNIPALITY_CODE)
    @JsonProperty(value = IO_FIELD_TO_MUNIPALITY_CODE)
    @XmlElement(name = IO_FIELD_TO_MUNIPALITY_CODE)
    private String toMunicipalityCode;

    public String getToMunicipalityCode() {
        return this.toMunicipalityCode;
    }

    public void setToMunicipalityCode(String toMunicipalityCode) {
        this.toMunicipalityCode = toMunicipalityCode;
    }

    // Til vejkode
    public static final String DB_FIELD_TO_ROAD_CODE = "toRoadCode";
    public static final String IO_FIELD_TO_ROAD_CODE = "tilVejKode";
    @Column(name = DB_FIELD_TO_ROAD_CODE)
    @JsonProperty(value = IO_FIELD_TO_ROAD_CODE)
    @XmlElement(name = IO_FIELD_TO_ROAD_CODE)
    private String toRoadCode;

    public String getToRoadCode() {
        return toRoadCode;
    }

    public void setToRoadCode(String toRoadCode) {
        this.toRoadCode = toRoadCode;
    }


    // Fra kommunekode
    public static final String DB_FIELD_FROM_MUNIPALITY_CODE = "fromMunicipalityCode";
    public static final String IO_FIELD_FROM_MUNIPALITY_CODE = "fraKommuneKode";
    @Column(name = DB_FIELD_FROM_MUNIPALITY_CODE)
    @JsonProperty(value = IO_FIELD_FROM_MUNIPALITY_CODE)
    @XmlElement(name = IO_FIELD_FROM_MUNIPALITY_CODE)
    private String fromMunicipalityCode;
    public String getFromMunicipalityCode() {
        return fromMunicipalityCode;
    }

    public void setFromMunicipalityCode(String fromMunicipalityCode) {
        this.fromMunicipalityCode = fromMunicipalityCode;
    }

    // Fra vejkode
    public static final String DB_FIELD_FROM_ROAD_CODE = "fromRoadCode";
    public static final String IO_FIELD_FROM_ROAD_CODE = "fraVejKode";
    @Column(name = DB_FIELD_FROM_ROAD_CODE)
    @JsonProperty(value = IO_FIELD_FROM_ROAD_CODE)
    @XmlElement(name = IO_FIELD_FROM_ROAD_CODE)
    private String fromRoadCode;

    public String getFromRoadCode() {
        return fromRoadCode;
    }

    public void setFromRoadCode(String fromRoadCode) {
        this.fromRoadCode = fromRoadCode;
    }

    // Haenstart
    public static final String DB_FIELD_HAEN_START = "haenStart";
    public static final String IO_FIELD_HAEN_START = "haenStart";
    @Column(name = DB_FIELD_HAEN_START)
    @JsonProperty(value = IO_FIELD_HAEN_START)
    @XmlElement(name = IO_FIELD_HAEN_START)
    private String haenStart;

    public String getHaenStart() {
        return haenStart;
    }

    public void setHaenStart(String haenStart) {
        this.haenStart = haenStart;
    }

    // Vejens adressenavn
    public static final String DB_FIELD_ROM_ROAD_CODE = "roadAdddressName";
    public static final String IO_FIELD_ROM_ROAD_CODE = "vejensAddressenavn";
    @Column(name = DB_FIELD_ROM_ROAD_CODE)
    @JsonProperty(value = IO_FIELD_ROM_ROAD_CODE)
    @XmlElement(name = IO_FIELD_ROM_ROAD_CODE)
    private String roadAdddressName;

    public String getRoadAdddressName() {
        return roadAdddressName;
    }

    public void setRoadAdddressName(String roadAdddressName) {
        this.roadAdddressName = roadAdddressName;
    }

    // Vejens navn
    public static final String DB_FIELD_ROADNAME = "roadName";
    public static final String IO_FIELD_ROADNAME = "vejensNavn";
    @Column(name = DB_FIELD_ROADNAME)
    @JsonProperty(value = IO_FIELD_ROADNAME)
    @XmlElement(name = IO_FIELD_ROADNAME)
    private String roadName;

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoadBitemporalRecord)) return false;
        if (!super.equals(o)) return false;
        RoadBitemporalRecord that = (RoadBitemporalRecord) o;
        return Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(toMunicipalityCode, that.toMunicipalityCode) &&
                Objects.equals(toRoadCode, that.toRoadCode) &&
                Objects.equals(fromMunicipalityCode, that.fromMunicipalityCode) &&
                Objects.equals(fromRoadCode, that.fromRoadCode) &&
                Objects.equals(haenStart, that.haenStart) &&
                Objects.equals(roadAdddressName, that.roadAdddressName) &&
                Objects.equals(roadName, that.roadName);
    }

    @Override
    public boolean hasData() {
        return stringNonEmpty(this.timestamp) || stringNonEmpty(this.toMunicipalityCode) || stringNonEmpty(this.toRoadCode) ||
                stringNonEmpty(this.fromMunicipalityCode) || stringNonEmpty(this.fromRoadCode) || stringNonEmpty(this.haenStart) ||
                stringNonEmpty(this.roadAdddressName) || stringNonEmpty(this.roadName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), timestamp, toMunicipalityCode, toRoadCode, fromMunicipalityCode, fromRoadCode, haenStart, roadAdddressName, roadName);
    }

    @Override
    public RoadBitemporalRecord clone() {
        RoadBitemporalRecord clone = new RoadBitemporalRecord();
        clone.timestamp = this.timestamp;
        clone.toMunicipalityCode = this.toMunicipalityCode;
        clone.toRoadCode = this.toRoadCode;
        clone.fromMunicipalityCode = this.fromMunicipalityCode;
        clone.fromRoadCode = this.fromRoadCode;
        clone.haenStart = this.haenStart;
        clone.roadAdddressName = this.roadAdddressName;
        clone.roadName = this.roadName;
        RoadBitemporalRecord.copy(this, clone);
        return clone;
    }
}

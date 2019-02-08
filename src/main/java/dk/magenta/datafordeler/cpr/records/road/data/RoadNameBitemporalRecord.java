package dk.magenta.datafordeler.cpr.records.road.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.CprPlugin;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.time.OffsetDateTime;
import java.util.Objects;

@javax.persistence.Entity
@Table(name= CprPlugin.DEBUG_TABLE_PREFIX + "road_record")
public class RoadNameBitemporalRecord extends CprBitemporalRoadRecord<RoadNameBitemporalRecord> {

    public static final String TABLE_NAME = "road_record";

    public RoadNameBitemporalRecord() {
    }

    public RoadNameBitemporalRecord(OffsetDateTime timestamp, int toMunicipalityCode, int toRoadCode, int fromMunicipalityCode,
                                    int fromRoadCode, OffsetDateTime haenStart, String roadAdddressName, String roadName) {
        super.setRegistrationFrom(timestamp);
        super.setEffectFrom(haenStart);
        this.timestamp = timestamp;
        this.toMunicipalityCode = toMunicipalityCode;
        this.toRoadCode = toRoadCode;
        this.fromMunicipalityCode = fromMunicipalityCode;
        this.fromRoadCode = fromRoadCode;
        this.haenStart = haenStart;
        this.roadAdddressName = roadAdddressName;
        this.roadName = roadName;
    }


    // Timestamp
    public static final String DB_FIELD_TIMESTAMP = "timestamp";
    public static final String IO_FIELD_TO_TIMESTAMP = "tidsstempel";
    @Column(name = DB_FIELD_TIMESTAMP)
    @JsonProperty(value = IO_FIELD_TO_TIMESTAMP)
    @XmlElement(name = IO_FIELD_TO_TIMESTAMP)
    private OffsetDateTime timestamp;

    public OffsetDateTime getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }


    // Til kommunekode
    public static final String DB_FIELD_TO_MUNIPALITY_CODE = "toMunicipalityCode";
    public static final String IO_FIELD_TO_MUNIPALITY_CODE = "tilKommuneKode";
    @Column(name = DB_FIELD_TO_MUNIPALITY_CODE)
    @JsonProperty(value = IO_FIELD_TO_MUNIPALITY_CODE)
    @XmlElement(name = IO_FIELD_TO_MUNIPALITY_CODE)
    private int toMunicipalityCode;

    public int getToMunicipalityCode() {
        return this.toMunicipalityCode;
    }

    public void setToMunicipalityCode(int toMunicipalityCode) {
        this.toMunicipalityCode = toMunicipalityCode;
    }

    // Til vejkode
    public static final String DB_FIELD_TO_ROAD_CODE = "toRoadCode";
    public static final String IO_FIELD_TO_ROAD_CODE = "tilVejKode";
    @Column(name = DB_FIELD_TO_ROAD_CODE)
    @JsonProperty(value = IO_FIELD_TO_ROAD_CODE)
    @XmlElement(name = IO_FIELD_TO_ROAD_CODE)
    private int toRoadCode;

    public int getToRoadCode() {
        return toRoadCode;
    }

    public void setToRoadCode(int toRoadCode) {
        this.toRoadCode = toRoadCode;
    }


    // Fra kommunekode
    public static final String DB_FIELD_FROM_MUNIPALITY_CODE = "fromMunicipalityCode";
    public static final String IO_FIELD_FROM_MUNIPALITY_CODE = "fraKommuneKode";
    @Column(name = DB_FIELD_FROM_MUNIPALITY_CODE)
    @JsonProperty(value = IO_FIELD_FROM_MUNIPALITY_CODE)
    @XmlElement(name = IO_FIELD_FROM_MUNIPALITY_CODE)
    private int fromMunicipalityCode;
    public int getFromMunicipalityCode() {
        return fromMunicipalityCode;
    }

    public void setFromMunicipalityCode(int fromMunicipalityCode) {
        this.fromMunicipalityCode = fromMunicipalityCode;
    }

    // Fra vejkode
    public static final String DB_FIELD_FROM_ROAD_CODE = "fromRoadCode";
    public static final String IO_FIELD_FROM_ROAD_CODE = "fraVejKode";
    @Column(name = DB_FIELD_FROM_ROAD_CODE)
    @JsonProperty(value = IO_FIELD_FROM_ROAD_CODE)
    @XmlElement(name = IO_FIELD_FROM_ROAD_CODE)
    private int fromRoadCode;

    public int getFromRoadCode() {
        return fromRoadCode;
    }

    public void setFromRoadCode(int fromRoadCode) {
        this.fromRoadCode = fromRoadCode;
    }

    // Haenstart
    public static final String DB_FIELD_HAEN_START = "haenStart";
    public static final String IO_FIELD_HAEN_START = "haenStart";
    @Column(name = DB_FIELD_HAEN_START)
    @JsonProperty(value = IO_FIELD_HAEN_START)
    @XmlElement(name = IO_FIELD_HAEN_START)
    private OffsetDateTime haenStart;

    public OffsetDateTime getHaenStart() {
        return haenStart;
    }

    public void setHaenStart(OffsetDateTime haenStart) {
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
        if (!(o instanceof RoadNameBitemporalRecord)) return false;
        if (!super.equals(o)) return false;
        RoadNameBitemporalRecord that = (RoadNameBitemporalRecord) o;
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
        return this.timestamp!=null || this.toMunicipalityCode!=0 || this.toRoadCode!=0 ||
                this.fromMunicipalityCode!=0 || this.fromRoadCode!=0 || this.haenStart!=null ||
                stringNonEmpty(this.roadAdddressName) || stringNonEmpty(this.roadName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), timestamp, toMunicipalityCode, toRoadCode, fromMunicipalityCode, fromRoadCode, haenStart, roadAdddressName, roadName);
    }

    @Override
    public RoadNameBitemporalRecord clone() {
        RoadNameBitemporalRecord clone = new RoadNameBitemporalRecord();
        clone.timestamp = this.timestamp;
        clone.toMunicipalityCode = this.toMunicipalityCode;
        clone.toRoadCode = this.toRoadCode;
        clone.fromMunicipalityCode = this.fromMunicipalityCode;
        clone.fromRoadCode = this.fromRoadCode;
        clone.haenStart = this.haenStart;
        clone.roadAdddressName = this.roadAdddressName;
        clone.roadName = this.roadName;
        RoadNameBitemporalRecord.copy(this, clone);
        return clone;
    }
}

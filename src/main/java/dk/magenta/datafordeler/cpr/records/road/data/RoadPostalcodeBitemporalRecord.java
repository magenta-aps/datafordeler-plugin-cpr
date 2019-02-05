package dk.magenta.datafordeler.cpr.records.road.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.CprPlugin;
import dk.magenta.datafordeler.cpr.records.CprBitemporalRecord;
import dk.magenta.datafordeler.cpr.records.person.CprBitemporalPersonRecord;
import dk.magenta.datafordeler.cpr.records.person.data.PersonNumberDataRecord;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

@javax.persistence.Entity
@Table(name= CprPlugin.DEBUG_TABLE_PREFIX + "road_postalcode_record")
public class RoadPostalcodeBitemporalRecord extends CprBitemporalPersonRecord<RoadPostalcodeBitemporalRecord> {

    public static final String TABLE_NAME = "road_postalcode_record";

    public RoadPostalcodeBitemporalRecord() {
    }

    public RoadPostalcodeBitemporalRecord(String timestamp, String toHousenumber, String fromHousenumber, boolean equalUnequal, int postalCode, String postalDistrict) {
        this.timestamp = timestamp;
        this.toHousenumber = toHousenumber;
        this.fromHousenumber = fromHousenumber;
        this.equalUnequal = equalUnequal;
        this.postalCode = postalCode;
        this.postalDistrict = postalDistrict;
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


    // Til husnummer
    public static final String DB_FIELD_TO_HOUSENUMBER = "toHousenumber";
    public static final String IO_FIELD_TO_HOUSENUMBER = "tilHusnummer";
    @Column(name = DB_FIELD_TO_HOUSENUMBER)
    @JsonProperty(value = IO_FIELD_TO_HOUSENUMBER)
    @XmlElement(name = IO_FIELD_TO_HOUSENUMBER)
    private String toHousenumber;
    public String getToHousenumber() {
        return toHousenumber;
    }

    public void setToHousenumber(String toHousenumber) {
        this.toHousenumber = toHousenumber;
    }

    // Fra husnummer
    public static final String DB_FIELD_FROM_HOUSENUMBER = "fromHousenumber";
    public static final String IO_FIELD_FROM_HOUSENUMBER = "fraHusnummer";
    @Column(name = DB_FIELD_FROM_HOUSENUMBER)
    @JsonProperty(value = IO_FIELD_FROM_HOUSENUMBER)
    @XmlElement(name = IO_FIELD_FROM_HOUSENUMBER)
    private String fromHousenumber;
    public String getFromHousenumber() {
        return fromHousenumber;
    }

    public void setFromHousenumber(String fromHousenumber) {
        this.fromHousenumber = fromHousenumber;
    }

    // Lige/ulige indikator
    public static final String DB_FIELD_EQUAL_UNEQUAL = "equalUnequal";
    public static final String IO_FIELD_EQUAL_UNEQUAL= "ligeUlige";
    @Column(name = DB_FIELD_EQUAL_UNEQUAL)
    @JsonProperty(value = IO_FIELD_EQUAL_UNEQUAL)
    @XmlElement(name = IO_FIELD_EQUAL_UNEQUAL)
    private boolean equalUnequal;
    public boolean getEqualUnequal() {
        return equalUnequal;
    }

    public void setEqualUnequal(boolean equalUnequal) {
        this.equalUnequal = equalUnequal;
    }

    // postnummer
    public static final String DB_FIELD_POSTAL_CODE = "postalCode";
    public static final String IO_FIELD_POSTAL_CODE= "postnummer";
    @Column(name = DB_FIELD_POSTAL_CODE)
    @JsonProperty(value = IO_FIELD_POSTAL_CODE)
    @XmlElement(name = IO_FIELD_POSTAL_CODE)
    private int postalCode;
    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    // Postdistrikt
    public static final String DB_FIELD_POSTAL_DISTRICT = "postalDistrict";
    public static final String IO_FIELD_POSTAL_DISTRICT= "postDistrikt";
    @Column(name = DB_FIELD_POSTAL_DISTRICT)
    @JsonProperty(value = IO_FIELD_POSTAL_DISTRICT)
    @XmlElement(name = IO_FIELD_POSTAL_DISTRICT)
    private String postalDistrict;
    public String getPostalDistrict() {
        return postalDistrict;
    }

    public void setPostalDistrict(String postalDistrict) {
        this.postalDistrict = postalDistrict;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoadPostalcodeBitemporalRecord)) return false;
        if (!super.equals(o)) return false;
        RoadPostalcodeBitemporalRecord that = (RoadPostalcodeBitemporalRecord) o;
        return Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(toHousenumber, that.toHousenumber) &&
                Objects.equals(fromHousenumber, that.fromHousenumber) &&
                Objects.equals(equalUnequal, that.equalUnequal) &&
                Objects.equals(postalCode, that.postalCode) &&
                Objects.equals(postalDistrict, that.postalDistrict);
    }

    @Override
    public boolean hasData() {
        return stringNonEmpty(this.timestamp) || stringNonEmpty(this.toHousenumber) || stringNonEmpty(this.fromHousenumber) ||
                this.postalCode!=0 || stringNonEmpty(this.postalDistrict);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), timestamp, toHousenumber, fromHousenumber, equalUnequal, postalCode, postalDistrict);
    }

    @Override
    public RoadPostalcodeBitemporalRecord clone() {
        RoadPostalcodeBitemporalRecord clone = new RoadPostalcodeBitemporalRecord();
        clone.timestamp = this.timestamp;
        clone.toHousenumber = this.toHousenumber;
        clone.fromHousenumber = this.fromHousenumber;
        clone.equalUnequal = this.equalUnequal;
        clone.postalCode = this.postalCode;
        clone.postalDistrict = this.postalDistrict;
        RoadPostalcodeBitemporalRecord.copy(this, clone);
        return clone;
    }
}

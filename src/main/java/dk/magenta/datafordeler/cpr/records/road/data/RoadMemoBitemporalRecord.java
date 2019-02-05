package dk.magenta.datafordeler.cpr.records.road.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.CprPlugin;
import dk.magenta.datafordeler.cpr.records.person.CprBitemporalPersonRecord;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

@javax.persistence.Entity
@Table(name= CprPlugin.DEBUG_TABLE_PREFIX + "road_memo_record")
public class RoadMemoBitemporalRecord extends CprBitemporalPersonRecord<RoadMemoBitemporalRecord> {

    public static final String TABLE_NAME = "road_memo_record";

    public RoadMemoBitemporalRecord() {
    }

    public RoadMemoBitemporalRecord(String timestamp, String haenStart, int noteNumber, String noteLine) {
        this.timestamp = timestamp;
        this.haenStart = haenStart;
        this.noteNumber = noteNumber;
        this.noteLine = noteLine;
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


    // Notatnummer
    public static final String DB_FIELD_NOTE_NUMBER = "noteNumber";
    public static final String IO_FIELD_NOTE_NUMBER = "notatNummer";
    @Column(name = DB_FIELD_NOTE_NUMBER)
    @JsonProperty(value = IO_FIELD_NOTE_NUMBER)
    @XmlElement(name = IO_FIELD_NOTE_NUMBER)
    private int noteNumber;
    public int getNoteNumber() {
        return noteNumber;
    }

    public void setNoteNumber(int noteNumber) {
        this.noteNumber = noteNumber;
    }

    // Notatlinie
    public static final String DB_FIELD_NOTE_LINE = "noteLine";
    public static final String IO_FIELD_NOTE_LINE = "notatLinie";
    @Column(name = DB_FIELD_NOTE_LINE)
    @JsonProperty(value = IO_FIELD_NOTE_LINE)
    @XmlElement(name = IO_FIELD_NOTE_LINE)
    private String noteLine;
    public String getNoteLine() {
        return noteLine;
    }

    public void setNoteLine(String noteLine) {
        this.noteLine = noteLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoadMemoBitemporalRecord)) return false;
        if (!super.equals(o)) return false;
        RoadMemoBitemporalRecord that = (RoadMemoBitemporalRecord) o;
        return Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(haenStart, that.haenStart) &&
                Objects.equals(noteNumber, that.noteNumber) &&
                Objects.equals(noteLine, that.noteLine);
    }

    @Override
    public boolean hasData() {
        return stringNonEmpty(this.timestamp) || stringNonEmpty(this.haenStart) || this.noteNumber!=0 ||
                stringNonEmpty(this.noteLine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), timestamp, haenStart, noteNumber, noteLine);
    }

    @Override
    public RoadMemoBitemporalRecord clone() {
        RoadMemoBitemporalRecord clone = new RoadMemoBitemporalRecord();
        clone.timestamp = this.timestamp;
        clone.haenStart = this.haenStart;
        clone.noteNumber = this.noteNumber;
        clone.noteLine = this.noteLine;
        RoadMemoBitemporalRecord.copy(this, clone);
        return clone;
    }
}

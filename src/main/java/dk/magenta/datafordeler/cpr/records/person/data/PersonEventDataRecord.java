package dk.magenta.datafordeler.cpr.records.person.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.core.database.DatabaseEntry;
import dk.magenta.datafordeler.cpr.CprPlugin;
import dk.magenta.datafordeler.cpr.records.CprBitemporalRecord;
import dk.magenta.datafordeler.cpr.records.person.CprBitemporalPersonRecord;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Storage for data on a Person's parentage,
 * referenced by {@link dk.magenta.datafordeler.cpr.records.person.data.PersonEventDataRecord}
 */
@Entity
@Table(name = CprPlugin.DEBUG_TABLE_PREFIX + PersonEventDataRecord.TABLE_NAME, indexes = {
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + PersonEventDataRecord.TABLE_NAME + CprBitemporalPersonRecord.DB_FIELD_ENTITY, columnList = CprBitemporalPersonRecord.DB_FIELD_ENTITY + DatabaseEntry.REF)})
public class PersonEventDataRecord extends CprBitemporalPersonRecord<PersonEventDataRecord> {

    public static final String TABLE_NAME = "cpr_person_event_record";

    public PersonEventDataRecord() {
    }

    public PersonEventDataRecord(OffsetDateTime timestamp, String event, String derived) {
        this.timestamp = timestamp;
        this.event = event;
        this.derived = derived;
    }





    public static final String DB_FIELD_TIMESTAMP = "timestamp";
    @Column(name = DB_FIELD_TIMESTAMP)
    @JsonIgnore
    @XmlElement(name = DB_FIELD_TIMESTAMP)
    private OffsetDateTime timestamp;


    public static final String DB_FIELD_EVENT = "event";
    @Column(name = DB_FIELD_EVENT)
    @JsonIgnore
    @XmlTransient
    private String event;

    public static final String DB_FIELD_DERIVED = "derived";
    @Column(name = DB_FIELD_DERIVED)
    @JsonIgnore
    @XmlTransient
    private String derived;

    @Transient
    private OffsetDateTime effectFrom;

    @Transient
    private OffsetDateTime effectFromUncertain;

    @Transient
    private OffsetDateTime effectTo;

    @Transient
    private OffsetDateTime effectToUncertain;

    /*@Transient
    private OffsetDateTime historic;*/

    @Override
    public boolean equalData(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equalData(o)) return false;
        PersonEventDataRecord that = (PersonEventDataRecord) o;
        return timestamp == that.timestamp;
    }

    @Override
    public boolean hasData() {
        return this.timestamp != null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), timestamp);
    }

    @Override
    public PersonEventDataRecord clone() {
        PersonEventDataRecord clone = new PersonEventDataRecord();
        clone.timestamp = this.timestamp;
        clone.event = this.event;
        clone.derived = this.derived;
        return clone;
    }
}

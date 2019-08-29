package dk.magenta.datafordeler.cpr.records.person.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.core.database.*;
import dk.magenta.datafordeler.cpr.CprPlugin;
import dk.magenta.datafordeler.cpr.data.CprRecordEntity;
import dk.magenta.datafordeler.cpr.data.person.PersonEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.time.OffsetDateTime;
import java.util.Collection;

/**
 * Storage for data on a Person's eventhistory
 * referenced by {@link dk.magenta.datafordeler.cpr.records.person.data}
 */
@Entity
@Table(name = CprPlugin.DEBUG_TABLE_PREFIX + "cpr_person_event_record", indexes = {
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + "cpr_person_event_record" + PersonEventDataRecord.DB_FIELD_ENTITY, columnList = PersonEventDataRecord.DB_FIELD_ENTITY + DatabaseEntry.REF)
})
public class PersonEventDataRecord extends CprRecordEntity {

    public PersonEventDataRecord() {
    }

    public PersonEventDataRecord(OffsetDateTime timestamp, String event, String derived) {
        this.timestamp = timestamp;
        this.event = event;
        this.derived = derived;
    }


    public static final String DB_FIELD_ENTITY = "entity";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = PersonEventDataRecord.DB_FIELD_ENTITY + DatabaseEntry.REF)
    @JsonIgnore
    @XmlTransient
    private PersonEntity entity;

    public PersonEntity getEntity() {
        return this.entity;
    }

    public void setEntity(PersonEntity entity) {
        this.entity = entity;
    }

    public void setEntity(IdentifiedEntity entity) {
        this.entity = (PersonEntity) entity;
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


    public static final String DB_FIELD_IDENTIFICATION = "identification";

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = DB_FIELD_IDENTIFICATION)
    public Identification identification;


    public void setIdentification(Identification identification) {
        this.identification = identification;
    }

    @JsonProperty(value = "id")
    public Long getId() {
        return super.getId();
    }


    @Override
    public IdentifiedEntity getNewest(Collection<IdentifiedEntity> collection) {
        return null;
    }
}

package dk.magenta.datafordeler.cpr.records.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dk.magenta.datafordeler.core.database.DatabaseEntry;
import dk.magenta.datafordeler.cpr.data.person.PersonEntity;
import dk.magenta.datafordeler.cpr.records.CprBitemporalRecord;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class CprBitemporalPersonRecord extends CprBitemporalRecord {

    public static final String DB_FIELD_ENTITY = "entity";

    @ManyToOne(fetch = FetchType.EAGER, optional = false, targetEntity = PersonEntity.class)
    @JoinColumn(name = DB_FIELD_ENTITY + DatabaseEntry.REF)
    @JsonIgnore
    @XmlTransient
    private PersonEntity entity;

    public PersonEntity getEntity() {
        return this.entity;
    }

    public void setEntity(PersonEntity entity) {
        this.entity = entity;
    }
}

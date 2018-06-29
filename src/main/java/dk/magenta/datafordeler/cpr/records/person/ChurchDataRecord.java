package dk.magenta.datafordeler.cpr.records.person;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.CprPlugin;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.records.CprBitemporalRecord;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

/**
 * Storage for data on a Person's church relation,
 * referenced by {@link PersonBaseData}
 */
@Entity
@Table(name = CprPlugin.DEBUG_TABLE_PREFIX + ChurchDataRecord.TABLE_NAME, indexes = {
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + ChurchDataRecord.TABLE_NAME + CprBitemporalRecord.DB_FIELD_REGISTRATION_FROM, columnList = CprBitemporalRecord.DB_FIELD_REGISTRATION_FROM),
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + ChurchDataRecord.TABLE_NAME + CprBitemporalRecord.DB_FIELD_REGISTRATION_TO, columnList = CprBitemporalRecord.DB_FIELD_REGISTRATION_TO),
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + ChurchDataRecord.TABLE_NAME + CprBitemporalRecord.DB_FIELD_EFFECT_FROM, columnList = CprBitemporalRecord.DB_FIELD_EFFECT_FROM),
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + ChurchDataRecord.TABLE_NAME + CprBitemporalRecord.DB_FIELD_EFFECT_TO, columnList = CprBitemporalRecord.DB_FIELD_EFFECT_TO),
})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ChurchDataRecord extends CprBitemporalPersonRecord {

    public static final String TABLE_NAME = "cpr_person_church_record";

    public ChurchDataRecord() {
    }

    public ChurchDataRecord(Character churchRelation) {
        this.churchRelation = churchRelation;
    }

    public static final String DB_FIELD_CHURCH_RELATION = "churchRelation";
    public static final String IO_FIELD_CHURCH_RELATION = "folkekirkeforhold";
    @Column(name = DB_FIELD_CHURCH_RELATION)
    @JsonProperty(value = IO_FIELD_CHURCH_RELATION)
    @XmlElement(name = IO_FIELD_CHURCH_RELATION)
    private Character churchRelation;

    public Character getChurchRelation() {
        return this.churchRelation;
    }

    public void setChurchRelation(Character churchRelation) {
        this.churchRelation = churchRelation;
    }

    @Override
    protected ChurchDataRecord clone() {
        ChurchDataRecord clone = new ChurchDataRecord();
        clone.churchRelation = this.churchRelation;
        CprBitemporalRecord.copy(this, clone);
        return clone;
    }

}

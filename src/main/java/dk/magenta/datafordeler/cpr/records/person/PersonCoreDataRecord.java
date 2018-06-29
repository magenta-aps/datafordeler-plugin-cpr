package dk.magenta.datafordeler.cpr.records.person;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.CprPlugin;
import dk.magenta.datafordeler.cpr.records.CprBitemporalRecord;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

/**
 * Storage for data on a Person's core data (gender, current cpr number),
 * referenced by {@link dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData}
 */
@Entity
@Table(name = CprPlugin.DEBUG_TABLE_PREFIX + PersonCoreDataRecord.TABLE_NAME, indexes = {
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + PersonCoreDataRecord.TABLE_NAME + CprBitemporalRecord.DB_FIELD_REGISTRATION_FROM, columnList = CprBitemporalRecord.DB_FIELD_REGISTRATION_FROM),
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + PersonCoreDataRecord.TABLE_NAME + CprBitemporalRecord.DB_FIELD_REGISTRATION_TO, columnList = CprBitemporalRecord.DB_FIELD_REGISTRATION_TO),
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + PersonCoreDataRecord.TABLE_NAME + CprBitemporalRecord.DB_FIELD_EFFECT_FROM, columnList = CprBitemporalRecord.DB_FIELD_EFFECT_FROM),
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + PersonCoreDataRecord.TABLE_NAME + CprBitemporalRecord.DB_FIELD_EFFECT_TO, columnList = CprBitemporalRecord.DB_FIELD_EFFECT_TO),
})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PersonCoreDataRecord extends CprBitemporalPersonRecord {

    public static final String TABLE_NAME = "cpr_person_core_record";

    public enum Koen {
        MAND,
        KVINDE
    }




    public static final String DB_FIELD_GENDER = "gender";
    public static final String IO_FIELD_GENDER = "køn";
    @Column(name = DB_FIELD_GENDER)
    @JsonProperty(value = IO_FIELD_GENDER)
    @XmlElement(name = IO_FIELD_GENDER)
    private Koen gender;

    public Koen getGender() {
        return this.gender;
    }

    public void setGender(Koen gender) {
        this.gender = gender;
    }

    public void setKoen(String koen) {
        if (koen != null) {
            if (koen.equalsIgnoreCase("M")) {
                this.setGender(Koen.MAND);
            } else if (koen.equalsIgnoreCase("K")) {
                this.setGender(Koen.KVINDE);
            }
        }
    }

    @Override
    protected PersonCoreDataRecord clone() {
        PersonCoreDataRecord clone = new PersonCoreDataRecord();
        clone.gender = this.gender;
        CprBitemporalRecord.copy(this, clone);
        return clone;
    }
}

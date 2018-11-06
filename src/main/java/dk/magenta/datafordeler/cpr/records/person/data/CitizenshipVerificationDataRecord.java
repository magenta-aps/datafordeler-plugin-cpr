package dk.magenta.datafordeler.cpr.records.person.data;

import dk.magenta.datafordeler.core.database.DatabaseEntry;
import dk.magenta.datafordeler.cpr.CprPlugin;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.records.CprBitemporalRecord;
import dk.magenta.datafordeler.cpr.records.person.CprBitemporalPersonRecord;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Storage for data on a Person's church verification,
 * referenced by {@link PersonBaseData}
 */
@Entity
@Table(name = CprPlugin.DEBUG_TABLE_PREFIX + CitizenshipVerificationDataRecord.TABLE_NAME, indexes = {
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + CitizenshipVerificationDataRecord.TABLE_NAME + CprBitemporalPersonRecord.DB_FIELD_ENTITY, columnList = CprBitemporalPersonRecord.DB_FIELD_ENTITY + DatabaseEntry.REF),
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + CitizenshipVerificationDataRecord.TABLE_NAME + CprBitemporalRecord.DB_FIELD_REGISTRATION_FROM, columnList = CprBitemporalRecord.DB_FIELD_REGISTRATION_FROM),
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + CitizenshipVerificationDataRecord.TABLE_NAME + CprBitemporalRecord.DB_FIELD_REGISTRATION_TO, columnList = CprBitemporalRecord.DB_FIELD_REGISTRATION_TO),
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + CitizenshipVerificationDataRecord.TABLE_NAME + CprBitemporalRecord.DB_FIELD_EFFECT_FROM, columnList = CprBitemporalRecord.DB_FIELD_EFFECT_FROM),
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + CitizenshipVerificationDataRecord.TABLE_NAME + CprBitemporalRecord.DB_FIELD_EFFECT_TO, columnList = CprBitemporalRecord.DB_FIELD_EFFECT_TO),
})
public class CitizenshipVerificationDataRecord extends VerificationDataRecord<CitizenshipVerificationDataRecord> {

    public static final String TABLE_NAME = "cpr_person_citizenship_verification_record";

    public CitizenshipVerificationDataRecord() {
    }

    public CitizenshipVerificationDataRecord(boolean verified) {
        super(verified);
    }



    @OneToMany(fetch = FetchType.LAZY, mappedBy = DB_FIELD_CORRECTION_OF)
    private Set<CitizenshipVerificationDataRecord> correctors = new HashSet<>();

    public Set<CitizenshipVerificationDataRecord> getCorrectors() {
        return this.correctors;
    }



    @Override
    protected CitizenshipVerificationDataRecord clone() {
        CitizenshipVerificationDataRecord clone = new CitizenshipVerificationDataRecord();
        VerificationDataRecord.copy(this, clone);
        return clone;
    }
}

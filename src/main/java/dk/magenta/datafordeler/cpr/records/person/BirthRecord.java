package dk.magenta.datafordeler.cpr.records.person;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.records.Bitemporality;
import org.hibernate.Session;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Record for Person birth (type 025).
 */
public class BirthRecord extends PersonDataRecord {

    private Bitemporality birthTemporality;
    private Bitemporality documentTemporality;

    public BirthRecord(String line) throws ParseException {
        super(line);
        this.obtain("start_mynkod-fødested", 14, 4);
        this.obtain("fødested_ts", 18, 12);
        this.obtain("myntxt_mynkod-fødested", 30, 4);
        this.obtain("myntxt_ts-fødested", 34, 12);
        this.obtain("myntxt-fødested", 46, 20);
        this.obtain("dok_mynkod-fødested", 66, 4);
        this.obtain("dok_ts-fødested", 70, 12);
        this.obtain("dok-fødested", 82, 3);

        this.birthTemporality = new Bitemporality(this.getOffsetDateTime("fødested_ts"));
        this.documentTemporality = new Bitemporality(this.getOffsetDateTime("dok_ts-fødested"));
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_BIRTH;
    }

    @Override
    public boolean populateBaseData(PersonBaseData data, PersonEffect effect, OffsetDateTime registrationTime, Session session) {
        boolean updated = false;
        if (this.birthTemporality.matches(registrationTime, effect)) {
            data.setBirth(
                this.getInt("start_mynkod-fødested"),
                this.getInt("myntxt_mynkod-fødested", true),
                this.getString("myntxt-fødested", true)
            );
            updated = true;
        }
        if (this.documentTemporality.matches(registrationTime, effect)) {
            data.setBirthVerification(
                    this.getInt("dok_mynkod-fødested"),
                    this.getBoolean("dok-fødested")
            );
            updated = true;
        }
        return updated;
    }

    @Override
    public HashSet<OffsetDateTime> getRegistrationTimestamps() {
        HashSet<OffsetDateTime> timestamps = super.getRegistrationTimestamps();
        timestamps.add(this.getOffsetDateTime("fødested_ts"));
        timestamps.add(this.getOffsetDateTime("dok_ts-fødested"));
        return timestamps;
    }

    @Override
    public List<Bitemporality> getBitemporality() {
        return Arrays.asList(
                this.birthTemporality,
                this.documentTemporality
        );
    }

    @Override
    public Set<PersonEffect> getEffects() {
        HashSet<PersonEffect> effects = new HashSet<>();
        effects.add(new PersonEffect(null, null, false, null, false));
        return effects;
    }
}

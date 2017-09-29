package dk.magenta.datafordeler.cpr.records.person;

import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.records.Bitemporality;
import org.hibernate.Session;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lars on 27-06-17.
 */
public class ProtectionRecord extends PersonDataRecord {

    private Bitemporality protectionTemporality;

    public ProtectionRecord(String line) throws ParseException {
        super(line);
        this.obtain("beskyttype",14,4);
        this.obtain("start_mynkod-beskyttelse",18,4);
        this.obtain("start_ts-beskyttelse",22,12	);
        this.obtain("start_dt-beskyttelse",34,10	);
        this.obtain("indrap-beskyttelse",44	,3);
        this.obtain("slet_dt-beskyttelse",47,10);

        this.protectionTemporality = new Bitemporality(this.getOffsetDateTime("start_ts-beskyttelse"), null, this.getOffsetDateTime("start_dt-beskyttelse"), false, this.getOffsetDateTime("slet_dt-beskyttelse"), false);
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_PROTECTION;
    }

    @Override
    public boolean populateBaseData(PersonBaseData data, PersonEffect effect, OffsetDateTime registrationTime, QueryManager queryManager, Session session) {
        if (this.protectionTemporality.matches(registrationTime, effect)) {
            data.setProtection(
                // int authority,
                this.getInt("start_mynkod-beskyttelse"),
                // int beskyttelsestype,
                this.getInt("beskyttype"),
                // boolean reportMarking
                this.getBoolean("indrap-beskyttelse")
            );
            return true;
        }
        return false;
    }

    @Override
    public HashSet<OffsetDateTime> getRegistrationTimestamps() {
        HashSet<OffsetDateTime> timestamps = super.getRegistrationTimestamps();
        timestamps.add(this.getOffsetDateTime("start_ts-beskyttelse"));
        return timestamps;
    }

    @Override
    public List<Bitemporality> getBitemporality() {
        return Collections.singletonList(this.protectionTemporality);
    }

    @Override
    public Set<PersonEffect> getEffects() {
        HashSet<PersonEffect> effects = new HashSet<>();
        effects.add(new PersonEffect(null, this.getOffsetDateTime("start_dt-beskyttelse"), false, this.getOffsetDateTime("slet_dt-beskyttelse"), false));
        return effects;
    }
}

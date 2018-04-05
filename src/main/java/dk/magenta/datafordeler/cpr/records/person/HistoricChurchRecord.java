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
 * Record for Person historic church relation (type 011).
 */
public class HistoricChurchRecord extends PersonDataRecord {

    private Bitemporality churchTemporality;
    private Bitemporality documentTemporality;

    public HistoricChurchRecord(String line) throws ParseException {
        super(line);
        this.obtain("start_mynkod-folkekirke", 14, 4);
        this.obtain("fkirk_ts", 18, 12);
        this.obtain("fkirk", 30, 1);
        this.obtain("start_dt-folkekirke", 31, 10);
        this.obtain("start_dt-umrk-folkekirke", 41, 1);
        this.obtain("slut_dt-folkekirke", 42, 10);
        this.obtain("slut_dt-umrk-folkekirke", 52, 1);
        this.obtain("dok_mynkod-folkekirke", 53, 4);
        this.obtain("dok_ts-folkekirke", 57, 12);
        this.obtain("dok-folkekirke", 69, 3);

        this.churchTemporality = new Bitemporality(this.getOffsetDateTime("fkirk_ts"), null, this.getOffsetDateTime("start_dt-folkekirke"), this.getBoolean("start_dt-umrk-folkekirke"), this.getOffsetDateTime("slut_dt-folkekirke"), this.getBoolean("slut_dt-umrk-folkekirke"));
        this.documentTemporality = new Bitemporality(this.getOffsetDateTime("dok_ts-folkekirke"));
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_HISTORIC_CHURCH;
    }

    @Override
    public boolean populateBaseData(PersonBaseData data, PersonEffect effect, OffsetDateTime registrationTime, Session session) {
        boolean updated = false;
        if (this.churchTemporality.matches(registrationTime, effect)) {
            //data.setChurch(this.getInt("start_mynkod-folkekirke"), this.getString("fkirk", true));
            updated = true;
        }
        if (this.documentTemporality.matches(registrationTime, effect)) {
            //data.setChurchVerification(this.get("dok_mynkod-folkekirke"), this.get("dok-folkekirke"));
            updated = true;
        }
        return updated;
    }

    @Override
    public HashSet<OffsetDateTime> getRegistrationTimestamps() {
        HashSet<OffsetDateTime> timestamps = super.getRegistrationTimestamps();
        timestamps.add(this.getOffsetDateTime("fkirk_ts"));
        timestamps.add(this.getOffsetDateTime("dok_ts-folkekirke"));
        return timestamps;
    }

    @Override
    public List<Bitemporality> getBitemporality() {
        return Arrays.asList(
                this.churchTemporality,
                this.documentTemporality
        );
    }

    @Override
    public Set<PersonEffect> getEffects() {
        HashSet<PersonEffect> effects = new HashSet<>();
        effects.add(new PersonEffect(null, this.getOffsetDateTime("start_dt-folkekirke"), this.getBoolean("start_dt-umrk-folkekirke"), this.getOffsetDateTime("slut_dt-folkekirke"), this.getBoolean("slut_dt-umrk-folkekirke")));
        effects.add(new PersonEffect(null, null, false, null, false));
        return effects;
    }
}

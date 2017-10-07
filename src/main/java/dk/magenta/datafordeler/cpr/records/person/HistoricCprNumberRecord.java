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
 * Created by lars on 22-06-17.
 */
public class HistoricCprNumberRecord extends PersonDataRecord {

    private Bitemporality cprTemporality;

    public HistoricCprNumberRecord(String line) throws ParseException {
        super(line);
        this.obtain("start_mynkod-pnrgaeld", 14, 4);
        this.obtain("gammelt_pnr", 18, 10);
        this.obtain("start_dt-person", 28, 10);
        this.obtain("start_dt_umrk-person", 38, 1);
        this.obtain("slut_dt-person", 38, 10);
        this.obtain("slut_dt_umrk-person", 49, 1);
        this.cprTemporality = new Bitemporality(null, null, this.getOffsetDateTime("start_dt-person"), this.getBoolean("start_dt_umrk-person"), this.getOffsetDateTime("slut_dt-person"), this.getBoolean("slut_dt_umrk-person"));
    }

    @Override
    public boolean populateBaseData(PersonBaseData data, PersonEffect effect, OffsetDateTime registrationTime, Session session) {
        boolean updated = false;
        if (this.cprTemporality.matches(registrationTime, effect)) {
            data.setCprNumber(this.getInt("start_mynkod-pnrgaeld"), this.getString("gammelt_pnr", false));
            updated = true;
        }
        return updated;
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_HISTORIC_CPRNUMBER;
    }

    @Override
    public HashSet<OffsetDateTime> getRegistrationTimestamps() {
        HashSet<OffsetDateTime> timestamps = super.getRegistrationTimestamps();
        timestamps.add(this.cprTemporality.registrationFrom);
        return timestamps;
    }

    @Override
    public List<Bitemporality> getBitemporality() {
        return Collections.singletonList(this.cprTemporality);
    }

    @Override
    public Set<PersonEffect> getEffects() {
        HashSet<PersonEffect> effects = new HashSet<>();
        effects.add(new PersonEffect(null, this.getOffsetDateTime("start_dt-person"), this.getBoolean("start_dt_umrk-person"), this.getOffsetDateTime("slut_dt-person"), this.getBoolean("slut_dt_umrk-person")));
        return effects;
    }
}

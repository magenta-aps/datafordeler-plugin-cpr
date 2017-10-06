package dk.magenta.datafordeler.cpr.records.person;

import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.records.Bitemporality;
import org.hibernate.Session;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Created by lars on 22-06-17.
 */
public class CivilStatusRecord extends PersonDataRecord {

    private Bitemporality civilTemporality;
    private Bitemporality documentTemporality;
    private Bitemporality officiaryTemporality;

    public CivilStatusRecord(String line) throws ParseException {
        super(line);
        this.obtain("start_mynkod-civilstand", 14, 4);
        this.obtain("civ_ts", 18, 12);
        this.obtain("civst", 30, 1);
        this.obtain("aegtepnr", 31, 10);
        this.obtain("aegtefoed_dt", 41, 10);
        this.obtain("aegtefoeddt_umrk", 51, 1);
        this.obtain("aegtenvn", 52, 34);
        this.obtain("aegtenvn_mrk", 86, 1);
        this.obtain("haenstart-civilstand", 87, 12);
        this.obtain("haenstart_umrk-civilstand", 99, 1);
        this.obtain("dok_mynkod-civilstand", 100, 4);
        this.obtain("dok_ts-civilstand", 104, 12);
        this.obtain("dok-civilstand", 116, 3);
        this.obtain("myntxt_mynkod-civilstand", 119, 4);
        this.obtain("myntxt_ts-civilstand", 123, 12);
        this.obtain("myntxt-civilstand", 135, 20);
        this.obtain("sep_henvis_ts", 155, 12);

        this.civilTemporality = new Bitemporality(this.getOffsetDateTime("civ_ts"), null, this.getOffsetDateTime("haenstart-civilstand"), this.getBoolean("haenstart_umrk-civilstand"), null, false);
        this.documentTemporality = new Bitemporality(this.getOffsetDateTime("dok_ts-civilstand"));
        this.officiaryTemporality = new Bitemporality(this.getOffsetDateTime("myntxt_ts-civilstand"));
    }

    @Override
    public boolean populateBaseData(PersonBaseData data, PersonEffect effect, OffsetDateTime registrationTime, Session session) {
        boolean updated = false;
        if (this.civilTemporality.matches(registrationTime, effect)) {
            data.setCivilStatus(
                // int authority,
                this.getInt("start_mynkod-civilstand"),
                // String civilStatus,
                this.getString("civst", true),
                // String spouseCpr,
                this.getString("aegtepnr", true),
                // LocalDate spouseBirthdate,
                this.getDate("aegtefoed_dt"),
                // boolean spouseBirthdateUncertain,
                this.getBoolean("aegtefoeddt_umrk"),
                // String spouseName,
                this.getString("aegtenvn", true),
                // boolean spouseNameMarking
                this.getMarking("aegtenvn_mrk")
            );
            updated = true;
        }
        if (this.documentTemporality.matches(registrationTime, effect)) {
            data.setCivilStatusVerification(
                    this.getInt("dok_mynkod-civilstand"),
                    this.getBoolean("dok-civilstand")
            );
        }
        if (this.officiaryTemporality.matches(registrationTime, effect)) {
            data.setCivilStatusAuthorityText(
                    this.getInt("myntxt_mynkod-civilstand"),
                    this.getString("myntxt-civilstand", true)
            );
        }
        return updated;
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_CURRENT_NAME;
    }

    @Override
    public HashSet<OffsetDateTime> getRegistrationTimestamps() {
        HashSet<OffsetDateTime> timestamps = super.getRegistrationTimestamps();
        timestamps.add(this.civilTemporality.registrationFrom);
        return timestamps;
    }

    @Override
    public List<Bitemporality> getBitemporality() {
        return Collections.singletonList(this.civilTemporality);
    }

    @Override
    public Set<PersonEffect> getEffects() {
        HashSet<PersonEffect> effects = new HashSet<>();
        effects.add(new PersonEffect(null, this.getOffsetDateTime("haenstart-civilstand"), this.getMarking("haenstart_umrk-civilstand"), null, false));
        return effects;
    }
}

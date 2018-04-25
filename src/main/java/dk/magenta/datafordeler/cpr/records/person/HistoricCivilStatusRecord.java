package dk.magenta.datafordeler.cpr.records.person;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.io.ImportMetadata;
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
 * Record for Person historic civil status (type 036).
 */
public class HistoricCivilStatusRecord extends PersonDataRecord {

    private Bitemporality civilTemporality;
    private Bitemporality documentTemporality;
    private Bitemporality officiaryTemporality;

    public HistoricCivilStatusRecord(String line) throws ParseException {
        super(line);
        this.obtain("annkor", 14, 1);
        this.obtain("start_mynkod-civilstand", 15, 4);
        this.obtain("civ_ts", 19, 12);
        this.obtain("civst", 31, 1);
        this.obtain("aegtepnr", 32, 10);
        this.obtain("aegtefoed_dt", 42, 10);
        this.obtain("aegtefoeddt_umrk", 52, 1);
        this.obtain("aegtenvn", 53, 34);
        this.obtain("aegtenvn_mrk", 87, 1);
        this.obtain("haenstart-civilstand", 88, 12);
        this.obtain("haenstart_umrk-civilstand", 100, 1);
        this.obtain("haenslut-civilstand", 101, 12);
        this.obtain("haenslut_umrk-civilstand", 113, 1);
        this.obtain("dok_mynkod-civilstand", 114, 4);
        this.obtain("dok_ts-civilstand", 118, 12);
        this.obtain("dok-civilstand", 130, 3);
        this.obtain("myntxt_mynkod-civilstand", 133, 4);
        this.obtain("myntxt_ts-civilstand", 137, 12);
        this.obtain("myntxt-civilstand", 149, 20);
        this.obtain("sep_henvis_ts", 169, 12);

        this.civilTemporality = new Bitemporality(this.getOffsetDateTime("civ_ts"), null, this.getOffsetDateTime("haenstart-civilstand"), this.getBoolean("haenstart_umrk-civilstand"), this.getOffsetDateTime("haenslut-civilstand"), this.getBoolean("haenslut_umrk-civilstand"));
        this.documentTemporality = new Bitemporality(this.getOffsetDateTime("dok_ts-civilstand"));
        this.officiaryTemporality = new Bitemporality(this.getOffsetDateTime("myntxt_ts-civilstand"));
    }

    @Override
    public boolean populateBaseData(PersonBaseData data, PersonEffect effect, OffsetDateTime registrationTime, Session session, ImportMetadata importMetadata) {
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
                    this.getMarking("aegtenvn_mrk"),
                    // String correctionMarking
                    this.getString("annkor", true),
                    importMetadata.getImportTime()
            );
            updated = true;
        }
        if (this.documentTemporality.matches(registrationTime, effect)) {
            data.setCivilStatusVerification(
                    this.getInt("dok_mynkod-civilstand"),
                    this.getBoolean("dok-civilstand"),
                    this.getString("annkor", true),
                    importMetadata.getImportTime()
            );
        }
        if (this.officiaryTemporality.matches(registrationTime, effect)) {
            data.setCivilStatusAuthorityText(
                    this.getInt("myntxt_mynkod-civilstand"),
                    this.getString("myntxt-civilstand", true),
                    this.getString("annkor", true),
                    importMetadata.getImportTime()
            );
        }
        return updated;
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_HISTORIC_CIVILSTATUS;
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

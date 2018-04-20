package dk.magenta.datafordeler.cpr.records.person;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.records.Bitemporality;
import org.hibernate.Session;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Record for Person citizenship (type 040).
 */
public class CitizenshipRecord extends PersonDataRecord {

    private Bitemporality citizenshipTemporality;
    private Bitemporality documentTemporality;

    public CitizenshipRecord(String line) throws ParseException {
        super(line);
        this.obtain("start_mynkod-statsborgerskab", 14, 4);
        this.obtain("stat_ts", 18, 12);
        this.obtain("landekod", 30, 4);
        this.obtain("haenstart-statsborgerskab", 34, 12);
        this.obtain("haenstart_umrk-statsborgerskab", 46, 1);
        this.obtain("dok_mynkod-statsborgerskab", 47, 4);
        this.obtain("dok_ts-statsborgerskab", 51, 12);
        this.obtain("dok-statsborgerskab", 63, 3);

        this.citizenshipTemporality = new Bitemporality(this.getOffsetDateTime("stat_ts"), null, this.getOffsetDateTime("haenstart-statsborgerskab"), this.getBoolean("haenstart_umrk-statsborgerskab"), null, false);

        OffsetDateTime docTime = this.getOffsetDateTime("dok_ts-statsborgerskab");
        if (docTime != null) {
            this.documentTemporality = new Bitemporality(docTime);
        }
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_CHURCH;
    }

    @Override
    public boolean populateBaseData(PersonBaseData data, PersonEffect effect, OffsetDateTime registrationTime, Session session, ImportMetadata importMetadata) {
        boolean updated = false;
        if (this.citizenshipTemporality.matches(registrationTime, effect)) {
            data.setCitizenship(
                    this.getInt("start_mynkod-statsborgerskab"),
                    this.getInt("landekod"),
                    importMetadata.getImportTime()
            );
            updated = true;
        }
        if (this.documentTemporality != null && this.documentTemporality.matches(registrationTime, effect)) {
            data.setCitizenshipVerification(
                    this.getInt("dok_mynkod-statsborgerskab"),
                    this.getBoolean("dok-statsborgerskab"),
                    importMetadata.getImportTime()
            );
            updated = true;
        }
        return updated;
    }

    @Override
    public HashSet<OffsetDateTime> getRegistrationTimestamps() {
        HashSet<OffsetDateTime> timestamps = super.getRegistrationTimestamps();
        timestamps.add(this.getOffsetDateTime("stat_ts"));
        timestamps.add(this.getOffsetDateTime("dok_ts-statsborgerskab"));
        return timestamps;
    }

    @Override
    public List<Bitemporality> getBitemporality() {
        ArrayList<Bitemporality> bitemporalities = new ArrayList<>();
        bitemporalities.add(this.citizenshipTemporality);
        if (this.documentTemporality != null) {
            bitemporalities.add(this.documentTemporality);
        }
        return bitemporalities;
    }

    @Override
    public Set<PersonEffect> getEffects() {
        HashSet<PersonEffect> effects = new HashSet<>();
        effects.add(new PersonEffect(null, this.getOffsetDateTime("haenstart-statsborgerskab"), this.getBoolean("haenstart_umrk-statsborgerskab"), null, false));
        effects.add(new PersonEffect(null, null, false, null, false));
        return effects;
    }
}

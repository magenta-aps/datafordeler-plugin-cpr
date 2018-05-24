package dk.magenta.datafordeler.cpr.records.person;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.records.Bitemporality;
import org.hibernate.Session;

import java.time.OffsetDateTime;
import java.util.*;

/**
 * Record for Person church relation (type 010).
 */
public class ChurchRecord extends PersonDataRecord {

    private Bitemporality churchTemporality;
    private Bitemporality documentTemporality;

    public ChurchRecord(String line) throws ParseException {
        super(line);
        this.obtain("start_mynkod-folkekirke", 14, 4);
        this.obtain("fkirk_ts", 18, 12);
        this.obtain("fkirk", 30, 1);
        this.obtain("start_dt-folkekirke", 31, 10);
        this.obtain("start_dt-umrk-folkekirke", 41, 1);
        this.obtain("dok_mynkod-folkekirke", 42, 4);
        this.obtain("dok_ts-folkekirke", 46, 12);
        this.obtain("dok-folkekirke", 58, 3);
        this.churchTemporality = new Bitemporality(this.getOffsetDateTime("fkirk_ts"), null, this.getOffsetDateTime("start_dt-folkekirke"), this.getBoolean("start_dt-umrk-folkekirke"), null, false);
        this.documentTemporality = new Bitemporality(this.getOffsetDateTime("dok_ts-folkekirke"));
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_CHURCH;
    }

    @Override
    public boolean populateBaseData(PersonBaseData data, Bitemporality bitemporality, Session session, ImportMetadata importMetadata) {
        boolean updated = false;
        if (bitemporality.equals(this.churchTemporality)) {
            data.setChurch(
                    this.getInt("start_mynkod-folkekirke"),
                    this.getChar("fkirk"),
                    importMetadata.getImportTime()
            );
            updated = true;
        }
        if (bitemporality.equals(this.documentTemporality)) {
            data.setChurchVerification(
                    this.getInt("dok_mynkod-folkekirke"),
                    this.getBoolean("dok-folkekirke"),
                    importMetadata.getImportTime()
            );
            updated = true;
        }
        return updated;
    }

    @Override
    public HashSet<OffsetDateTime> getRegistrationTimestamps() {
        HashSet<OffsetDateTime> timestamps = super.getRegistrationTimestamps();
        timestamps.add(this.churchTemporality.registrationFrom);
        if (this.documentTemporality != null) {
            timestamps.add(this.documentTemporality.registrationFrom);
        }
        return timestamps;
    }

    @Override
    public List<Bitemporality> getBitemporality() {
        ArrayList<Bitemporality> bitemporalities = new ArrayList<>();
        bitemporalities.add(this.churchTemporality);
        if (this.documentTemporality != null) {
            bitemporalities.add(this.documentTemporality);
        }
        return bitemporalities;
    }

    @Override
    public Set<PersonEffect> getEffects() {
        HashSet<PersonEffect> effects = new HashSet<>();
        effects.add(new PersonEffect(null, this.getOffsetDateTime("start_dt-folkekirke"), this.getBoolean("start_dt-umrk-folkekirke"), null, false));
        effects.add(new PersonEffect(null, null, false, null, false));
        return effects;
    }
}

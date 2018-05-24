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
 * Record for Person protection (type 015).
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
    public boolean populateBaseData(PersonBaseData data, Bitemporality bitemporality, Session session, ImportMetadata importMetadata) {
        if (bitemporality.equals(this.protectionTemporality)) {
            data.addProtection(
                    // int authority,
                    this.getInt("start_mynkod-beskyttelse"),
                    // int beskyttelsestype,
                    this.getInt("beskyttype"),
                    // boolean reportMarking
                    this.getBoolean("indrap-beskyttelse"),
                    importMetadata.getImportTime()
            );
            return true;
        }
        return false;
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

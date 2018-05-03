package dk.magenta.datafordeler.cpr.records.person;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.io.ImportMetadata;
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
 * Record for Person foreign address (type 028).
 */
public class HistoricForeignAddressRecord extends PersonDataRecord {

    private Bitemporality emigrationTemporality;
    private Bitemporality foreignAddressTemporality;

    public HistoricForeignAddressRecord(String line) throws ParseException {
        super(line);
        this.obtain("annkor", 14,1);
        this.obtain("start_mynkod-udrindrejse", 16, 4);
        this.obtain("udr_ts", 19, 12);
        this.obtain("udr_landekod", 31, 4);
        this.obtain("udrdto", 35, 12);
        this.obtain("udrdto_umrk", 47, 1);
        this.obtain("indr_ts", 48, 12);
        this.obtain("uindr_landekod", 60, 4);
        this.obtain("indrdto", 64, 12);
        this.obtain("indrdto_umrk", 76, 1);
        this.obtain("udlandadr_mynkod", 77, 4);
        this.obtain("udlandadr_ts", 81, 12);
        this.obtain("udlandadr1", 93, 34);
        this.obtain("udlandadr2", 127, 34);
        this.obtain("udlandadr3", 161, 34);
        this.obtain("udlandadr4", 195, 34);
        this.obtain("udlandadr5", 229, 34);

        OffsetDateTime effectFrom = this.getOffsetDateTime("udrdto");
        boolean effectFromUncertain = this.getMarking("udrdto_umrk");
        OffsetDateTime effectTo = this.getOffsetDateTime("indrdto");
        boolean effectToUncertain = this.getMarking("indrdto_umrk");
        this.emigrationTemporality = new Bitemporality(this.getOffsetDateTime("udr_ts"), null, effectFrom, effectFromUncertain, effectTo, effectToUncertain);
        this.foreignAddressTemporality = new Bitemporality(this.getOffsetDateTime("udlandadr_ts"), null, effectFrom, effectFromUncertain, effectTo, effectToUncertain);
    }

    @Override
    public boolean populateBaseData(PersonBaseData data, Bitemporality bitemporality, Session session, ImportMetadata importMetadata) {
        boolean updated = false;
        if (bitemporality.equals(this.emigrationTemporality)) {
            data.setEmigration(
                    this.getInt("start_mynkod-udrindrejs"),
                    this.getInt("udr_landekod"),
                    importMetadata.getImportTime()
            );
            updated = true;
        }
        if (bitemporality.equals(this.foreignAddressTemporality)) {
            data.setForeignAddress(
                    this.getInt("udlandadr_mynkod"),
                    this.get("udlandadr1"),
                    this.get("udlandadr2"),
                    this.get("udlandadr3"),
                    this.get("udlandadr4"),
                    this.get("udlandadr5"),
                    importMetadata.getImportTime()
            );
            updated = true;
        }
        return updated;
    }


    @Override
    public String getRecordType() {
        return RECORDTYPE_HISTORIC_FOREIGN_ADDRESS;
    }

    @Override
    public HashSet<OffsetDateTime> getRegistrationTimestamps() {
        HashSet<OffsetDateTime> timestamps = super.getRegistrationTimestamps();
        timestamps.add(this.emigrationTemporality.registrationFrom);
        timestamps.add(this.foreignAddressTemporality.registrationFrom);
        return timestamps;
    }

    @Override
    public List<Bitemporality> getBitemporality() {
        return Arrays.asList(
                this.emigrationTemporality,
                this.foreignAddressTemporality
        );
    }

    @Override
    public Set<PersonEffect> getEffects() {
        HashSet<PersonEffect> effects = new HashSet<>();
        effects.add(new PersonEffect(null, this.getOffsetDateTime("udrdto"), this.getMarking("udrdto_umrk"), this.getOffsetDateTime("indrdto"), this.getMarking("indrdto_umrk")));
        return effects;
    }
}

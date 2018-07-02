package dk.magenta.datafordeler.cpr.records.person;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.records.Bitemporality;
import dk.magenta.datafordeler.cpr.records.CprBitemporalRecord;
import dk.magenta.datafordeler.cpr.records.person.data.ForeignAddressDataRecord;
import dk.magenta.datafordeler.cpr.records.person.data.ForeignAddressEmigrationDataRecord;
import org.hibernate.Session;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Record for Person foreign address (type 028).
 */
public class HistoricForeignAddressRecord extends HistoricPersonDataRecord {

    private Bitemporality emigrationTemporality;
    private Bitemporality immigrationTemporality;
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
        this.obtain("indr_landekod", 60, 4);
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
        this.immigrationTemporality = new Bitemporality(this.getOffsetDateTime("indr_ts"), null, effectFrom, effectFromUncertain, effectTo, effectToUncertain);
        this.foreignAddressTemporality = new Bitemporality(this.getOffsetDateTime("udlandadr_ts"), null, effectFrom, effectFromUncertain, effectTo, effectToUncertain);
    }

    @Override
    public boolean populateBaseData(PersonBaseData data, Bitemporality bitemporality, Session session, ImportMetadata importMetadata) {
        boolean updated = false;
        if (this.emigrationTemporality.registrationFrom != null && bitemporality.equals(this.emigrationTemporality)) {
            data.setEmigration(
                    this.getInt("start_mynkod-udrindrejse"),
                    this.getInt("udr_landekod"),
                    importMetadata.getImportTime()
            );
            updated = true;
        }
        if (this.immigrationTemporality.registrationFrom != null && bitemporality.equals(this.immigrationTemporality)) {
            data.setEmigration(
                    this.getInt("start_mynkod-udrindrejse"),
                    this.getInt("indr_landekod"),
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

    /**
     * Delete obsolete data that has been replaced
     * The obsolete data must not match the new bitemporality, but must match it without effectTo
     * @param data
     * @param bitemporality
     * @param outdatedTemporality
     * @param session
     * @return
     */
    @Override
    public boolean cleanBaseData(PersonBaseData data, Bitemporality bitemporality, Bitemporality outdatedTemporality, Session session) {
        boolean updated = false;
        if (bitemporality.equals(this.emigrationTemporality) && outdatedTemporality.equals(this.emigrationTemporality, Bitemporality.EXCLUDE_EFFECT_TO)) {
            data.clearEmigration(session);
            updated = true;
        }
        if (bitemporality.equals(this.immigrationTemporality) && outdatedTemporality.equals(this.immigrationTemporality, Bitemporality.EXCLUDE_EFFECT_TO)) {
            data.clearEmigration(session);
            updated = true;
        }
        if (bitemporality.equals(this.foreignAddressTemporality) && outdatedTemporality.equals(this.foreignAddressTemporality, Bitemporality.EXCLUDE_EFFECT_TO)) {
            data.clearForeignAddress(session);
            updated = true;
        }
        return updated;
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_HISTORIC_FOREIGN_ADDRESS;
    }

    @Override
    public List<CprBitemporalRecord> getBitemporalRecords() {

        ArrayList<CprBitemporalRecord> records = new ArrayList<>();

        records.add(new ForeignAddressDataRecord(
                this.get("udlandadr1"),
                this.get("udlandadr2"),
                this.get("udlandadr3"),
                this.get("udlandadr4"),
                this.get("udlandadr5")
        ).setAuthority(
                this.getInt("udlandadr_mynkod")
        ).setBitemporality(
                this.foreignAddressTemporality
        ).setHistoric());

        records.add(new ForeignAddressEmigrationDataRecord(
                this.getInt("udr_landekod")
        ).setAuthority(
                this.getInt("start_mynkod-udrindrejs")
        ).setBitemporality(
                this.emigrationTemporality
        ).setHistoric());

        return records;
    }

    @Override
    public List<Bitemporality> getBitemporality() {
        ArrayList<Bitemporality> bitemporalities = new ArrayList<>();
        if (this.has("udr_ts") || this.has("udr_landekod")) {
            bitemporalities.add(this.emigrationTemporality);
        }
        if (this.has("indr_ts") || this.has("indr_landekod")) {
            bitemporalities.add(this.immigrationTemporality);
        }
        if (this.has("udlandadr_mynkod") || this.has("udlandadr1")) {
            bitemporalities.add(this.foreignAddressTemporality);
        }
        return bitemporalities;
    }

    @Override
    public Set<PersonEffect> getEffects() {
        HashSet<PersonEffect> effects = new HashSet<>();
        effects.add(new PersonEffect(null, this.getOffsetDateTime("udrdto"), this.getMarking("udrdto_umrk"), this.getOffsetDateTime("indrdto"), this.getMarking("indrdto_umrk")));
        return effects;
    }
}

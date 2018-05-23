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
 * Record for Person historic name (type 021).
 */
public class HistoricNameRecord extends HistoricPersonDataRecord {

    private Bitemporality nameTemporality;
    private Bitemporality addressNameTemporality;
    private Bitemporality documentNameTemporality;
    private Bitemporality officiaryTemporality;

    public HistoricNameRecord(String line) throws ParseException {
        super(line);
        this.obtain("annkor", 14, 1);
        this.obtain("start_mynkod-navne", 15, 4);
        this.obtain("nvn_ts", 19, 12);
        this.obtain("fornvn", 31, 50);
        this.obtain("fornvn_mrk", 81, 1);
        this.obtain("melnvn", 82, 40);
        this.obtain("melnvn_mrk", 122, 1);
        this.obtain("efternvn", 123, 40);
        this.obtain("efternvn_mrk", 163, 1);
        this.obtain("slægtnvn", 164, 40);
        this.obtain("slægtnvn_mrk", 204, 1);
        this.obtain("nvnhaenstart", 205, 12);
        this.obtain("haenstart_umrk-navne", 217, 1);
        this.obtain("nvnhaenslut", 218, 12);
        this.obtain("haenslut_umrk-navne", 230, 1);
        this.obtain("dok_mynkod-navne", 231, 4);
        this.obtain("dok_ts-navne", 235, 12);
        this.obtain("dok-navne", 247, 3);
        this.obtain("myntxt_mynkod-navne", 250, 4);
        this.obtain("myntxt_ts-navne", 254, 12);
        this.obtain("myntxt-navne", 266, 20);

        OffsetDateTime effectFrom = this.getOffsetDateTime("nvnhaenstart");
        boolean effectFromUncertain = this.getMarking("haenstart_umrk-navne");
        OffsetDateTime effectTo = this.getOffsetDateTime("nvnhaenslut");
        boolean effectToUncertain = this.getMarking("haenslut_umrk-navne");
        this.nameTemporality = new Bitemporality(this.getOffsetDateTime("nvn_ts"), null, effectFrom, effectFromUncertain, effectTo, effectToUncertain);
        this.addressNameTemporality = new Bitemporality(this.getOffsetDateTime("adrnvn_ts"), null, effectFrom, effectFromUncertain, effectTo, effectToUncertain);
        this.documentNameTemporality = new Bitemporality(this.getOffsetDateTime("dok_ts-navne"), null, effectFrom, effectFromUncertain, effectTo, effectToUncertain);
        this.officiaryTemporality = new Bitemporality(this.getOffsetDateTime("myntxt_ts-navne"), null, effectFrom, effectFromUncertain, effectTo, effectToUncertain);
    }

    @Override
    public boolean populateBaseData(PersonBaseData data, Bitemporality bitemporality, Session session, ImportMetadata importMetadata) {
        boolean updated = false;
        if (bitemporality.equals(this.nameTemporality)) {
            data.setName(
                    // int authority,
                    this.getInt("start_mynkod-navne"),
                    // String adresseringsnavn,
                    null,
                    // String efternavn,
                    this.get("efternvn"),
                    // String fornavne,
                    this.get("fornvn"),
                    // String mellemnavn,
                    this.get("melnvn"),
                    // boolean efternavnMarkering,
                    this.getMarking("efternvn_mrk"),
                    // boolean fornavneMarkering,
                    this.getMarking("fornvn_mrk"),
                    // boolean mellemnavnMarkering,
                    this.getMarking("melnvn_mrk"),
                    // String egetEfternavn,
                    this.get("slægtsnvn"),
                    // boolean ownLastNameMarking,
                    this.getMarking("slægtsnvn_mrk"),
                    // boolean reportNames
                    false,
                    importMetadata.getImportTime()
            );
            updated = true;
        }
        if (bitemporality.equals(this.addressNameTemporality)) {
            data.setAddressName(
                    // int authority,
                    this.getInt("adrnvn_mynkod"),
                    // String addressName
                    this.get("adrnvn"),
                    importMetadata.getImportTime()
            );
            updated = true;
        }
        if (bitemporality.equals(this.documentNameTemporality)) {
            data.setNameVerification(
                    // int authority,
                    this.getInt("dok_mynkod-navne"),
                    // boolean verification
                    this.getBoolean("dok-navne"),
                    importMetadata.getImportTime()
            );
            updated = true;
        }
        if (bitemporality.equals(this.officiaryTemporality)) {
            data.setNameAuthorityText(
                    // int authority,
                    this.getInt("myntxt_mynkod-navne"),
                    // String text
                    this.get("myntxt-navne"),
                    importMetadata.getImportTime()
            );
            updated = true;
        }
        return updated;
    }

    @Override
    public boolean cleanBaseData(PersonBaseData data, Bitemporality bitemporality, Bitemporality outdatedTemporality, Session session) {
        boolean updated = false;
        if (bitemporality.equals(this.nameTemporality) && outdatedTemporality.equals(this.nameTemporality, Bitemporality.EXCLUDE_EFFECT_TO)) {
            data.clearName(session);
            updated = true;
        }
        if (bitemporality.equals(this.addressNameTemporality) && outdatedTemporality.equals(this.addressNameTemporality, Bitemporality.EXCLUDE_EFFECT_TO)) {
            data.clearAddressName(session);
            updated = true;
        }
        if (bitemporality.equals(this.documentNameTemporality) && outdatedTemporality.equals(this.documentNameTemporality, Bitemporality.EXCLUDE_EFFECT_TO)) {
            data.clearNameVerification(session);
            updated = true;
        }
        if (bitemporality.equals(this.officiaryTemporality) && outdatedTemporality.equals(this.officiaryTemporality, Bitemporality.EXCLUDE_EFFECT_TO)) {
            data.clearNameAuthorityText(session);
            updated = true;
        }
        return updated;
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_HISTORIC_NAME;
    }

    @Override
    public HashSet<OffsetDateTime> getRegistrationTimestamps() {
        HashSet<OffsetDateTime> timestamps = super.getRegistrationTimestamps();
        timestamps.add(this.nameTemporality.registrationFrom);
        timestamps.add(this.addressNameTemporality.registrationFrom);
        timestamps.add(this.documentNameTemporality.registrationFrom);
        timestamps.add(this.officiaryTemporality.registrationFrom);
        return timestamps;
    }

    @Override
    public List<Bitemporality> getBitemporality() {
        return Arrays.asList(
                this.nameTemporality,
                this.addressNameTemporality,
                this.documentNameTemporality,
                this.officiaryTemporality
        );
    }

    @Override
    public Set<PersonEffect> getEffects() {
        HashSet<PersonEffect> effects = new HashSet<>();
        effects.add(new PersonEffect(null, this.getOffsetDateTime("nvnhaenstart"), this.getMarking("haenstart_umrk-navne"), this.getOffsetDateTime("nvnhaenslut"), this.getMarking("haenslut_umrk-navne")));
        return effects;
    }
}

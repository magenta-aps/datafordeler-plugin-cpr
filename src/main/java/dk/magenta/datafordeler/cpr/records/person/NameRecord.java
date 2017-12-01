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
 * Created by lars on 22-06-17.
 */
public class NameRecord extends PersonDataRecord {

    private Bitemporality nameTemporality;
    private Bitemporality addressNameTemporality;
    private Bitemporality documentNameTemporality;
    private Bitemporality officiaryTemporality;

    public NameRecord(String line) throws ParseException {
        super(line);
        this.obtain("start_mynkod-navne", 14, 4);
        this.obtain("nvn_ts", 18, 12);
        this.obtain("fornvn", 30, 50);
        this.obtain("fornvn_mrk", 80, 1);
        this.obtain("melnvn", 81, 40);
        this.obtain("melnvn_mrk", 121, 1);
        this.obtain("efternvn", 122, 40);
        this.obtain("efternvn_mrk", 162, 1);
        this.obtain("slægtnvn", 163, 40);
        this.obtain("slægtnvn_mrk", 203, 1);
        this.obtain("nvnhaenstart", 204, 12);
        this.obtain("haenstart_umrk-navne", 216, 1);
        this.obtain("adrnvn_mynkod", 217, 4);
        this.obtain("adrnvn_ts", 221, 12);
        this.obtain("adrnvn", 233, 34);
        this.obtain("indrap-navne", 267, 3);
        this.obtain("dok_mynkod-navne", 270, 4);
        this.obtain("dok_ts-navne", 274, 12);
        this.obtain("dok-navne", 286, 3);
        this.obtain("myntxt_mynkod-navne", 289, 4);
        this.obtain("myntxt_ts-navne", 293, 12);
        this.obtain("myntxt-navne", 305, 20);

        this.nameTemporality = new Bitemporality(this.getOffsetDateTime("nvn_ts"), null, this.getOffsetDateTime("nvnhaenstart"), this.getBoolean("haenstart_umrk-navne"), null, false);
        this.addressNameTemporality = new Bitemporality(this.getOffsetDateTime("adrnvn_ts"));
        this.documentNameTemporality = new Bitemporality(this.getOffsetDateTime("dok_ts-navne"));
        this.officiaryTemporality = new Bitemporality(this.getOffsetDateTime("myntxt_ts-navne"));
    }

    @Override
    public boolean populateBaseData(PersonBaseData data, PersonEffect effect, OffsetDateTime registrationTime, Session session) {
        boolean updated = false;
        if (this.nameTemporality.matches(registrationTime, effect)) {
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
                this.getBoolean("indrap-navne")
            );
            updated = true;
        }
        if (this.addressNameTemporality.matches(registrationTime, effect)) {
            data.setAddressName(
                // int authority,
                this.getInt("adrnvn_mynkod"),
                // String addressName
                this.get("adrnvn")
            );
            updated = true;
        }
        if (this.documentNameTemporality.matches(registrationTime, effect)) {
            data.setNameVerification(
                // int authority,
                this.getInt("dok_mynkod-navne"),
                // boolean verification
                this.getBoolean("dok-navne")
            );
            updated = true;
        }
        if (this.officiaryTemporality.matches(registrationTime, effect)) {
            data.setNameAuthorityText(
                // int authority,
                this.getInt("myntxt_mynkod-navne"),
                // String text
                this.get("myntxt-navne")
            );
            updated = true;
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
        effects.add(new PersonEffect(null, this.getOffsetDateTime("nvnhaenstart"), this.getMarking("haenstart_umrk-navne"), null, false));
        effects.add(new PersonEffect(null, null, false, null, false));
        return effects;
    }
}

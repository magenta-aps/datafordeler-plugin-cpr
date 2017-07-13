package dk.magenta.datafordeler.cpr.records.person;

import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import org.hibernate.Session;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lars on 22-06-17.
 */
public class NameRecord extends PersonDataRecord {
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
    }

    @Override
    public void populateBaseData(PersonBaseData data, PersonEffect effect, OffsetDateTime registrationTime, QueryManager queryManager, Session session) {
        if (registrationTime.equals(this.getOffsetDateTime("nvn_ts")) && effect.compareRange(this.getOffsetDateTime("nvnhaenstart"), this.getBoolean("haenstart_umrk-navne"), null, false)) {
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
        }
        if (registrationTime.equals(this.getOffsetDateTime("adrnvn_ts")) && effect.compareRange(null, false, null, false)) {
            data.setAddressName(
                // int authority,
                this.getInt("adrnvn_mynkod"),
                // String addressName
                this.get("adrnvn")
            );
        }
        if (registrationTime.equals(this.getOffsetDateTime("dok_ts-navne")) && effect.compareRange(null, false, null, false)) {
            data.setNameVerification(
                // int authority,
                this.getInt("dok_mynkod-navne"),
                // boolean verification
                this.getBoolean("dok-navne")
            );
        }
        if (registrationTime.equals(this.getOffsetDateTime("myntxt_ts-navne")) && effect.compareRange(null, false, null, false)) {
            data.setNameAuthorityText(
                // int authority,
                this.getInt("myntxt_mynkod-navne"),
                // String text
                this.get("myntxt-navne")
            );
        }
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_CURRENT_NAME;
    }

    @Override
    public HashSet<OffsetDateTime> getRegistrationTimestamps() {
        HashSet<OffsetDateTime> timestamps = super.getRegistrationTimestamps();
        timestamps.add(this.getOffsetDateTime("nvn_ts"));
        timestamps.add(this.getOffsetDateTime("adrnvn_ts"));
        timestamps.add(this.getOffsetDateTime("dok_ts-navne"));
        timestamps.add(this.getOffsetDateTime("myntxt_ts-navne"));
        return timestamps;
    }

    @Override
    public Set<PersonEffect> getEffects() {
        HashSet<PersonEffect> effects = new HashSet<>();
        effects.add(new PersonEffect(null, this.getOffsetDateTime("nvnhaenstart"), this.getMarking("haenstart_umrk-navne"), null, false));
        effects.add(new PersonEffect(null, null, false, null, false));
        return effects;
    }
}

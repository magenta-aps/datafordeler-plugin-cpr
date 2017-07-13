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
public class HistoricNameRecord extends PersonDataRecord {

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
    }

    @Override
    public void populateBaseData(PersonBaseData data, PersonEffect effect, OffsetDateTime registrationTime, QueryManager queryManager, Session session) {

        if (registrationTime.equals(this.getOffsetDateTime("nvn_ts"))) {
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
                false
            );
        }
        if (registrationTime.equals(this.getOffsetDateTime("adrnvn_ts"))) {
            data.setAddressName(
                // int authority,
                this.getInt("adrnvn_mynkod"),
                // String addressName
                this.get("adrnvn")
            );
        }
        if (registrationTime.equals(this.getOffsetDateTime("dok_ts-navne"))) {
            data.setNameVerification(
                // int authority,
                this.getInt("dok_mynkod-navne"),
                // boolean verification
                this.getBoolean("dok-navne")
            );
        }
        if (registrationTime.equals(this.getOffsetDateTime("myntxt_ts-navne"))) {
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
        return RECORDTYPE_HISTORIC_NAME;
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
        effects.add(new PersonEffect(null, this.getOffsetDateTime("nvnhaenstart"), this.getMarking("haenstart_umrk-navne"), this.getOffsetDateTime("nvnhaenslut"), this.getMarking("haenslut_umrk-navne")));
        return effects;
    }
}

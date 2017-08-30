package dk.magenta.datafordeler.cpr.records.person;

import dk.magenta.datafordeler.core.database.QueryManager;
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
    public void populateBaseData(PersonBaseData data, PersonEffect effect, OffsetDateTime registrationTime, QueryManager queryManager, Session session) {
        if (this.nameTemporality.matches(registrationTime, effect)) {
            data.setName(
                    this.getInt("start_mynkod-navne"),
                    this.get("fornvn"),
                    this.getMarking("fornvn_mrk"),
                    this.get("melnvn"),
                    this.getMarking("melnvn_mrk"),
                    this.get("efternvn"),
                    this.getMarking("efternvn_mrk"),
                    this.get("slægtsnvn"),
                    this.getMarking("slægtsnvn_mrk"),
                    this.getBoolean("indrap-navne")
            );
        }
        if (this.addressNameTemporality.matches(registrationTime, effect)) {
            data.setAddressName(
                    this.getInt("adrnvn_mynkod"),
                    this.get("adrnvn")
            );
        }
        if (this.documentNameTemporality.matches(registrationTime, effect)) {
            data.setNameVerification(
                    this.getInt("dok_mynkod-navne"),
                    this.getBoolean("dok-navne")
            );
        }
        if (this.officiaryTemporality.matches(registrationTime, effect)) {
            data.setNameAuthorityText(
                    this.getInt("myntxt_mynkod-navne"),
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

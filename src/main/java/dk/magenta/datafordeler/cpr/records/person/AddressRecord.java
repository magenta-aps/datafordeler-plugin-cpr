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
public class AddressRecord extends PersonDataRecord {

    private Bitemporality addressTemporality;
    private Bitemporality conameTemporality;
    private Bitemporality municipalityTemporality;

    public AddressRecord(String line) throws ParseException {
        super(line);
        this.obtain("start_mynkod-personbolig", 14, 4);
        this.obtain("adr_ts", 18, 12);
        this.obtain("komkod", 30, 4);
        this.obtain("vejkod", 34, 4);
        this.obtain("husnr", 38, 4);
        this.obtain("etage", 42, 2);
        this.obtain("sidedoer", 44, 4);
        this.obtain("bnr", 48, 4);
        this.obtain("convn", 52, 34);
        this.obtain("convn_ts", 86, 12);
        this.obtain("tilflydto", 98, 12);
        this.obtain("tilflydto_umrk", 110, 1);
        this.obtain("tilfra_mynkod", 111, 4);
        this.obtain("tilfra_ts", 115, 12);
        this.obtain("tilflykomdto", 127, 12);
        this.obtain("tilflykomdt_umrk", 139, 1);
        this.obtain("fraflykomkod", 140, 4);
        this.obtain("fraflykomdto", 144, 12);
        this.obtain("fraflykomdt_umrk", 156, 1);
        this.obtain("adrtxttype", 157, 4);
        this.obtain("start_mynkod-adrtxt", 161, 4);
        this.obtain("adr1-supladr", 165, 34);
        this.obtain("adr2-supladr", 199, 34);
        this.obtain("adr3-supladr", 233, 34);
        this.obtain("adr4-supladr", 267, 34);
        this.obtain("adr5-supladr", 301, 34);
        this.obtain("start_dt-adrtxt", 335, 10);
        this.obtain("slet_dt-adrtxt", 345, 10);

        this.addressTemporality = new Bitemporality(this.getOffsetDateTime("adr_ts"), null, this.getOffsetDateTime("tilflydto"), this.getBoolean("tilflydto_umrk"), null, false);
        this.conameTemporality = new Bitemporality(this.getOffsetDateTime("convn_ts"));
        this.municipalityTemporality = new Bitemporality(this.getOffsetDateTime("tilfra_ts"));
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_DOMESTIC_ADDRESS;
    }

    @Override
    public void populateBaseData(PersonBaseData data, PersonEffect effect, OffsetDateTime registrationTime, QueryManager queryManager, Session session) {
        if (this.addressTemporality.matches(registrationTime, effect)) {
            data.setAddress(
                // int authority,
                this.getInt("start_mynkod-personbolig"),
                // String bygningsnummer,
                this.get("bnr"),
                // String bynavn,
                null,
                // String cprKommunekode,
                this.getString("komkod", false),
                // String cprKommunenavn,
                null,
                // String cprVejkode,
                this.getString("vejkod", false),
                // String darAdresse,
                null,
                // String etage,
                this.get("etage"),
                // String husnummer,
                this.get("husnr"),
                // String postdistrikt,
                null,
                // String postnummer,
                null,
                // String sideDoer,
                this.get("sidedoer"),
                // String adresselinie1,
                this.get("adr1-supladr"),
                // String adresselinie2,
                this.get("adr2-supladr"),
                // String adresselinie3,
                this.get("adr3-supladr"),
                // String adresselinie4,
                this.get("adr4-supladr"),
                // String adresselinie5,
                this.get("adr5-supladr"),
                // int addressTextType,
                this.getInt("adrtxttype"),
                // int startAuthority
                this.getInt("start_mynkod-adrtxt")
            );
        }
        if (this.conameTemporality.matches(registrationTime, effect)) {
            data.setCoName(this.get("convn"));
        }
        if (this.municipalityTemporality.matches(registrationTime, effect)) {
            data.setMoveMunicipality(
                //int authority,
                this.getInt("tilfra_mynkod"),
                // LocalDateTime fraflytningsdatoKommune,
                this.getDateTime("tilflykomdto"),
                // boolean fraflytningsdatoKommuneUsikkerhedsmarkering,
                this.getBoolean("tilflykomdt_umrk"),
                // int fraflytningskommunekode,
                this.getInt("fraflykomkod"),
                // LocalDateTime tilflytningsdatoKommune,
                this.getDateTime("fraflykomdto"),
                // boolean tilflytningsdatoKommuneUsikkerhedsmarkering
                this.getBoolean("fraflykomdt_umrk")
            );
        }
    }

    @Override
    public HashSet<OffsetDateTime> getRegistrationTimestamps() {
        HashSet<OffsetDateTime> timestamps = super.getRegistrationTimestamps();
        timestamps.add(this.getOffsetDateTime("adr_ts"));
        timestamps.add(this.getOffsetDateTime("convn_ts"));
        timestamps.add(this.getOffsetDateTime("tilfra_ts"));
        return timestamps;
    }

    @Override
    public List<Bitemporality> getBitemporality() {
        return Arrays.asList(
                this.addressTemporality,
                this.conameTemporality,
                this.municipalityTemporality
        );
    }

    @Override
    public Set<PersonEffect> getEffects() {
        HashSet<PersonEffect> effects = new HashSet<>();
        effects.add(new PersonEffect(null, this.getOffsetDateTime("tilflydto"), this.getMarking("tilflydto_umrk"), null, false));
        effects.add(new PersonEffect(null, null, false, null, false));
        return effects;
    }
}

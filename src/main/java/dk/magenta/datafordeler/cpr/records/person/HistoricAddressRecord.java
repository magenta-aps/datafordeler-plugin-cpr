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
 * Record for Person historic address (type 026).
 */
public class HistoricAddressRecord extends HistoricPersonDataRecord {

    private Bitemporality addressTemporality;
    private Bitemporality conameTemporality;
    private Bitemporality municipalityTemporality;

    public HistoricAddressRecord(String line) throws ParseException {
        super(line);
        this.obtain("annkor", 14, 1);
        this.obtain("start_mynkod-personbolig", 15, 4);
        this.obtain("adr_ts", 19, 12);
        this.obtain("komkod", 31, 4);
        this.obtain("vejkod", 35, 4);
        this.obtain("husnr", 39, 4);
        this.obtain("etage", 43, 2);
        this.obtain("sidedoer", 45, 4);
        this.obtain("bnr", 49, 4);
        this.obtain("convn", 53, 34);
        this.obtain("convn_ts", 87, 12);
        this.obtain("tilflydto", 99, 12);
        this.obtain("tilflydto_umrk", 111, 1);
        this.obtain("fraflydto", 112, 12);
        this.obtain("fraflydto_umrk", 124, 1);
        this.obtain("tilfra_mynkod", 125, 4);
        this.obtain("tilfra_ts", 129, 12);
        this.obtain("tilflykomdto", 141, 12);
        this.obtain("tilflykomdt_umrk", 153, 1);
        this.obtain("fraflykomkod", 154, 4);
        this.obtain("fraflykomdto", 158, 12);
        this.obtain("fraflykomdt_umrk", 170, 1);

        this.addressTemporality = new Bitemporality(this.getOffsetDateTime("adr_ts"), null, this.getOffsetDateTime("tilflydto"), this.getBoolean("tilflydto_umrk"), this.getOffsetDateTime("fraflydto"), this.getBoolean("fraflydto_umrk"));
        this.conameTemporality = new Bitemporality(this.getOffsetDateTime("convn_ts"));
        this.municipalityTemporality = new Bitemporality(this.getOffsetDateTime("tilfra_ts"));
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_HISTORIC_DOMESTIC_ADDRESS;
    }

    @Override
    public boolean populateBaseData(PersonBaseData data, Bitemporality bitemporality, Session session, ImportMetadata importMetadata) {
        boolean updated = false;

        if (bitemporality.equals(this.addressTemporality)) {
            data.setAddress(
                    // int authority,
                    this.getInt("start_mynkod-personbolig"),
                    // String bygningsnummer,
                    this.getString("bnr", true),
                    // String bynavn,
                    null,
                    // String cprKommunekode,
                    this.getInt("komkod", false),
                    // String cprKommunenavn,
                    null,
                    // String cprVejkode,
                    this.getInt("vejkod", false),
                    // String darAdresse,
                    null,
                    // String etage,
                    this.get("etage"),
                    // String husnummer,
                    this.getString("husnr", true),
                    // String postdistrikt,
                    null,
                    // String postnummer,
                    null,
                    // String sideDoer,
                    this.get("sidedoer"),
                    // String adresselinie1,
                    null,
                    // String adresselinie2,
                    null,
                    // String adresselinie3,
                    null,
                    // String adresselinie4,
                    null,
                    // String adresselinie5,
                    null,
                    // int addressTextType,
                    0,
                    // int startAuthority
                    0,
                    importMetadata.getImportTime()
            );
            updated = true;
        }

        if (bitemporality.equals(this.conameTemporality)) {
            data.setCoName(
                    this.get("convn"),
                    importMetadata.getImportTime()
            );
            updated = true;
        }
        if (bitemporality.equals(this.municipalityTemporality)) {
            data.setMoveMunicipality(
                    //int authority,
                    this.getInt("tilfra_mynkod"),
                    // LocalDateTime fraflytningsdatoKommune,
                    this.getDateTime("fraflykomdto"),
                    // boolean fraflytningsdatoKommuneUsikkerhedsmarkering,
                    this.getBoolean("fraflykomdt_umrk"),
                    // int fraflytningskommunekode,
                    this.getInt("fraflykomkod"),
                    // LocalDateTime tilflytningsdatoKommune,
                    this.getDateTime("tilflykomdto"),
                    // boolean tilflytningsdatoKommuneUsikkerhedsmarkering
                    this.getBoolean("tilflykomdt_umrk"),
                    importMetadata.getImportTime()
            );
            updated = true;
        }
        return updated;
    }

    @Override
    public boolean cleanBaseData(PersonBaseData data, Bitemporality bitemporality, Bitemporality outdatedTemporality, Session session) {
        boolean updated = false;
        if (bitemporality.equals(this.addressTemporality) && outdatedTemporality.equals(this.addressTemporality, Bitemporality.EXCLUDE_EFFECT_TO)) {
            data.clearAddress(session);
            updated = true;
        }
        if (bitemporality.equals(this.conameTemporality) && outdatedTemporality.equals(this.conameTemporality, Bitemporality.EXCLUDE_EFFECT_TO)) {
            data.clearCoName(session);
            updated = true;
        }
        if (bitemporality.equals(this.municipalityTemporality) && outdatedTemporality.equals(this.municipalityTemporality, Bitemporality.EXCLUDE_EFFECT_TO)) {
            data.clearMoveMunicipality(session);
            updated = true;
        }
        return updated;
    }

    @Override
    public HashSet<OffsetDateTime> getRegistrationTimestamps() {
        HashSet<OffsetDateTime> timestamps = super.getRegistrationTimestamps();
        timestamps.add(this.addressTemporality.registrationFrom);
        timestamps.add(this.conameTemporality.registrationFrom);
        timestamps.add(this.municipalityTemporality.registrationFrom);
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
        effects.add(new PersonEffect(null, this.getOffsetDateTime("tilflydto"), this.getMarking("tilflydto_umrk"), this.getOffsetDateTime("fraflydto"), this.getMarking("fraflydto_umrk")));
        effects.add(new PersonEffect(null, null, false, null, false));
        return effects;
    }

    public int getMunicipalityCode() {
        return this.getInt("komkod");
    }
}

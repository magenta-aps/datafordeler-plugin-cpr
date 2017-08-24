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
 * Created by lars on 27-06-17.
 */
public class ForeignAddressRecord extends PersonDataRecord {

    private Bitemporality emigrationTemporality;
    private Bitemporality foreignAddressTemporality;

    public ForeignAddressRecord(String line) throws ParseException {
        super(line);
        this.obtain("start_mynkod-udrindrejse", 14, 4);
        this.obtain("udr_ts", 18, 12);
        this.obtain("udr_landekod", 30, 4);
        this.obtain("udrdto", 34, 12);
        this.obtain("udrdto_umrk", 46, 1);
        this.obtain("udlandadr_mynkod", 47, 4);
        this.obtain("udlandadr_ts", 51, 12);
        this.obtain("udlandadr1", 63, 34);
        this.obtain("udlandadr2", 97, 34);
        this.obtain("udlandadr3", 131, 34);
        this.obtain("udlandadr4", 165, 34);
        this.obtain("udlandadr5", 199, 34);

        OffsetDateTime effectFrom = this.getOffsetDateTime("udrdto");
        boolean effectFromUncertain = this.getMarking("udrdto_umrk");
        this.emigrationTemporality = new Bitemporality(this.getOffsetDateTime("udr_ts"), null, effectFrom, effectFromUncertain, null, false);
        this.foreignAddressTemporality = new Bitemporality(this.getOffsetDateTime("udlandadr_ts"), null, effectFrom, effectFromUncertain, null, false);
    }

    @Override
    public void populateBaseData(PersonBaseData data, PersonEffect effect, OffsetDateTime registrationTime, QueryManager queryManager, Session session) {
        if (this.emigrationTemporality.matches(registrationTime, effect)) {
            data.setEmigration(this.getInt("start_mynkod-udrindrejs"), this.getInt("udr_landekod"));
        }
        if (this.foreignAddressTemporality.matches(registrationTime, effect)) {
            data.setForeignAddress(this.getInt("udlandadr_mynkod"), this.get("udlandadr1"), this.get("udlandadr2"), this.get("udlandadr3"), this.get("udlandadr4"), this.get("udlandadr5"));
        }
    }


    @Override
    public String getRecordType() {
        return RECORDTYPE_FOREIGN_ADDRESS;
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
        effects.add(new PersonEffect(null, this.getOffsetDateTime("udrdto"), this.getMarking("udrdto_umrk"), null, false));
        return effects;
    }
}

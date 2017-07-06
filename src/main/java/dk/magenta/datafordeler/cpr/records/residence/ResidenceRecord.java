package dk.magenta.datafordeler.cpr.records.residence;

import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceEffect;
import dk.magenta.datafordeler.cpr.data.residence.data.ResidenceBaseData;
import dk.magenta.datafordeler.cpr.records.CprDataRecord;
import org.hibernate.Session;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lars on 29-06-17.
 */
public class ResidenceRecord extends CprDataRecord<ResidenceEffect, ResidenceBaseData> {

    public static final String RECORDTYPE_RESIDENCE = "002";

    public ResidenceRecord(String line) throws ParseException {
        super(line);
        this.obtain("komkod", 4, 4);
        this.obtain("vejkod", 8, 4);
        this.obtain("husnr", 12, 4);
        this.obtain("etage", 16, 2);
        this.obtain("sidedoer", 18, 4);
        this.obtain("timestamp", 22, 12);
        this.obtain("haenstart", 35, 12);
        this.obtain("lokalitet", 59, 34);
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_RESIDENCE;
    }

    @Override
    public void populateBaseData(ResidenceBaseData data, ResidenceEffect effect, OffsetDateTime registrationTime, QueryManager queryManager, Session session) {
        if (registrationTime.equals(this.getOffsetDateTime("timestamp")) && effect.compareRange(this.getOffsetDateTime("haenstart"), false, null, false)) {
            data.setKommunekode(this.getInt("komkod"));
            data.setVejkode(this.getInt("vejkod"));
            data.setHusnummer(this.getString("husnr", true));
            data.setEtage(this.getString("etage", true));
            data.setSideDoer(this.getString("sidedoer", true));
        }
    }

    @Override
    protected ResidenceEffect createEffect(OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        ResidenceEffect effect = new ResidenceEffect(null, effectFrom, effectTo);
        effect.setUncertainFrom(effectFromUncertain);
        effect.setUncertainTo(effectToUncertain);
        return effect;
    }

    @Override
    protected ResidenceBaseData createEmptyBaseData() {
        return new ResidenceBaseData();
    }

    @Override
    public Set<ResidenceEffect> getEffects() {
        HashSet<ResidenceEffect> effects = new HashSet<>();
        effects.add(new ResidenceEffect(null, this.getOffsetDateTime("haenstart"), false, null, false));
        return effects;
    }

    @Override
    public HashSet<OffsetDateTime> getRegistrationTimestamps() {
        HashSet<OffsetDateTime> timestamps = new HashSet<>();
        timestamps.add(this.getOffsetDateTime("timestamp"));
        return timestamps;
    }
}

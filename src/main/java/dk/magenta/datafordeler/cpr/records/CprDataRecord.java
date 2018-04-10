package dk.magenta.datafordeler.cpr.records;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.cpr.data.CprData;
import dk.magenta.datafordeler.cpr.data.CprEffect;
import org.hibernate.Session;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Superclass for line records.
 */
public abstract class CprDataRecord<V extends CprEffect, B extends CprData> extends Record {

    public CprDataRecord(String line) throws ParseException {
        super(line);
    }

    public abstract boolean populateBaseData(B data, V effect, OffsetDateTime registrationTime, Session session, ImportMetadata importMetadata);


    protected B getBaseDataItem(HashMap<V, B> data) {
        return this.getBaseDataItem(data, null, false, null, false);
    }

    protected B getBaseDataItem(HashMap<V, B> data, OffsetDateTime effectFrom, boolean effectFromUncertain) {
        return this.getBaseDataItem(data, effectFrom, effectFromUncertain, null, false);
    }

    protected B getBaseDataItem(HashMap<V, B> data, OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        V effect = null;
        for (V e : data.keySet()) {
            if (e.compareRange(effectFrom, effectFromUncertain, effectTo, effectToUncertain)) {
                effect = e;
                break;
            }
        }
        if (effect == null) {
            effect = this.createEffect(effectFrom, effectFromUncertain, effectTo, effectToUncertain);
            data.put(effect, this.createEmptyBaseData());
        }
        return data.get(effect);
    }

    protected abstract V createEffect(OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain);

    protected abstract B createEmptyBaseData();

    public abstract Set<V> getEffects();

    public abstract Set<OffsetDateTime> getRegistrationTimestamps();

    public abstract List<Bitemporality> getBitemporality();

    public boolean filter() {
        return true;
    }

}
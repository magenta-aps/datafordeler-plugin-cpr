package dk.magenta.datafordeler.cpr.records.road;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.util.DoubleHashMap;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.data.CprData;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.road.RoadEffect;
import dk.magenta.datafordeler.cpr.records.CprRecord;

import java.time.OffsetDateTime;
import java.util.HashSet;

/**
 * Created by lars on 22-06-17.
 */
public abstract class RoadDataRecord<B extends CprData> extends CprRecord {

    public static final String RECORDTYPE_ROAD = "001";
    // TODO: Add one for each data type

    public RoadDataRecord(String line) throws ParseException {
        super(line);
        this.obtain("komkod", 4, 4);
        this.obtain("vejkod", 8, 4);
    }

    protected int getTimestampStart() {
        return 21;
    }

    public int getRoadCode() {
        return this.getInt("vejkod");
    }

    public HashSet<String> getTimestamps() {
        return new HashSet<>();
    }

    protected B getBaseDataItem(DoubleHashMap<String, String, B> data, String effectStart, String effectEnd) {
        effectStart = CprRecord.normalizeDate(effectStart);
        effectEnd = CprRecord.normalizeDate(effectEnd);
        B personBaseData = data.get(effectStart, effectEnd);
        if (personBaseData == null) {
            personBaseData = this.createEmptyBaseData();
            data.put(effectStart, effectEnd, personBaseData);
        }
        return personBaseData;
    }

    protected B getBaseDataItem(ListHashMap<RoadEffect, B> data) {
        return this.getBaseDataItem(data, null, false, null, false);
    }

    protected B getBaseDataItem(ListHashMap<RoadEffect, B> data, OffsetDateTime effectFrom, boolean effectFromUncertain) {
        return this.getBaseDataItem(data, effectFrom, effectFromUncertain, null, false);
    }

    protected B getBaseDataItem(ListHashMap<RoadEffect, B> data, OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        B personBaseData = this.createEmptyBaseData();
        RoadEffect effect = new RoadEffect(null, effectFrom, effectTo);
        effect.setUncertainFrom(effectFromUncertain);
        effect.setUncertainTo(effectToUncertain);
        data.add(effect, personBaseData);
        return personBaseData;
    }

    protected abstract B createEmptyBaseData();
}

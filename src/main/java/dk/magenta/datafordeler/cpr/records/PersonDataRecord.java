package dk.magenta.datafordeler.cpr.records;

import dk.magenta.datafordeler.core.util.DoubleHashMap;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.data.CprData;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;

import java.text.ParseException;
import java.time.OffsetDateTime;
import java.util.HashSet;

/**
 * Created by lars on 22-06-17.
 */
public abstract class PersonDataRecord<B extends CprData> extends CprRecord {

    public static final String RECORDTYPE_PERSON = "001";
    public static final String RECORDTYPE_PROTECTION = "015";
    public static final String RECORDTYPE_CURRENT_NAME = "020";
    public static final String RECORDTYPE_HISTORIC_NAME = "021";
    public static final String RECORDTYPE_DOMESTIC_ADDRESS = "025";
    // TODO: Add one for each data type

    public PersonDataRecord(String line) throws ParseException {
        super(line);
        this.obtain("pnr", 4, 10);
    }

    protected int getTimestampStart() {
        return 21;
    }

    public int getCprNumber() {
        return Integer.parseInt(this.get("pnr"));
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

    protected B getBaseDataItem(ListHashMap<PersonEffect, B> data) {
        return this.getBaseDataItem(data, null, false, null, false);
    }

    protected B getBaseDataItem(ListHashMap<PersonEffect, B> data, OffsetDateTime effectFrom, boolean effectFromUncertain) {
        return this.getBaseDataItem(data, effectFrom, effectFromUncertain, null, false);
    }

    protected B getBaseDataItem(ListHashMap<PersonEffect, B> data, OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        B personBaseData = this.createEmptyBaseData();
        PersonEffect effect = new PersonEffect(null, effectFrom, effectTo);
        effect.setUncertainFrom(effectFromUncertain);
        effect.setUncertainTo(effectToUncertain);
        data.add(effect, personBaseData);
        return personBaseData;
    }

    protected abstract B createEmptyBaseData();
}

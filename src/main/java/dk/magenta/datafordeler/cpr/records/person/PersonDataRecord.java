package dk.magenta.datafordeler.cpr.records.person;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.records.CprDataRecord;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by lars on 22-06-17.
 */
public abstract class PersonDataRecord extends CprDataRecord<PersonEffect, PersonBaseData> {

    public static final String RECORDTYPE_PERSON = "001";
    public static final String RECORDTYPE_PROTECTION = "015";
    public static final String RECORDTYPE_CURRENT_NAME = "020";
    public static final String RECORDTYPE_HISTORIC_NAME = "021";
    public static final String RECORDTYPE_DOMESTIC_ADDRESS = "025";
    public static final String RECORDTYPE_FOREIGN_ADDRESS = "028";
    // TODO: Add one for each data type

    public PersonDataRecord(String line) throws ParseException {
        super(line);
        this.obtain("pnr", 4, 10);
    }

    protected int getTimestampStart() {
        return 21;
    }

    public String getCprNumber() {
        return this.get("pnr");
    }

    public HashSet<OffsetDateTime> getRegistrationTimestamps() {
        return new HashSet<>();
    }

    protected PersonBaseData getBaseDataItem(HashMap<PersonEffect, PersonBaseData> data) {
        return this.getBaseDataItem(data, null, false, null, false);
    }

    protected PersonBaseData getBaseDataItem(HashMap<PersonEffect, PersonBaseData> data, OffsetDateTime effectFrom, boolean effectFromUncertain) {
        return this.getBaseDataItem(data, effectFrom, effectFromUncertain, null, false);
    }

    protected PersonBaseData getBaseDataItem(HashMap<PersonEffect, PersonBaseData> data, OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        PersonEffect effect = null;
        for (PersonEffect e : data.keySet()) {
            if (e.compareRange(effectFrom, effectFromUncertain, effectTo, effectToUncertain)) {
                effect = e;
                break;
            }
        }
        if (effect == null) {
            effect = new PersonEffect(null, effectFrom, effectTo);
            effect.setVirkningFraUsikkerhedsmarkering(effectFromUncertain);
            effect.setVirkningTilUsikkerhedsmarkering(effectToUncertain);
            data.put(effect, this.createEmptyBaseData());
        }
        return data.get(effect);
    }

    protected PersonBaseData createEmptyBaseData() {
        return new PersonBaseData();
    }

    @Override
    protected PersonEffect createEffect(OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        PersonEffect effect = new PersonEffect(null, effectFrom, effectTo);
        effect.setVirkningFraUsikkerhedsmarkering(effectFromUncertain);
        effect.setVirkningTilUsikkerhedsmarkering(effectToUncertain);
        return effect;
    }
}

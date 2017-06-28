package dk.magenta.datafordeler.cpr.records;

import dk.magenta.datafordeler.core.database.Effect;
import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.data.CprData;

import java.util.regex.Pattern;

/**
 * Created by lars on 04-11-14.
 */
public abstract class CprDataRecord<V extends Effect, B extends CprData> extends CprRecord {

    public CprDataRecord(String line) throws ParseException {
        super(line);
    }

    public abstract ListHashMap<V, B> getDataEffects(String registrationFrom);

}
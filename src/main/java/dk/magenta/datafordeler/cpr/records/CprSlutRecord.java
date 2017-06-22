package dk.magenta.datafordeler.cpr.records;

import dk.magenta.datafordeler.core.util.DoubleHashMap;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;

import java.text.ParseException;

/**
 * Created by lars on 04-11-14.
 */
public class CprSlutRecord extends CprRecord {

    public String getRecordType() {
        return RECORDTYPE_SLUT;
    }

    public CprSlutRecord(String line) throws ParseException {
        super(line);
        this.put("taeller", substr(line, 4, 8));
    }

    @Override
    public DoubleHashMap<String, String, PersonBaseData> getDataEffects(String timestamp) {
        return null;
    }
}

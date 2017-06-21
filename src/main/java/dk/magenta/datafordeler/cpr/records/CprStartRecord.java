package dk.magenta.datafordeler.cpr.records;

import dk.magenta.datafordeler.core.util.DoubleHashMap;
import dk.magenta.datafordeler.cpr.data.person.PersonBaseData;

import java.text.ParseException;

/**
 * Created by lars on 04-11-14.
 */
public class CprStartRecord extends CprRecord {

    public String getRecordType() {
        return RECORDTYPE_START;
    }

    public CprStartRecord(String line) throws ParseException {
        super(line);
        this.put("opgaveNr", substr(line,4,6));
        this.put("prodDato", substr(line,10,8));
    }

    @Override
    public DoubleHashMap<String, String, PersonBaseData> getDataEffects(String timestamp) {
        return null;
    }

}

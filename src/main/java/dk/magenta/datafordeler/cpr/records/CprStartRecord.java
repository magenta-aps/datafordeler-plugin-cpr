package dk.magenta.datafordeler.cpr.records;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;

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
    public ListHashMap<PersonEffect, PersonBaseData> getDataEffects(String timestamp) {
        return null;
    }

}

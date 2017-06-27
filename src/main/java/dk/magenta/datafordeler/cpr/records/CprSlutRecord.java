package dk.magenta.datafordeler.cpr.records;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;

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
    public ListHashMap<PersonEffect, PersonBaseData> getDataEffects(String timestamp) {
        return null;
    }
}

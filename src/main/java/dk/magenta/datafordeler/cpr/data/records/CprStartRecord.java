package dk.magenta.datafordeler.cpr.data.records;

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

}

package dk.magenta.datafordeler.cpr.records.person;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.records.CprBitemporalRecord;
import dk.magenta.datafordeler.cpr.records.CprBitemporality;
import dk.magenta.datafordeler.cpr.records.person.data.PersonEventDataRecord;
import dk.magenta.datafordeler.cpr.records.person.data.PersonNumberDataRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Record for Person historic cpr number (type 065).
 */
public class PersonEventRecord extends HistoricPersonDataRecord {

    public PersonEventRecord(String line) throws ParseException {
        super(line);
        //this.obtain("pnr", 4, 10);
        this.obtain("timestamp", 14, 12);
        this.obtain("event", 26, 3);
        this.obtain("derived", 29, 2);
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_HISTORIC_CPRNUMBER;
    }


    @Override
    public List<CprBitemporalRecord> getBitemporalRecords() {

        ArrayList<CprBitemporalRecord> records = new ArrayList<>();

        records.add(new PersonEventDataRecord(
                this.getOffsetDateTime("timestamp")
        , this.getString("event", false)
        , this.getString("derived", false)));

        return records;
    }

}

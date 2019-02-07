package dk.magenta.datafordeler.cpr.records.road;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.records.CprBitemporality;
import dk.magenta.datafordeler.cpr.records.road.data.CprBitemporalRoadRecord;
import dk.magenta.datafordeler.cpr.records.road.data.RoadBitemporalRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Record for Road names (type 001).
 */
public class RoadRecord extends RoadDataRecord {

    private CprBitemporality roadTemporality;

    public RoadRecord(String line) throws ParseException {
        super(line);
        this.obtain("timestamp", 12, 12);
        this.obtain("tilkomkod", 24, 4);
        this.obtain("tilvejkod", 28, 4);
        this.obtain("frakomkod", 32, 4);
        this.obtain("fravejkod", 36, 4);
        this.obtain("haenstart", 40, 12);
        this.obtain("vejadrnvn", 52, 20);
        this.obtain("vejnvn", 72, 40);

        this.roadTemporality = new CprBitemporality(this.getOffsetDateTime("timestamp"), null, this.getOffsetDateTime("haenstart"), false, null, false);
    }


    @Override
    public String getRecordType() {
        return RECORDTYPE_ROAD;
    }

    @Override
    public List<CprBitemporalRoadRecord> getBitemporalRecords() {
        List<CprBitemporalRoadRecord> records = new ArrayList<>();
        records.add(new RoadBitemporalRecord(this.getOffsetDateTime("timestamp"), this.getInt("tilkomkod"), this.getInt("tilvejkod"),
                this.getInt("frakomkod"), this.getInt("fravejkod"), this.getOffsetDateTime("haenstart"), this.getString("vejadrnvn", true), this.getString("vejnvn", true)));
        return records;
    }

}

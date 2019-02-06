package dk.magenta.datafordeler.cpr.records.road;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.records.CprBitemporality;
import dk.magenta.datafordeler.cpr.records.road.data.CprBitemporalRoadRecord;
import dk.magenta.datafordeler.cpr.records.road.data.RoadCityBitemporalRecord;

import java.util.*;

/**
 * Record for Road city (type 003).
 */
public class RoadCityRecord extends RoadDataRecord {

    private CprBitemporality cityTemporality;

    public RoadCityRecord(String line) throws ParseException {
        super(line);
        this.obtain("husnrfra", 12, 4);
        this.obtain("husnrtil", 16, 4);
        this.obtain("ligeulige", 20, 1);
        this.obtain("timestamp", 21, 12);
        this.obtain("bynvn", 33, 34);

        this.cityTemporality = new CprBitemporality(this.getOffsetDateTime("timestamp"));
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_ROADCITY;
    }

    private boolean getEven(String key) {
        String value = this.get(key);
        return "L".equalsIgnoreCase(value);
    }


    @Override
    public List<CprBitemporalRoadRecord> getBitemporalRecords() {
        List<CprBitemporalRoadRecord> records = new ArrayList<>();

        records.add(new RoadCityBitemporalRecord(null,
                this.get("husnrtil"), this.get("husnrfra"), this.getEven("ligeulige"), this.get("bynvn")));

        return records;
    }
}

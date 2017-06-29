package dk.magenta.datafordeler.cpr.records.road;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.data.road.RoadEffect;
import dk.magenta.datafordeler.cpr.data.road.data.RoadBaseData;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by lars on 28-06-17.
 */
public class RoadMemoRecord extends RoadDataRecord {

    public RoadMemoRecord(String line) throws ParseException {
        super(line);
        this.obtain("timestamp", 54, 12);
        this.obtain("notatnr", 12, 2);
        this.obtain("notatlinie", 14, 40);
        this.obtain("haenstart", 66, 12);
    }



    @Override
    protected RoadBaseData createEmptyBaseData() {
        return new RoadBaseData();
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_ROADMEMO;
    }

    @Override
    public void getDataEffects(HashMap<RoadEffect, RoadBaseData> data, String registrationFrom) {
        RoadBaseData roadBaseData;
        if (registrationFrom.equals(this.get("timestamp"))) {
            roadBaseData = this.getBaseDataItem(data, this.getOffsetDateTime("haenstart"), false);
            roadBaseData.addMemo(
                    this.getInt("notatnr"),
                    this.get("notatlinie")
            );
        }
    }

    @Override
    public HashSet<String> getTimestamps() {
        HashSet<String> timestamps = super.getTimestamps();
        timestamps.add(this.get("timestamp"));
        return timestamps;
    }

}

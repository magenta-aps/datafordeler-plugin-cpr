package dk.magenta.datafordeler.cpr.records.road;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.data.road.RoadEffect;
import dk.magenta.datafordeler.cpr.data.road.data.RoadBaseData;
import dk.magenta.datafordeler.cpr.records.person.PersonDataRecord;

import java.util.HashSet;

/**
 * Created by lars on 28-06-17.
 */
public class RoadRecord extends RoadDataRecord<RoadBaseData> {

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
    }



    @Override
    protected RoadBaseData createEmptyBaseData() {
        return new RoadBaseData();
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_ROAD;
    }

    @Override
    public ListHashMap<RoadEffect, RoadBaseData> getDataEffects(String registrationFrom) {
        ListHashMap<RoadEffect, RoadBaseData> data = new ListHashMap<>();
        RoadBaseData roadBaseData;
        if (registrationFrom.equals(this.get("timestamp"))) {
            roadBaseData = this.getBaseDataItem(data, this.getOffsetDateTime("haenstart"), false);
            roadBaseData.setCore(
                    this.getInt("tilkomkod"),
                    this.getInt("tilvejkod"),
                    this.getInt("frakomkod"),
                    this.getInt("fravejkod"),
                    this.get("vejadrnvn"),
                    this.get("vejnvn")
            );
        }
        return data;
    }

    @Override
    public HashSet<String> getTimestamps() {
        HashSet<String> timestamps = super.getTimestamps();
        timestamps.add(this.get("timestamp"));
        return timestamps;
    }

}

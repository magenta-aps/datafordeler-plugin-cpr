package dk.magenta.datafordeler.cpr.records;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.data.CprData;
import dk.magenta.datafordeler.cpr.data.CprEffect;
import dk.magenta.datafordeler.cpr.records.road.data.CprBitemporalRoadRecord;

import java.util.List;

public abstract class CprGeoRecord<V extends CprEffect, D extends CprData<V, D>> extends CprDataRecord<V, D> {

    public CprGeoRecord(String line) throws ParseException {
        super(line);
    }

    @Override
    public String getRecordType() {
        return null;
    }

    //TODO: RECONSIDERE THE REFACTORING OF THIS CLASS
    public abstract List<CprBitemporalRoadRecord> getBitemporalRecords();
}

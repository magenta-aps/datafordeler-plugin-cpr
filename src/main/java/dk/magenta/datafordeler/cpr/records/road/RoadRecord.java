package dk.magenta.datafordeler.cpr.records.road;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.records.person.PersonDataRecord;

/**
 * Created by lars on 28-06-17.
 */
public class RoadRecord extends RoadDataRecord<PersonBaseData> {

    public RoadRecord(String line) throws ParseException {
        super(line);
        this.obtain("TILKOMKOD", 12, 4);
        this.obtain("TILVEJKOD", 12, 4);
    }

    @Override
    protected PersonBaseData createEmptyBaseData() {
        return null;
    }

    @Override
    public String getRecordType() {
        return null;
    }

    @Override
    public ListHashMap<PersonEffect, PersonBaseData> getDataEffects(String registrationFrom) {
        return null;
    }
}

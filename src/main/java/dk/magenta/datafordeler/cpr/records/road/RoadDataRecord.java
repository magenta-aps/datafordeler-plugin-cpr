package dk.magenta.datafordeler.cpr.records.road;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.road.RoadEffect;
import dk.magenta.datafordeler.cpr.data.road.data.RoadBaseData;
import dk.magenta.datafordeler.cpr.records.CprDataRecord;

import java.time.OffsetDateTime;
import java.util.HashSet;

/**
 * Created by lars on 22-06-17.
 */
public abstract class RoadDataRecord extends CprDataRecord<RoadEffect, RoadBaseData> {

    public static final String RECORDTYPE_ROAD = "001";
    public static final String RECORDTYPE_ROADMEMO = "005";
    // TODO: Add one for each data type

    public RoadDataRecord(String line) throws ParseException {
        super(line);
        this.obtain("komkod", 4, 4);
        this.obtain("vejkod", 8, 4);
    }

    protected int getTimestampStart() {
        return 21;
    }

    public int getMunicipalityCode() {
        return this.getInt("komkod");
    }

    public int getRoadCode() {
        return this.getInt("vejkod");
    }

    public HashSet<String> getTimestamps() {
        return new HashSet<>();
    }

    protected RoadBaseData createEmptyBaseData() {
        return new RoadBaseData();
    };

    @Override
    protected RoadEffect createEffect(OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        RoadEffect effect = new RoadEffect(null, effectFrom, effectTo);
        effect.setUncertainFrom(effectFromUncertain);
        effect.setUncertainTo(effectToUncertain);
        return effect;
    }

}

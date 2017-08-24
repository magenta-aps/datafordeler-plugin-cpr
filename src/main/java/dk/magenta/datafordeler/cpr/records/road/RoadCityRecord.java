package dk.magenta.datafordeler.cpr.records.road;

import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.data.road.RoadEffect;
import dk.magenta.datafordeler.cpr.data.road.data.RoadBaseData;
import dk.magenta.datafordeler.cpr.records.Bitemporality;
import org.hibernate.Session;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lars on 29-06-17.
 */
public class RoadCityRecord extends RoadDataRecord {

    private Bitemporality cityTemporality;

    public RoadCityRecord(String line) throws ParseException {
        super(line);
        this.obtain("husnrfra", 12, 4);
        this.obtain("husnrtil", 16, 4);
        this.obtain("ligeulige", 20, 1);
        this.obtain("timestamp", 21, 12);
        this.obtain("bynvn", 33, 34);

        this.cityTemporality = new Bitemporality(this.getOffsetDateTime("timestamp"));
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_ROADCITY;
    }

    @Override
    public void populateBaseData(RoadBaseData data, RoadEffect effect, OffsetDateTime registrationTime, QueryManager queryManager, Session session) {
        if (this.cityTemporality.matches(registrationTime, effect)) {
            data.addCity(
                    this.get("husnrfra"),
                    this.get("husnrtil"),
                    this.getEven("ligeulige"),
                    this.get("bynvn")
            );
        }
    }

    @Override
    public HashSet<OffsetDateTime> getRegistrationTimestamps() {
        HashSet<OffsetDateTime> timestamps = super.getRegistrationTimestamps();
        timestamps.add(this.cityTemporality.registrationFrom);
        return timestamps;
    }

    @Override
    public List<Bitemporality> getBitemporality() {
        return Collections.singletonList(this.cityTemporality);
    }

    private boolean getEven(String key) {
        String value = this.get(key);
        return "L".equalsIgnoreCase(value);
    }

    @Override
    public Set<RoadEffect> getEffects() {
        HashSet<RoadEffect> effects = new HashSet<>();
        effects.add(new RoadEffect(null, null, false, null, false));
        return effects;
    }
}

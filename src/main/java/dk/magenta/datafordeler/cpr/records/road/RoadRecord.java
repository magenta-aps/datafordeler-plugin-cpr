package dk.magenta.datafordeler.cpr.records.road;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.io.ImportMetadata;
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
 * Record for Road names (type 001).
 */
public class RoadRecord extends RoadDataRecord {

    private Bitemporality roadTemporality;

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

        this.roadTemporality = new Bitemporality(this.getOffsetDateTime("timestamp"), null, this.getOffsetDateTime("haenstart"), false, null, false);
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
    public boolean populateBaseData(RoadBaseData data, RoadEffect effect, OffsetDateTime registrationTime, Session session, ImportMetadata importMetadata) {
        if (registrationTime.equals(this.getOffsetDateTime("timestamp"))) {
            data.setCore(
                    this.getInt("tilkomkod"),
                    this.getInt("tilvejkod"),
                    this.getInt("frakomkod"),
                    this.getInt("fravejkod"),
                    this.get("vejadrnvn"),
                    this.get("vejnvn")
            );
            return true;
        }
        return false;
    }

    @Override
    public HashSet<OffsetDateTime> getRegistrationTimestamps() {
        HashSet<OffsetDateTime> timestamps = super.getRegistrationTimestamps();
        timestamps.add(this.roadTemporality.registrationFrom);
        return timestamps;
    }

    @Override
    public List<Bitemporality> getBitemporality() {
        return Collections.singletonList(this.roadTemporality);
    }

    @Override
    public Set<RoadEffect> getEffects() {
        HashSet<RoadEffect> effects = new HashSet<>();
        effects.add(new RoadEffect(null, this.getOffsetDateTime("haenstart"), false, null, false));
        return effects;
    }

}

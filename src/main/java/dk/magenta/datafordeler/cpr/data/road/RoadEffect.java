package dk.magenta.datafordeler.cpr.data.road;

import dk.magenta.datafordeler.core.database.Effect;
import dk.magenta.datafordeler.cpr.data.road.data.RoadBaseData;

import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;

/**
 * Created by lars on 16-05-17.
 */
@javax.persistence.Entity
@Table(name="cpr_road_effect")
public class RoadEffect extends Effect<RoadRegistration, RoadEffect, RoadBaseData> {

    private boolean uncertainFrom;

    private boolean uncertainTo;

    public RoadEffect() {
    }

    public RoadEffect(RoadRegistration registration, OffsetDateTime effectFrom, OffsetDateTime effectTo) {
        super(registration, effectFrom, effectTo);
    }

    public RoadEffect(RoadRegistration registration, TemporalAccessor effectFrom, TemporalAccessor effectTo) {
        super(registration, effectFrom, effectTo);
    }

    public RoadEffect(RoadRegistration registration, String effectFrom, String effectTo) {
        super(registration, effectFrom, effectTo);
    }

    public boolean isUncertainFrom() {
        return this.uncertainFrom;
    }

    public void setUncertainFrom(boolean uncertainFrom) {
        this.uncertainFrom = uncertainFrom;
    }

    public boolean isUncertainTo() {
        return this.uncertainTo;
    }

    public void setUncertainTo(boolean uncertainTo) {
        this.uncertainTo = uncertainTo;
    }

    public void setRegistration(RoadRegistration registration) {
        super.setRegistration(registration);
    }
}

package dk.magenta.datafordeler.cpr.data.road;

import dk.magenta.datafordeler.cpr.data.CprEffect;
import dk.magenta.datafordeler.cpr.data.road.data.RoadBaseData;

import javax.persistence.Index;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;

/**
 * Created by lars on 16-05-17.
 */
@javax.persistence.Entity
@Table(name="cpr_road_effect", indexes = {
        @Index(name = "cpr_road_effect_registration", columnList = "registration_id"),
        @Index(name = "cpr_road_effect_from", columnList = "effectFrom"),
        @Index(name = "cpr_road_effect_to", columnList = "effectTo")
})
public class RoadEffect extends CprEffect<RoadRegistration, RoadEffect, RoadBaseData> {

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

    public RoadEffect(RoadRegistration registration, OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        this(registration, effectFrom, effectTo);
        this.setEffectFromUncertain(effectFromUncertain);
        this.setEffectToUncertain(effectToUncertain);
    }

}

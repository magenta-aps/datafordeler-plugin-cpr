package dk.magenta.datafordeler.cpr.data.road;

import dk.magenta.datafordeler.cpr.data.CprRegistration;

import javax.persistence.Index;
import javax.persistence.Table;
import java.time.OffsetDateTime;

/**
 * Created by lars on 16-05-17.
 */
@javax.persistence.Entity
@Table(name="cpr_road_registration", indexes = {
        @Index(name = "cpr_road_entity", columnList = "entity_id"),
        @Index(name = "cpr_road_registration_from", columnList = "registrationFrom"),
        @Index(name = "cpr_road_registration_to", columnList = "registrationTo")
})
public class RoadRegistration extends CprRegistration<RoadEntity, RoadRegistration, RoadEffect> {

    @Override
    protected RoadEffect createEmptyEffect(OffsetDateTime effectFrom, OffsetDateTime effectTo) {
        return new RoadEffect(this, effectFrom, effectTo);
    }

}

package dk.magenta.datafordeler.cpr.data.road;

import dk.magenta.datafordeler.core.database.Registration;

import javax.persistence.Table;

/**
 * Created by lars on 16-05-17.
 */
@javax.persistence.Entity
@Table(name="cpr_road_registration")
public class RoadRegistration extends Registration<RoadEntity, RoadRegistration, RoadEffect> {

}

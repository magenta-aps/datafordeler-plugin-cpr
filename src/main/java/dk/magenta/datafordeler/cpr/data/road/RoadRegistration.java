package dk.magenta.datafordeler.cpr.data.road;

import dk.magenta.datafordeler.core.database.Registration;

import javax.persistence.Table;
import java.time.OffsetDateTime;

/**
 * Created by lars on 16-05-17.
 */
@javax.persistence.Entity
@Table(name="cpr_road_registration")
public class RoadRegistration extends Registration<RoadEntity, RoadRegistration, RoadEffect> {

    public RoadEffect getEffect(OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        for (RoadEffect effect : this.virkninger) {
            if (
                    (effect.getVirkningFra() == null ? effectFrom == null : effect.getVirkningFra().equals(effectFrom)) &&
                            (effect.getVirkningTil() == null ? effectTo == null : effect.getVirkningTil().equals(effectTo)) &&
                            (effect.isVirkningFraUsikkerhedsmarkering() == effectFromUncertain) &&
                            (effect.isVirkningTilUsikkerhedsmarkering() == effectToUncertain)
                    ) {
                return effect;
            }
        }
        return null;
    }
}

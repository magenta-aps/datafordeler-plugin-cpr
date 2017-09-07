package dk.magenta.datafordeler.cpr.data.residence;

import dk.magenta.datafordeler.cpr.data.CprRegistration;

import javax.persistence.Table;
import java.time.OffsetDateTime;

/**
 * Created by lars on 16-05-17.
 */
@javax.persistence.Entity
@Table(name="cpr_residence_registration")
public class ResidenceRegistration extends CprRegistration<ResidenceEntity, ResidenceRegistration, ResidenceEffect> {

    @Override
    protected ResidenceEffect createEmptyEffect(OffsetDateTime effectFrom, OffsetDateTime effectTo) {
        return new ResidenceEffect(this, effectFrom, effectTo);
    }

    public ResidenceEffect getEffect(OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        for (ResidenceEffect effect : this.effects) {
            if (
                    (effect.getEffectFrom() == null ? effectFrom == null : effect.getEffectFrom().equals(effectFrom)) &&
                    (effect.getEffectTo() == null ? effectTo == null : effect.getEffectTo().equals(effectTo)) &&
                    (effect.getEffectFromUncertain() == effectFromUncertain) &&
                    (effect.getEffectToUncertain() == effectToUncertain)
            ) {
                return effect;
            }
        }
        return null;
    }

}

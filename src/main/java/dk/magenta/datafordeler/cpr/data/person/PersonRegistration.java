package dk.magenta.datafordeler.cpr.data.person;

import dk.magenta.datafordeler.cpr.data.CprRegistration;

import javax.persistence.Table;
import java.time.OffsetDateTime;

/**
 * Created by lars on 16-05-17.
 */
@javax.persistence.Entity
@Table(name="cpr_person_registration")
public class PersonRegistration extends CprRegistration<PersonEntity, PersonRegistration, PersonEffect> {

    @Override
    protected PersonEffect createEmptyEffect(OffsetDateTime effectFrom, OffsetDateTime effectTo) {
        return new PersonEffect(this, effectFrom, effectTo);
    }

    public PersonEffect getEffect(OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        for (PersonEffect effect : this.effects) {
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

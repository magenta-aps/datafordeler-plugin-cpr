package dk.magenta.datafordeler.cpr.data.person;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.core.database.Registration;

import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lars on 16-05-17.
 */
@javax.persistence.Entity
@Table(name="cpr_person_registration")
public class PersonRegistration extends Registration<PersonEntity, PersonRegistration, PersonEffect> {

    public PersonEffect getEffect(OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        for (PersonEffect effect : this.effects) {
            if (
                    (effect.getEffectFrom() == null ? effectFrom == null : effect.getEffectFrom().equals(effectFrom)) &&
                    (effect.getEffectTo() == null ? effectTo == null : effect.getEffectTo().equals(effectTo)) &&
                    (effect.isUncertainFrom() == effectFromUncertain) &&
                    (effect.isUncertainTo() == effectToUncertain)
            ) {
                return effect;
            }
        }
        return null;
    }

    @Override
    protected PersonEffect createEmptyEffect(OffsetDateTime effectFrom, OffsetDateTime effectTo) {
        return new PersonEffect(this, effectFrom, effectTo);
    }
}

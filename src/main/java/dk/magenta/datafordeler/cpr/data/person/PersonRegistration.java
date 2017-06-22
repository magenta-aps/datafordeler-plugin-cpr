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

    /*@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public List<PersonEffect> getEffects() {
        ArrayList<PersonEffect> uniqueEffects = new ArrayList<>();
        for (PersonEffect effect : this.effects) {
            OffsetDateTime effectFrom = effect.getEffectFrom();
        }
        return uniqueEffects;
    }*/
}

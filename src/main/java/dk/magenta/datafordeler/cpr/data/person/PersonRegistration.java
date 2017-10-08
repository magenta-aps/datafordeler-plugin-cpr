package dk.magenta.datafordeler.cpr.data.person;

import dk.magenta.datafordeler.cpr.data.CprRegistration;

import javax.persistence.Index;
import javax.persistence.Table;
import java.time.OffsetDateTime;

/**
 * Created by lars on 16-05-17.
 */
@javax.persistence.Entity
@Table(name="cpr_person_registration", indexes = {
        @Index(name = "entity", columnList = "entity_id")
})
public class PersonRegistration extends CprRegistration<PersonEntity, PersonRegistration, PersonEffect> {

    @Override
    protected PersonEffect createEmptyEffect(OffsetDateTime effectFrom, OffsetDateTime effectTo) {
        return new PersonEffect(this, effectFrom, effectTo);
    }

}

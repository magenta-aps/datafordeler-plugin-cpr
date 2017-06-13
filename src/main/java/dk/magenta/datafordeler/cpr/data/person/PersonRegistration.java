package dk.magenta.datafordeler.cpr.data.person;

import dk.magenta.datafordeler.core.database.Registration;

import javax.persistence.Table;

/**
 * Created by lars on 16-05-17.
 */
@javax.persistence.Entity
@Table(name="cpr_person_registration")
public class PersonRegistration extends Registration<PersonEntity, PersonRegistration, PersonEffect> {
}

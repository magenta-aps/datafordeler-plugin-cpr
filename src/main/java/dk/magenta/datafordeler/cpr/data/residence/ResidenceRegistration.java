package dk.magenta.datafordeler.cpr.data.residence;

import dk.magenta.datafordeler.cpr.data.CprRegistration;

import javax.persistence.Index;
import javax.persistence.Table;
import java.time.OffsetDateTime;

/**
 * Created by lars on 16-05-17.
 */
@javax.persistence.Entity
@Table(name="cpr_residence_registration", indexes = {
        @Index(name = "entity", columnList = "entity_id")
})
public class ResidenceRegistration extends CprRegistration<ResidenceEntity, ResidenceRegistration, ResidenceEffect> {

    @Override
    protected ResidenceEffect createEmptyEffect(OffsetDateTime effectFrom, OffsetDateTime effectTo) {
        return new ResidenceEffect(this, effectFrom, effectTo);
    }

}

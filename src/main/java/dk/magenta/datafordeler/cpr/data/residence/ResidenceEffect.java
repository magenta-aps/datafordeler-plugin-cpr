package dk.magenta.datafordeler.cpr.data.residence;

import dk.magenta.datafordeler.cpr.data.CprEffect;
import dk.magenta.datafordeler.cpr.data.residence.data.ResidenceBaseData;

import javax.persistence.Index;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;

/**
 * Created by lars on 16-05-17.
 */
@javax.persistence.Entity
@Table(name="cpr_residence_effect", indexes = {
        @Index(name = "cpr_residence_effect_registration", columnList = "registration_id"),
        @Index(name = "cpr_residence_effect_from", columnList = "effectFrom"),
        @Index(name = "cpr_residence_effect_to", columnList = "effectTo")
})
public class ResidenceEffect extends CprEffect<ResidenceRegistration, ResidenceEffect, ResidenceBaseData> {

    public ResidenceEffect() {
    }

    public ResidenceEffect(ResidenceRegistration registration, OffsetDateTime effectFrom, OffsetDateTime effectTo) {
        super(registration, effectFrom, effectTo);
    }

    public ResidenceEffect(ResidenceRegistration registration, TemporalAccessor effectFrom, TemporalAccessor effectTo) {
        super(registration, effectFrom, effectTo);
    }

    public ResidenceEffect(ResidenceRegistration registration, String effectFrom, String effectTo) {
        super(registration, effectFrom, effectTo);
    }


    public ResidenceEffect(ResidenceRegistration registration, OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        this(registration, effectFrom, effectTo);
        this.setEffectFromUncertain(effectFromUncertain);
        this.setEffectToUncertain(effectToUncertain);
    }


}

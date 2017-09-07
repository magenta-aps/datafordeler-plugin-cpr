package dk.magenta.datafordeler.cpr.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.core.database.Effect;
import dk.magenta.datafordeler.core.database.Registration;
import dk.magenta.datafordeler.cpr.records.Bitemporality;

import javax.xml.bind.annotation.XmlElement;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;

/**
 * Created by lars on 29-06-17.
 */
public abstract class CprEffect<R extends Registration, V extends CprEffect, D extends CprData> extends Effect<R, V, D> {

    private boolean effectFromUncertain;

    private boolean effectToUncertain;

    public CprEffect() {
    }

    public CprEffect(R registration, OffsetDateTime effectFrom, OffsetDateTime effectTo) {
        super(registration, effectFrom, effectTo);
    }

    public CprEffect(R registration, TemporalAccessor effectFrom, TemporalAccessor effectTo) {
        super(registration, effectFrom, effectTo);
    }

    public CprEffect(R registration, String effectFrom, String effectTo) {
        super(registration, effectFrom, effectTo);
    }

    @JsonProperty("virkningFraUsikkerhedsmarkering")
    @XmlElement(name = "virkningFraUsikkerhedsmarkering")
    public boolean getEffectFromUncertain() {
        return this.effectFromUncertain;
    }

    public void setEffectFromUncertain(boolean effectFromUncertain) {
        this.effectFromUncertain = effectFromUncertain;
    }

    @JsonProperty("virkningTilUsikkerhedsmarkering")
    @XmlElement(name = "virkningTilUsikkerhedsmarkering")
    public boolean getEffectToUncertain() {
        return this.effectToUncertain;
    }

    public void setEffectToUncertain(boolean effectToUncertain) {
        this.effectToUncertain = effectToUncertain;
    }

    public void setRegistration(R registrering) {
        super.setRegistration(registrering);
    }
    public boolean compareRange(V other) {
        return (other != null && this.compareRange(other.getEffectFrom(), other.getEffectFromUncertain(), other.getEffectTo(), other.getEffectToUncertain()));
    }

    public boolean compareRange(OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        return (
                (this.getEffectFrom() != null ? this.getEffectFrom().equals(effectFrom) : effectFrom == null) &&
                        (this.getEffectTo() != null ? this.getEffectTo().equals(effectTo) : effectTo == null) &&
                        (this.getEffectFromUncertain() == effectFromUncertain) &&
                        (this.getEffectToUncertain() == effectToUncertain)
        );
    }

    public boolean compareRange(Bitemporality bitemporality) {
        return this.compareRange(bitemporality.effectFrom, bitemporality.effectFromUncertain, bitemporality.effectTo, bitemporality.effectToUncertain);
    }

    public V createClone() {
        V effect = super.createClone();
        effect.setEffectFromUncertain(this.effectFromUncertain);
        effect.setEffectToUncertain(this.effectToUncertain);
        return effect;
    }
}

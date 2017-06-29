package dk.magenta.datafordeler.cpr.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.core.database.Effect;
import dk.magenta.datafordeler.core.database.Registration;

import javax.xml.bind.annotation.XmlElement;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;

/**
 * Created by lars on 29-06-17.
 */
public abstract class CprEffect<R extends Registration, V extends CprEffect, B extends CprData> extends Effect<R, V, B> {

    @JsonProperty(value = "fraUsikker")
    @XmlElement(name = "fraUsikker")
    private boolean uncertainFrom;

    @JsonProperty(value = "tilUsikker")
    @XmlElement(name = "tilUsikker")
    private boolean uncertainTo;

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

    public boolean isUncertainFrom() {
        return this.uncertainFrom;
    }

    public void setUncertainFrom(boolean uncertainFrom) {
        this.uncertainFrom = uncertainFrom;
    }

    public boolean isUncertainTo() {
        return this.uncertainTo;
    }

    public void setUncertainTo(boolean uncertainTo) {
        this.uncertainTo = uncertainTo;
    }

    public void setRegistration(R registration) {
        super.setRegistration(registration);
    }
    public boolean compareRange(V other) {
        return (other != null && this.compareRange(other.getEffectFrom(), other.isUncertainFrom(), other.getEffectTo(), other.isUncertainTo()));
    }

    public boolean compareRange(OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        return (
                (this.getEffectFrom() != null ? this.getEffectFrom().equals(effectFrom) : effectFrom == null) &&
                        (this.getEffectTo() != null ? this.getEffectTo().equals(effectTo) : effectTo == null) &&
                        (this.isUncertainFrom() == effectFromUncertain) &&
                        (this.isUncertainTo() == effectToUncertain)
        );
    }
}

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

    @JsonProperty(value = "virkningFraUsikkerhedsmarkering")
    @XmlElement(name = "virkningFraUsikkerhedsmarkering")
    private boolean virkningFraUsikkerhedsmarkering;

    @JsonProperty(value = "virkningTilUsikkerhedsmarkering")
    @XmlElement(name = "virkningTilUsikkerhedsmarkering")
    private boolean virkningTilUsikkerhedsmarkering;

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

    public boolean isVirkningFraUsikkerhedsmarkering() {
        return this.virkningFraUsikkerhedsmarkering;
    }

    public void setVirkningFraUsikkerhedsmarkering(boolean virkningFraUsikkerhedsmarkering) {
        this.virkningFraUsikkerhedsmarkering = virkningFraUsikkerhedsmarkering;
    }

    public boolean isVirkningTilUsikkerhedsmarkering() {
        return this.virkningTilUsikkerhedsmarkering;
    }

    public void setVirkningTilUsikkerhedsmarkering(boolean virkningTilUsikkerhedsmarkering) {
        this.virkningTilUsikkerhedsmarkering = virkningTilUsikkerhedsmarkering;
    }

    public void setRegistrering(R registrering) {
        super.setRegistrering(registrering);
    }
    public boolean compareRange(V other) {
        return (other != null && this.compareRange(other.getVirkningFra(), other.isVirkningFraUsikkerhedsmarkering(), other.getVirkningTil(), other.isVirkningTilUsikkerhedsmarkering()));
    }

    public boolean compareRange(OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        return (
                (this.getVirkningFra() != null ? this.getVirkningFra().equals(effectFrom) : effectFrom == null) &&
                        (this.getVirkningTil() != null ? this.getVirkningTil().equals(effectTo) : effectTo == null) &&
                        (this.isVirkningFraUsikkerhedsmarkering() == effectFromUncertain) &&
                        (this.isVirkningTilUsikkerhedsmarkering() == effectToUncertain)
        );
    }
}

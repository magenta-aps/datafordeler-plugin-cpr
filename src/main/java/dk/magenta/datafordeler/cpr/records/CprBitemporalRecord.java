package dk.magenta.datafordeler.cpr.records;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlElement;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;

@MappedSuperclass
public class CprBitemporalRecord extends CprMonotemporalRecord implements Comparable<CprBitemporalRecord> {

    //public static final String FILTER_LAST_UPDATED = "(" + CprBitemporalRecord.DB_FIELD_LAST_UPDATED + " < :" + Registration.FILTERPARAM_REGISTRATION_TO + ")";
    //public static final String FILTER_EFFECT_FROM = "(" + CvrRecordPeriod.DB_FIELD_VALID_TO + " >= :" + Effect.FILTERPARAM_EFFECT_FROM + " OR " + CvrRecordPeriod.DB_FIELD_VALID_TO + " is null)";
    //public static final String FILTER_EFFECT_TO = "(" + CvrRecordPeriod.DB_FIELD_VALID_FROM + " < :" + Effect.FILTERPARAM_EFFECT_TO + " OR " + CvrRecordPeriod.DB_FIELD_VALID_FROM + " is null)";

    public static final String DB_FIELD_EFFECT_FROM = "effectFrom";
    public static final String IO_FIELD_EFFECT_FROM = "virkningFra";
    @Column(name = DB_FIELD_EFFECT_FROM)
    @JsonProperty(value = IO_FIELD_EFFECT_FROM)
    @XmlElement(name = IO_FIELD_EFFECT_FROM)
    private OffsetDateTime effectFrom;

    public OffsetDateTime getEffectFrom() {
        return this.effectFrom;
    }

    public void setEffectFrom(OffsetDateTime effectFrom) {
        this.effectFrom = effectFrom;
    }

    public static final String DB_FIELD_EFFECT_FROM_UNCERTAIN = "effectFromUncertain";
    public static final String IO_FIELD_EFFECT_FROM_UNCERTAIN = "virkningFraUsikker";
    @Column(name = DB_FIELD_EFFECT_FROM_UNCERTAIN)
    @JsonProperty(value = IO_FIELD_EFFECT_FROM_UNCERTAIN)
    @XmlElement(name = IO_FIELD_EFFECT_FROM_UNCERTAIN)
    private boolean effectFromUncertain;

    public boolean isEffectFromUncertain() {
        return this.effectFromUncertain;
    }

    public void setEffectFromUncertain(boolean effectFromUncertain) {
        this.effectFromUncertain = effectFromUncertain;
    }

    public static final String DB_FIELD_EFFECT_TO = "effectTo";
    public static final String IO_FIELD_EFFECT_TO = "virkningTil";
    @Column(name = DB_FIELD_EFFECT_TO)
    @JsonProperty(value = IO_FIELD_EFFECT_TO)
    @XmlElement(name = IO_FIELD_EFFECT_TO)
    private OffsetDateTime effectTo;

    public OffsetDateTime getEffectTo() {
        return this.effectTo;
    }

    public void setEffectTo(OffsetDateTime effectTo) {
        this.effectTo = effectTo;
    }

    public static final String DB_FIELD_EFFECT_TO_UNCERTAIN = "effectToUncertain";
    public static final String IO_FIELD_EFFECT_TO_UNCERTAIN = "virkningTilUsikker";
    @Column(name = DB_FIELD_EFFECT_TO_UNCERTAIN)
    @JsonProperty(value = IO_FIELD_EFFECT_TO_UNCERTAIN)
    @XmlElement(name = IO_FIELD_EFFECT_TO_UNCERTAIN)
    private boolean effectToUncertain;

    public boolean isEffectToUncertain() {
        return this.effectToUncertain;
    }

    public void setEffectToUncertain(boolean effectToUncertain) {
        this.effectToUncertain = effectToUncertain;
    }


    public CprBitemporalRecord setBitemporality(OffsetDateTime registrationFrom, OffsetDateTime registrationTo, OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        super.setBitemporality(registrationFrom, registrationTo);
        this.effectFrom = effectFrom;
        this.effectFromUncertain = effectFromUncertain;
        this.effectTo = effectTo;
        this.effectToUncertain = effectToUncertain;
        return this;
    }

    public CprBitemporalRecord setBitemporality(OffsetDateTime registrationFrom, OffsetDateTime registrationTo, TemporalAccessor effectFrom, boolean effectFromUncertain, TemporalAccessor effectTo, boolean effectToUncertain) {
        return this.setBitemporality(
                registrationFrom,
                registrationTo,
                Bitemporality.convertTime(effectFrom),
                effectFromUncertain,
                Bitemporality.convertTime(effectTo),
                effectToUncertain
        );
    }

    public CprBitemporalRecord setBitemporality(Bitemporality bitemporality) {
        return this.setBitemporality(
                bitemporality.registrationFrom,
                bitemporality.registrationTo,
                bitemporality.effectFrom,
                bitemporality.effectFromUncertain,
                bitemporality.effectTo,
                bitemporality.effectToUncertain
        );
    }

    public CprBitemporalRecord setAuthority(int authority) {
        super.setAuthority(authority);
        return this;
    }

    public CprBitemporalRecord setDafoUpdated(OffsetDateTime updateTime) {
        super.setDafoUpdated(updateTime);
        return this;
    }

    /**
     * For sorting purposes; we implement the Comparable interface, so we should
     * provide a comparison method. Here, we sort CvrRecord objects by registrationFrom, with nulls first
     */
    @Override
    public int compareTo(CprBitemporalRecord o) {
        return super.compareTo(o);
    }

    @JsonIgnore
    public Bitemporality getBitemporality() {
        return new Bitemporality(this.getRegistrationFrom(), this.getRegistrationTo(), this.effectFrom, this.effectFromUncertain, this.effectTo, this.effectToUncertain);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CprBitemporalRecord that = (CprBitemporalRecord) o;
        return effectFromUncertain == that.effectFromUncertain &&
                effectToUncertain == that.effectToUncertain &&
                Objects.equals(effectFrom, that.effectFrom) &&
                Objects.equals(effectTo, that.effectTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), effectFrom, effectFromUncertain, effectTo, effectToUncertain);
    }

    protected static void copy(CprBitemporalRecord from, CprBitemporalRecord to) {
        CprMonotemporalRecord.copy(from, to);
        to.effectFrom = from.effectFrom;
        to.effectFromUncertain = from.effectFromUncertain;
        to.effectTo = from.effectTo;
        to.effectToUncertain = from.effectToUncertain;
    }
}

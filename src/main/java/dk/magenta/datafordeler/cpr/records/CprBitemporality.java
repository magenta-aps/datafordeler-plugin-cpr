package dk.magenta.datafordeler.cpr.records;

import dk.magenta.datafordeler.core.util.Bitemporality;
import dk.magenta.datafordeler.cpr.data.CprEffect;

import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;

public class CprBitemporality extends Bitemporality {
    public boolean effectFromUncertain;
    public boolean effectToUncertain;

    public CprBitemporality(OffsetDateTime registrationFrom, OffsetDateTime registrationTo, OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        super(registrationFrom, registrationTo, effectFrom, effectTo);
        this.effectFromUncertain = effectFromUncertain;
        this.effectToUncertain = effectToUncertain;
    }

    public CprBitemporality(OffsetDateTime registrationFrom) {
        this(registrationFrom, null, null, false, null, false);
    }

    public CprBitemporality(OffsetDateTime registrationFrom, OffsetDateTime registrationTo) {
        this(registrationFrom, registrationTo, null, false, null, false);
    }

    public CprBitemporality(OffsetDateTime registrationFrom, OffsetDateTime registrationTo, OffsetDateTime effectFrom, OffsetDateTime effectTo) {
        this(registrationFrom, registrationTo, effectFrom, false, effectTo, false);
    }

    public CprBitemporality(OffsetDateTime registrationFrom, OffsetDateTime registrationTo, TemporalAccessor effectFrom, TemporalAccessor effectTo) {
        this(registrationFrom, registrationTo, convertTime(effectFrom), false, convertTime(effectTo), false);
    }

    public CprBitemporality(OffsetDateTime registrationFrom, OffsetDateTime registrationTo, TemporalAccessor effectFrom, boolean effectFromUncertain, TemporalAccessor effectTo, boolean effectToUncertain) {
        this(registrationFrom, registrationTo, convertTime(effectFrom), effectFromUncertain, convertTime(effectTo), effectToUncertain);
    }

    public CprBitemporality withEffect(CprEffect effect) {
        return new CprBitemporality(this.registrationFrom, this.registrationTo, effect.getEffectFrom(), effect.getEffectFromUncertain(), effect.getEffectTo(), effect.getEffectToUncertain());
    }

    public boolean equals(Object o, char compare) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CprBitemporality that = (CprBitemporality) o;

        if (((compare & COMPARE_EFFECT_FROM) != 0) && (effectFromUncertain != that.effectFromUncertain)) return false;
        if (((compare & COMPARE_EFFECT_TO) != 0) && (effectToUncertain != that.effectToUncertain)) return false;
        return super.equals(o, compare);
    }

    @Override
    public int hashCode() {
        int result = registrationFrom != null ? registrationFrom.hashCode() : 0;
        result = 31 * result + (effectFrom != null ? effectFrom.hashCode() : 0);
        result = 31 * result + (effectFromUncertain ? 1 : 0);
        result = 31 * result + (effectTo != null ? effectTo.hashCode() : 0);
        result = 31 * result + (effectToUncertain ? 1 : 0);
        return result;
    }

}

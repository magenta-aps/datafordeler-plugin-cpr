package dk.magenta.datafordeler.cpr.records;

import dk.magenta.datafordeler.core.util.Equality;
import dk.magenta.datafordeler.cpr.data.CprEffect;

import java.time.OffsetDateTime;
import java.util.Objects;

public class Bitemporality implements Comparable<Bitemporality> {
    public OffsetDateTime registrationFrom;
    public OffsetDateTime registrationTo;
    public OffsetDateTime effectFrom;
    public boolean effectFromUncertain;
    public OffsetDateTime effectTo;
    public boolean effectToUncertain;

    public Bitemporality(OffsetDateTime registrationFrom, OffsetDateTime registrationTo, OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        this.registrationFrom = registrationFrom;
        this.registrationTo = registrationTo;
        this.effectFrom = effectFrom;
        this.effectFromUncertain = effectFromUncertain;
        this.effectTo = effectTo;
        this.effectToUncertain = effectToUncertain;
    }

    public Bitemporality(OffsetDateTime registrationFrom) {
        this(registrationFrom, null, null, false, null, false);
    }

    public Bitemporality(OffsetDateTime registrationFrom, OffsetDateTime registrationTo) {
        this(registrationFrom, registrationTo, null, false, null, false);
    }

    public Bitemporality(OffsetDateTime registrationFrom, OffsetDateTime registrationTo, OffsetDateTime effectFrom, OffsetDateTime effectTo) {
        this(registrationFrom, registrationTo, effectFrom, false, effectTo, false);
    }

    public Bitemporality withEffect(CprEffect effect) {
        return new Bitemporality(this.registrationFrom, this.registrationTo, effect.getEffectFrom(), effect.getEffectFromUncertain(), effect.getEffectTo(), effect.getEffectToUncertain());
    }

    public static final char COMPARE_REGISTRATION_FROM = 0x01;
    public static final char COMPARE_REGISTRATION_TO = 0x02;
    public static final char COMPARE_REGISTRATION = COMPARE_REGISTRATION_FROM | COMPARE_REGISTRATION_TO;
    public static final char COMPARE_EFFECT_FROM = 0x04;
    public static final char COMPARE_EFFECT_TO = 0x08;
    public static final char COMPARE_EFFECT = COMPARE_EFFECT_FROM | COMPARE_EFFECT_TO;
    public static final char COMPARE_ALL = COMPARE_REGISTRATION | COMPARE_EFFECT;

    public static final char EXCLUDE_REGISTRATION_FROM = COMPARE_ALL ^ COMPARE_REGISTRATION_FROM;
    public static final char EXCLUDE_REGISTRATION_TO = COMPARE_ALL ^ COMPARE_REGISTRATION_TO;
    public static final char EXCLUDE_EFFECT_FROM = COMPARE_ALL ^ COMPARE_EFFECT_FROM;
    public static final char EXCLUDE_EFFECT_TO = COMPARE_ALL ^ COMPARE_EFFECT_TO;

    @Override
    public boolean equals(Object o) {
        return this.equals(o, COMPARE_ALL);
    }

    public boolean equals(Object o, char compare) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bitemporality that = (Bitemporality) o;

        if (((compare & COMPARE_EFFECT_FROM) != 0) && (effectFromUncertain != that.effectFromUncertain)) return false;
        if (((compare & COMPARE_EFFECT_TO) != 0) && (effectToUncertain != that.effectToUncertain)) return false;
        if (((compare & COMPARE_REGISTRATION_FROM) != 0) && (registrationFrom != null ? !registrationFrom.equals(that.registrationFrom) : that.registrationFrom != null))
            return false;
        if (((compare & COMPARE_REGISTRATION_TO) != 0) && (registrationTo != null ? !registrationTo.equals(that.registrationTo) : that.registrationTo != null))
            return false;
        if (((compare & COMPARE_EFFECT_FROM) != 0) && (effectFrom != null ? !effectFrom.equals(that.effectFrom) : that.effectFrom != null))
            return false;
        if (((compare & COMPARE_EFFECT_TO) != 0) && (effectTo != null ? !effectTo.equals(that.effectTo) : that.effectTo != null))
            return false;
        return true;
    }

    public boolean equalRegistration(Bitemporality o) {
        return o != null && Objects.equals(this.registrationFrom, o.registrationFrom) && Objects.equals(this.registrationTo, o.registrationTo);
    }

    public boolean equalEffect(Bitemporality o) {
        return o != null && Objects.equals(this.effectFrom, o.effectFrom) && Objects.equals(this.effectTo, o.effectTo);
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

    public boolean matches(OffsetDateTime registrationTime, CprEffect effect) {
        return Equality.equal(this.registrationFrom, registrationTime) && effect.compareRange(this);
    }

    public boolean overlapsRegistration(OffsetDateTime rangeStart, OffsetDateTime rangeEnd) {
        return (this.registrationFrom == null || rangeEnd == null || !rangeEnd.isBefore(this.registrationFrom)) && (this.registrationTo == null || rangeStart == null || !rangeStart.isAfter(this.registrationTo));
    }

    public boolean overlapsEffect(OffsetDateTime rangeStart, OffsetDateTime rangeEnd) {
        return (this.effectFrom == null || rangeEnd == null || !rangeEnd.isBefore(this.effectFrom)) && (this.effectTo == null || rangeStart == null || !rangeStart.isAfter(this.effectTo));
    }

    public boolean overlaps(Bitemporality other) {
        return this.overlapsRegistration(other.registrationFrom, other.registrationTo) && this.overlapsEffect(other.effectFrom, other.effectTo);
    }

    public boolean containsRegistration(OffsetDateTime rangeStart, OffsetDateTime rangeEnd) {
        return (this.registrationFrom == null || (rangeStart != null && !rangeStart.isBefore(this.registrationFrom))) && (this.registrationTo == null || (rangeEnd != null && !rangeEnd.isAfter(this.registrationTo)));
    }

    public boolean containsEffect(OffsetDateTime rangeStart, OffsetDateTime rangeEnd) {
        return (this.effectFrom == null || (rangeStart != null && !rangeStart.isBefore(this.effectFrom))) && (this.effectTo == null || (rangeEnd != null && !rangeEnd.isAfter(this.effectTo)));
    }

    public boolean contains(Bitemporality other) {
        return this.containsRegistration(other.registrationFrom, other.registrationTo) && this.containsEffect(other.effectFrom, other.effectTo);
    }

    public String toString() {
        return this.registrationFrom + "|" + this.registrationTo + "|" + this.effectFrom + "|" + this.effectTo;
    }

    @Override
    public int compareTo(Bitemporality o) {
        int c = Equality.compare(this.registrationFrom, o.registrationFrom, OffsetDateTime.class, false);
        if (c == 0) {
            c = Equality.compare(this.registrationTo, o.registrationTo, OffsetDateTime.class, true);
        }
        if (c == 0) {
            c = Equality.compare(this.effectFrom, o.effectFrom, OffsetDateTime.class, false);
        }
        if (c == 0) {
            c = Equality.compare(this.effectTo, o.effectTo, OffsetDateTime.class, true);
        }
        return c;
    }
}

package dk.magenta.datafordeler.cpr.records;

import dk.magenta.datafordeler.core.util.Equality;
import dk.magenta.datafordeler.cpr.data.CprEffect;

import java.time.OffsetDateTime;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bitemporality that = (Bitemporality) o;

        if (effectFromUncertain != that.effectFromUncertain) return false;
        if (effectToUncertain != that.effectToUncertain) return false;
        if (registrationFrom != null ? !registrationFrom.equals(that.registrationFrom) : that.registrationFrom != null)
            return false;
        if (effectFrom != null ? !effectFrom.equals(that.effectFrom) : that.effectFrom != null)
            return false;
        return effectTo != null ? effectTo.equals(that.effectTo) : that.effectTo == null;
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

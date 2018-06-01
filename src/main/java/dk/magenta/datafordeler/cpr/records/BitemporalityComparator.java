package dk.magenta.datafordeler.cpr.records;

import java.time.OffsetDateTime;
import java.util.Comparator;

public class BitemporalityComparator implements Comparator<Bitemporality> {

    public enum Type {
        REGISTRATION_FROM,
        REGISTRATION_TO,
        EFFECT_FROM,
        EFFECT_TO
    }

    private Type type;

    public BitemporalityComparator(Type type) {
        this.type = type;
    }

    private static final Comparator<OffsetDateTime> nullsFirst = Comparator.nullsFirst(OffsetDateTime.timeLineOrder());
    private static final Comparator<OffsetDateTime> nullsLast = Comparator.nullsLast(OffsetDateTime.timeLineOrder());

    @Override
    public int compare(Bitemporality o1, Bitemporality o2) {
        int c;
        switch (this.type) {

            case REGISTRATION_FROM:
                return nullsFirst.compare(o1.registrationFrom, o2.registrationFrom);

            case REGISTRATION_TO:
                return nullsLast.compare(o1.registrationTo, o2.registrationTo);

            case EFFECT_FROM:
                return nullsFirst.compare(o1.effectFrom, o2.effectFrom);

            case EFFECT_TO:
                return nullsLast.compare(o1.effectTo, o2.effectTo);
        }
        return 0;
    }

}

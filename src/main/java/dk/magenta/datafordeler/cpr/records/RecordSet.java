package dk.magenta.datafordeler.cpr.records;

import java.util.HashSet;

public class RecordSet<E extends CprBitemporalRecord> extends HashSet<E> {
    @Override
    public boolean add(E e) {
        if (e != null) {
            for (E item : this) {
                if (e.equals(item)) {
                    this.remove(item);
                    break;
                }
            }
        }
        return super.add(e);
    }

}

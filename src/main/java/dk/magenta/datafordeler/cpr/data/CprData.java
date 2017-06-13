package dk.magenta.datafordeler.cpr.data;

import dk.magenta.datafordeler.core.database.DataItem;
import dk.magenta.datafordeler.core.database.Effect;

import javax.persistence.MappedSuperclass;

/**
 * Created by lars on 19-05-17.
 */
@MappedSuperclass
public abstract class CprData<V extends Effect, D extends DataItem> extends DataItem<V, D> {
}

package dk.magenta.datafordeler.cpr.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.core.database.DataItem;
import dk.magenta.datafordeler.core.database.Effect;
import dk.magenta.datafordeler.core.database.RecordCollection;
import dk.magenta.datafordeler.core.database.RecordData;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.OffsetDateTime;

/**
 * Created by lars on 19-05-17.
 */
@MappedSuperclass
public abstract class CprData<V extends Effect, D extends DataItem> extends DataItem<V, D> {
}

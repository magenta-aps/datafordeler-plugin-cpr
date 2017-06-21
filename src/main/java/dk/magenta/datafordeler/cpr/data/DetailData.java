package dk.magenta.datafordeler.cpr.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dk.magenta.datafordeler.core.database.DatabaseEntry;
import dk.magenta.datafordeler.core.database.Identification;

import javax.persistence.MappedSuperclass;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 12-06-17.
 */
@MappedSuperclass
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class DetailData extends DatabaseEntry {

    public abstract Map<String, Object> asMap();

    @JsonIgnore
    public HashMap<String, Identification> getReferences() {
        return new HashMap<>();
    }

    public void updateReferences(HashMap<String, Identification> references) {
    }

    /**
     * Obtain contained data as a Map
     * Internally used for comparing DataItems
     * @return Map of all relevant attributes
     */
    public Map<String, Object> databaseFields() {
        return this.asMap();
    }
}

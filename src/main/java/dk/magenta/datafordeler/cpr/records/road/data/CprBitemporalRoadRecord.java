package dk.magenta.datafordeler.cpr.records.road.data;

import dk.magenta.datafordeler.cpr.records.CprBitemporalRecord;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class CprBitemporalRoadRecord<S extends CprBitemporalRoadRecord<S>> extends CprBitemporalRecord<RoadEntity, S> {

    public static final String DB_FIELD_ENTITY = CprBitemporalRecord.DB_FIELD_ENTITY;

    public abstract S clone();

    public boolean updateBitemporalityByCloning() {
        return false;
    }

}

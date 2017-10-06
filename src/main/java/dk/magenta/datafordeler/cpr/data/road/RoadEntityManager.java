package dk.magenta.datafordeler.cpr.data.road;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.RegistrationReference;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.cpr.data.CprEntityManager;
import dk.magenta.datafordeler.cpr.data.road.data.RoadBaseData;
import dk.magenta.datafordeler.cpr.parsers.CprSubParser;
import dk.magenta.datafordeler.cpr.parsers.RoadParser;
import dk.magenta.datafordeler.cpr.records.road.RoadDataRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.UUID;

/**
 * Created by lars on 16-05-17.
 */
@Component("cprRoadEntityMananger")
public class RoadEntityManager extends CprEntityManager<RoadDataRecord, RoadEntity, RoadRegistration, RoadEffect, RoadBaseData> {

    @Autowired
    private RoadEntityService roadEntityService;

    @Autowired
    private RoadParser roadParser;

    @Autowired
    private SessionManager sessionManager;


    private static class RoadIdentifier {
        public int kommunekode;
        public int vejkode;

        public RoadIdentifier(int kommunekode, int vejkode) {
            this.kommunekode = kommunekode;
            this.vejkode = vejkode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RoadIdentifier that = (RoadIdentifier) o;

            if (kommunekode != that.kommunekode) return false;
            return vejkode == that.vejkode;
        }

        @Override
        public int hashCode() {
            int result = kommunekode;
            result = 31 * result + vejkode;
            return result;
        }
    }

    public RoadEntityManager() {
        this.managedEntityClass = RoadEntity.class;
        this.managedEntityReferenceClass = RoadEntityReference.class;
        this.managedRegistrationClass = RoadRegistration.class;
        this.managedRegistrationReferenceClass = RoadRegistrationReference.class;
    }

    @Override
    protected String getBaseName() {
        return "road";
    }

    @Override
    public RoadEntityService getEntityService() {
        return this.roadEntityService;
    }

    @Override
    public String getSchema() {
        return RoadEntity.schema;
    }

    @Override
    protected RegistrationReference createRegistrationReference(URI uri) {
        return new RoadRegistrationReference(uri);
    }

    @Override
    protected SessionManager getSessionManager() {
        return this.sessionManager;
    }

    @Override
    protected CprSubParser<RoadDataRecord> getParser() {
        return this.roadParser;
    }

    @Override
    protected Class<RoadEntity> getEntityClass() {
        return RoadEntity.class;
    }

    @Override
    protected UUID generateUUID(RoadDataRecord record) {
        return RoadEntity.generateUUID(record.getMunicipalityCode(), record.getRoadCode());
    }

    @Override
    protected RoadEntity createBasicEntity(RoadDataRecord record) {
        RoadEntity roadEntity = new RoadEntity();
        roadEntity.setKommunekode(record.getMunicipalityCode());
        roadEntity.setVejkode(record.getRoadCode());
        return roadEntity;
    }

    @Override
    protected RoadBaseData createDataItem() {
        return new RoadBaseData();
    }

}

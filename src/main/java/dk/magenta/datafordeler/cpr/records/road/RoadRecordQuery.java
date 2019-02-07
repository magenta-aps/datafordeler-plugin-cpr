package dk.magenta.datafordeler.cpr.records.road;

import dk.magenta.datafordeler.core.database.BaseLookupDefinition;
import dk.magenta.datafordeler.core.database.LookupDefinition;
import dk.magenta.datafordeler.core.fapi.BaseQuery;
import dk.magenta.datafordeler.core.fapi.ParameterMap;
import dk.magenta.datafordeler.core.fapi.QueryField;
import dk.magenta.datafordeler.cpr.records.road.data.RoadEntity;

import java.util.*;

/**
 * Container for a query for Persons, defining fields and database lookup
 */
public class RoadRecordQuery extends BaseQuery {

    
    public static final String VEJKODE = RoadEntity.IO_FIELD_ROAD_CODE;
    public static final String KOMMUNEKODE = RoadEntity.IO_FIELD_MUNIPALITY_CODE;

    @QueryField(type = QueryField.FieldType.STRING, queryName = VEJKODE)
    private List<String> vejkoder = new ArrayList<>();

    public Collection<String> getVejkoder() {
        return this.vejkoder;
    }

    public void addVejkode(String vejkode) {
        this.vejkoder.add(vejkode);
        if (vejkode != null) {
                this.increaseDataParamCount();
            }
    }

    public void setVejkode(String vejkode) {
        this.vejkoder.clear();
        this.addVejkode(vejkode);
    }

    public void setVejkode(int vejkode) {
        this.setVejkode(Integer.toString(vejkode));
    }

    @QueryField(type = QueryField.FieldType.STRING, queryName = KOMMUNEKODE)
    private List<String> kommunekoder = new ArrayList<>();

    public List<String> getKommunekoder() {
        return kommunekoder;
    }

    public void addKommunekode(String kommunekode) {
        this.kommunekoder.add(kommunekode);
        if (kommunekode != null) {
            this.increaseDataParamCount();
        }
    }

    public void setKommunekode(String kommunekode) {
        this.kommunekoder.clear();
        this.addKommunekode(kommunekode);
    }

    public void addKommunekode(int kommunekode) {
        this.addKommunekode(Integer.toString(kommunekode));
    }

    public void clearKommunekode() {
        this.kommunekoder.clear();
    }

    @Override
    public Map<String, Object> getSearchParameters() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(VEJKODE, this.vejkoder);
        map.put(KOMMUNEKODE, this.kommunekoder);
        return map;
    }

    @Override
    public void setFromParameters(ParameterMap parameters) {
        if (parameters.containsKey(VEJKODE)) {
            for (String vejkode : parameters.get(VEJKODE)) {
                this.addVejkode(vejkode);
            }
        }
        if (parameters.containsKey(KOMMUNEKODE)) {
            for (String kommunekode : parameters.get(KOMMUNEKODE)) {
                this.addKommunekode(kommunekode);
            }
        }
    }


        
    
    @Override
    public BaseLookupDefinition getLookupDefinition() {
        BaseLookupDefinition lookupDefinition = new BaseLookupDefinition();
        if (!this.getVejkoder().isEmpty()) {
            lookupDefinition.put(LookupDefinition.entityref + LookupDefinition.separator + RoadEntity.DB_FIELD_ROAD_CODE, this.getVejkoder(), Integer.class);
        }
        if (this.getKommunekoder() != null) {
            lookupDefinition.put(LookupDefinition.entityref + LookupDefinition.separator + RoadEntity.DB_FIELD_MUNIPALITY_CODE, this.getKommunekoder(), Integer.class);
        }
        /*if (!this.getKommunekodeRestriction().isEmpty()) {
            lookupDefinition.put(LookupDefinition.entityref + LookupDefinition.separator + PersonEntity.DB_FIELD_ADDRESS + LookupDefinition.separator + AddressDataRecord.DB_FIELD_MUNICIPALITY_CODE, this.getKommunekodeRestriction(), Integer.class);
        }*/
        return lookupDefinition;
    }

}

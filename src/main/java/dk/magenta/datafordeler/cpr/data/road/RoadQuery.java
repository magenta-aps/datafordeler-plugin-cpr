package dk.magenta.datafordeler.cpr.data.road;

import dk.magenta.datafordeler.core.database.LookupDefinition;
import dk.magenta.datafordeler.core.fapi.ParameterMap;
import dk.magenta.datafordeler.core.fapi.QueryField;
import dk.magenta.datafordeler.cpr.data.CprQuery;
import dk.magenta.datafordeler.cpr.data.road.data.RoadBaseData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 19-05-17.
 */
public class RoadQuery extends CprQuery<RoadEntity> {

    public static final String CODE = "code";
    public static final String NAME = "name";

    @QueryField(type = QueryField.FieldType.INT)
    private String code;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @QueryField(type = QueryField.FieldType.STRING)
    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Map<String, Object> getSearchParameters() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(CODE, this.code);
        map.put(NAME, this.name);
        return map;
    }

    @Override
    public void setFromParameters(ParameterMap parameters) {
        this.setCode(parameters.getFirst(CODE));
        this.setName(parameters.getFirst(NAME));
    }

    @Override
    public Class<RoadEntity> getEntityClass() {
        return RoadEntity.class;
    }

    @Override
    public Class getDataClass() {
        return RoadBaseData.class;
    }


    @Override
    public LookupDefinition getLookupDefinition() {
        LookupDefinition lookupDefinition = new LookupDefinition(this);
        if (this.code != null) {
            lookupDefinition.put(LookupDefinition.entityref + ".code", this.code);
        }
        if (this.name != null) {
            lookupDefinition.put("coreData.name", this.name);
        }
        return lookupDefinition;
    }

}

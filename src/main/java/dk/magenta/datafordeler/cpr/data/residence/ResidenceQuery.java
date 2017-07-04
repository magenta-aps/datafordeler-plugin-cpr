package dk.magenta.datafordeler.cpr.data.residence;

import dk.magenta.datafordeler.core.database.LookupDefinition;
import dk.magenta.datafordeler.core.fapi.ParameterMap;
import dk.magenta.datafordeler.core.fapi.QueryField;
import dk.magenta.datafordeler.cpr.data.CprQuery;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.data.residence.data.ResidenceBaseData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 19-05-17.
 */
public class ResidenceQuery extends CprQuery<ResidenceEntity> {

    public static final String MUNICIPALITY_CODE = "municipalityCode";
    public static final String ROAD_CODE = "roadCode";
    public static final String HOUSE_NUMBER = "houseNumber";
    public static final String FLOOR = "floor";
    public static final String DOOR = "door";

    @QueryField(type = QueryField.FieldType.INT, queryName = MUNICIPALITY_CODE)
    private String municipalityCode;

    public String getMunicipalityCode() {
        return this.municipalityCode;
    }

    public void setMunicipalityCode(String municipalityCode) {
        this.municipalityCode = municipalityCode;
    }

    @QueryField(type = QueryField.FieldType.STRING, queryName = ROAD_CODE)
    private String roadCode;

    public String getRoadCode() {
        return this.roadCode;
    }

    public void setRoadCode(String roadCode) {
        this.roadCode = roadCode;
    }

    @QueryField(type = QueryField.FieldType.STRING, queryName = HOUSE_NUMBER)
    private String houseNumber;

    public String getHouseNumber() {
        return this.houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    @QueryField(type = QueryField.FieldType.STRING, queryName = FLOOR)
    private String floor;

    public String getFloor() {
        return this.floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    @QueryField(type = QueryField.FieldType.STRING, queryName = DOOR)
    private String door;

    public String getDoor() {
        return this.door;
    }

    public void setDoor(String door) {
        this.door = door;
    }

    @Override
    public Map<String, Object> getSearchParameters() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(MUNICIPALITY_CODE, this.municipalityCode);
        map.put(ROAD_CODE, this.roadCode);
        map.put(HOUSE_NUMBER, this.houseNumber);
        map.put(FLOOR, this.floor);
        map.put(DOOR, this.door);
        return map;
    }

    @Override
    public void setFromParameters(ParameterMap parameters) {
        this.setMunicipalityCode(parameters.getFirst(MUNICIPALITY_CODE));
        this.setRoadCode(parameters.getFirst(ROAD_CODE));
        this.setHouseNumber(parameters.getFirst(HOUSE_NUMBER));
        this.setFloor(parameters.getFirst(FLOOR));
        this.setDoor(parameters.getFirst(DOOR));
    }

    @Override
    public Class<ResidenceEntity> getEntityClass() {
        return ResidenceEntity.class;
    }

    @Override
    public Class getDataClass() {
        return ResidenceBaseData.class;
    }


    @Override
    public LookupDefinition getLookupDefinition() {
        LookupDefinition lookupDefinition = new LookupDefinition(this);
        if (this.municipalityCode != null) {
            lookupDefinition.put("municipalityCode", this.municipalityCode);
        }
        if (this.roadCode != null) {
            lookupDefinition.put("roadCode", this.roadCode);
        }
        if (this.houseNumber != null) {
            lookupDefinition.put("houseNumber", this.houseNumber);
        }
        if (this.floor != null) {
            lookupDefinition.put("floor", this.floor);
        }
        if (this.door != null) {
            lookupDefinition.put("door", this.door);
        }
        return lookupDefinition;
    }

}

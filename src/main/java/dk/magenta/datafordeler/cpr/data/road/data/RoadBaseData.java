package dk.magenta.datafordeler.cpr.data.road.data;

import dk.magenta.datafordeler.core.database.LookupDefinition;
import dk.magenta.datafordeler.cpr.data.CprData;
import dk.magenta.datafordeler.cpr.data.road.RoadEffect;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 16-05-17.
 */
@Entity
@Table(name="cpr_road_data")
public class RoadBaseData extends CprData<RoadEffect, RoadBaseData> {

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private RoadCoreData coreData;


    public void setCore(int toMunicipalityCode, int toRoadCode, int fromMunicipalityCode, int fromRoadCode, String addressingName, String name) {
        if (this.coreData == null) {
            this.coreData = new RoadCoreData();
        }
        this.coreData.setToMunicipalityCode(toMunicipalityCode);
        this.coreData.setToRoadCode(toRoadCode);
        this.coreData.setFromMunicipalityCode(fromMunicipalityCode);
        this.coreData.setFromRoadCode(fromRoadCode);
        this.coreData.setAddressingName(addressingName);
        this.coreData.setName(name);
    }


    /**
     * Return a map of attributes, including those from the superclass
     *
     * @return
     */
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        if (this.coreData != null) {
            map.putAll(this.coreData.asMap());
        }
        return map;
    }

    public LookupDefinition getLookupDefinition() {
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setMatchNulls(true);

        if (this.coreData != null) {
            lookupDefinition.putAll("coreData", this.coreData.databaseFields());
        }

        return lookupDefinition;
    }

}
package dk.magenta.datafordeler.cpr.data.road.data;

import dk.magenta.datafordeler.core.database.LookupDefinition;
import dk.magenta.datafordeler.cpr.data.CprData;
import dk.magenta.datafordeler.cpr.data.DetailData;
import dk.magenta.datafordeler.cpr.data.road.RoadEffect;
import dk.magenta.datafordeler.cpr.data.unversioned.PostCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lars on 16-05-17.
 */
@Entity
@Table(name="cpr_road_data")
public class RoadBaseData extends CprData<RoadEffect, RoadBaseData> {

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private RoadCoreData coreData;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("memoNumber")
    private List<RoadMemoData> memoData = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoadPostcodeData> postcodeData = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoadCityData> cityData = new ArrayList<>();


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

    public void addMemo(int memoNumber, String memoText) {
        RoadMemoData memoData = new RoadMemoData();
        memoData.setMemoNumber(memoNumber);
        memoData.setMemoText(memoText);
        this.memoData.add(memoData);
    }

    public void addPostcode(String houseNumberFrom, String houseNumberTo, boolean even, PostCode postCode) {
        RoadPostcodeData postcodeData = new RoadPostcodeData();
        postcodeData.setHouseNumberFrom(houseNumberFrom);
        postcodeData.setHouseNumberTo(houseNumberTo);
        postcodeData.setEven(even);
        postcodeData.setPostCode(postCode);
        this.postcodeData.add(postcodeData);
    }

    public void addCity(String houseNumberFrom, String houseNumberTo, boolean even, String cityName) {
        RoadCityData cityData = new RoadCityData();
        cityData.setHouseNumberFrom(houseNumberFrom);
        cityData.setHouseNumberTo(houseNumberTo);
        cityData.setEven(even);
        cityData.setCityName(cityName);
        this.cityData.add(cityData);
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
        if (this.memoData != null && !this.memoData.isEmpty()) {
            map.put("memo", this.memoData);
        }
        if (this.postcodeData != null && !this.postcodeData.isEmpty()) {
            map.put("postcode", this.postcodeData);
        }
        if (this.cityData != null && !this.cityData.isEmpty()) {
            map.put("city", this.cityData);
        }
        return map;
    }

    public LookupDefinition getLookupDefinition() {
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setMatchNulls(true);

        if (this.coreData != null) {
            lookupDefinition.putAll("coreData", this.coreData.databaseFields());
        }
        if (this.memoData != null) {
            lookupDefinition.putAll("memoData", DetailData.listDatabaseFields(this.memoData));
        }
        if (this.postcodeData != null) {
            lookupDefinition.putAll("postcodeData", DetailData.listDatabaseFields(this.postcodeData));
        }
        if (this.cityData != null) {
            lookupDefinition.putAll("cityData", DetailData.listDatabaseFields(this.cityData));
        }
        return lookupDefinition;
    }

}
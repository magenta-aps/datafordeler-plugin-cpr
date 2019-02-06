package dk.magenta.datafordeler.cpr.records.residence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.cpr.data.CprRecordEntityManager;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceEffect;
import dk.magenta.datafordeler.cpr.data.residence.data.ResidenceBaseData;
import dk.magenta.datafordeler.cpr.records.CprBitemporality;
import dk.magenta.datafordeler.cpr.records.CprGeoRecord;
import dk.magenta.datafordeler.cpr.records.person.CprBitemporalPersonRecord;
import dk.magenta.datafordeler.cpr.records.road.data.CprBitemporalRoadRecord;
import org.hibernate.Session;

import java.time.OffsetDateTime;
import java.util.*;

/**
 * Record for Residence (type 002).
 */
public class ResidenceRecord extends CprGeoRecord<ResidenceEffect, ResidenceBaseData> {

    public static final String RECORDTYPE_RESIDENCE = "002";

    private CprBitemporality residenceTemporality;

    public ResidenceRecord(String line) throws ParseException {
        super(line);
        this.obtain("komkod", 4, 4);
        this.obtain("vejkod", 8, 4);
        this.obtain("husnr", 12, 4);
        this.obtain("etage", 16, 2);
        this.obtain("sidedoer", 18, 4);
        this.obtain("timestamp", 22, 12);
        this.obtain("haenstart", 35, 12);
        this.obtain("lokalitet", 59, 34);

        this.residenceTemporality = new CprBitemporality(this.getOffsetDateTime("timestamp"), null, this.getOffsetDateTime("haenstart"), false, null, false);
    }





    protected ResidenceBaseData getBaseDataItem(HashMap<ResidenceEffect, ResidenceBaseData> data) {
        return this.getBaseDataItem(data, null, false, null, false);
    }

    protected ResidenceBaseData getBaseDataItem(HashMap<ResidenceEffect, ResidenceBaseData> data, OffsetDateTime effectFrom, boolean effectFromUncertain) {
        return this.getBaseDataItem(data, effectFrom, effectFromUncertain, null, false);
    }

    protected ResidenceBaseData getBaseDataItem(HashMap<ResidenceEffect, ResidenceBaseData> data, OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        ResidenceEffect effect = null;
        for (ResidenceEffect e : data.keySet()) {
            if (e.compareRange(effectFrom, effectFromUncertain, effectTo, effectToUncertain)) {
                effect = e;
                break;
            }
        }
        /*if (effect == null) {
            effect = this.createEffect(effectFrom, effectFromUncertain, effectTo, effectToUncertain);
            data.put(effect, this.createEmptyBaseData());
        }*/
        return data.get(effect);
    }

    public boolean filter(ObjectNode importConfiguration) {
        if (importConfiguration != null && importConfiguration.size() > 0) {
            if (importConfiguration.has(CprRecordEntityManager.IMPORTCONFIG_RECORDTYPE)) {
                HashSet<String> acceptedRecordTypes = new HashSet<>(
                        getConfigValueAsText(importConfiguration.get(CprRecordEntityManager.IMPORTCONFIG_RECORDTYPE), "%03d")
                );
                return acceptedRecordTypes.contains(this.getRecordType());
            }
        }
        return true;
    }

    protected static List<String> getConfigValueAsText(JsonNode value, String fmtNumber) {
        ArrayList<String> output = new ArrayList<>();
        if (value != null) {
            if (value.isTextual()) {
                output.add(value.asText());
            } else if (value.isIntegralNumber()) {
                if (fmtNumber == null) {
                    fmtNumber = "%d";
                }
                output.add(String.format(fmtNumber, value.asLong()));
            } else if (value.isArray()) {
                for (JsonNode j : value) {
                    output.addAll(getConfigValueAsText(j, fmtNumber));
                }
            }
        }
        return output;
    }




    @Override
    public String getRecordType() {
        return RECORDTYPE_RESIDENCE;
    }

    //TODO: RECONSIDERE THE REFACTORING OF THIS CLASS
    @Override
    public List<CprBitemporalRoadRecord> getBitemporalRecords() {
        return null;
    }


    public int getMunicipalityCode() {
        return this.getInt("komkod");
    }

    public int getRoadCode() {
        return this.getInt("vejkod");
    }

    public String getHouseNumber() {
        return this.getString("husnr", true);
    }

    public String getFloor() {
        return this.getString("etage", true);
    }

    public String getDoor() {
        return this.getString("sidedoer",true);
    }

}
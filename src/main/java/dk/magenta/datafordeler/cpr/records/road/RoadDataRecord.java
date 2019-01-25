package dk.magenta.datafordeler.cpr.records.road;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.data.CprRecordEntityManager;
import dk.magenta.datafordeler.cpr.data.road.RoadEffect;
import dk.magenta.datafordeler.cpr.data.road.data.RoadBaseData;
import dk.magenta.datafordeler.cpr.records.CprDataRecord;
import dk.magenta.datafordeler.cpr.records.CprGeoRecord;

import java.time.OffsetDateTime;
import java.util.*;

/**
 * Superclass for Road records
 */
public abstract class RoadDataRecord extends CprGeoRecord<RoadEffect, RoadBaseData> {

    public static final String RECORDTYPE_ROAD = "001";
    public static final String RECORDTYPE_ROADCITY = "003";
    public static final String RECORDTYPE_ROADPOSTCODE = "004";
    public static final String RECORDTYPE_ROADMEMO = "005";
    // TODO: Add one for each data type

    public RoadDataRecord(String line) throws ParseException {
        super(line);
        this.obtain("komkod", 4, 4);
        this.obtain("vejkod", 8, 4);
    }




    protected RoadBaseData getBaseDataItem(HashMap<RoadEffect, RoadBaseData> data) {
        return this.getBaseDataItem(data, null, false, null, false);
    }

    protected RoadBaseData getBaseDataItem(HashMap<RoadEffect, RoadBaseData> data, OffsetDateTime effectFrom, boolean effectFromUncertain) {
        return this.getBaseDataItem(data, effectFrom, effectFromUncertain, null, false);
    }

    protected RoadBaseData getBaseDataItem(HashMap<RoadEffect, RoadBaseData> data, OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        RoadEffect effect = null;
        for (RoadEffect e : data.keySet()) {
            if (e.compareRange(effectFrom, effectFromUncertain, effectTo, effectToUncertain)) {
                effect = e;
                break;
            }
        }
        if (effect == null) {
            effect = this.createEffect(effectFrom, effectFromUncertain, effectTo, effectToUncertain);
            data.put(effect, this.createEmptyBaseData());
        }
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







    public int getMunicipalityCode() {
        return this.getInt("komkod");
    }

    public int getRoadCode() {
        return this.getInt("vejkod");
    }

    public HashSet<OffsetDateTime> getRegistrationTimestamps() {
        return new HashSet<>();
    }

    protected RoadBaseData createEmptyBaseData() {
        return new RoadBaseData();
    }

    @Override
    protected RoadEffect createEffect(OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
        RoadEffect effect = new RoadEffect(null, effectFrom, effectTo);
        effect.setEffectFromUncertain(effectFromUncertain);
        effect.setEffectToUncertain(effectToUncertain);
        return effect;
    }

}

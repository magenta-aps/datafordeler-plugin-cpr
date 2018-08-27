package dk.magenta.datafordeler.cpr.data.road;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.magenta.datafordeler.core.fapi.BaseQuery;
import dk.magenta.datafordeler.core.fapi.OutputWrapper;
import dk.magenta.datafordeler.core.fapi.Query;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.core.util.OffsetDateTimeAdapter;
import dk.magenta.datafordeler.cpr.data.road.data.*;
import dk.magenta.datafordeler.cpr.records.Bitemporality;
import dk.magenta.datafordeler.cpr.records.BitemporalityComparator;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RoadOutputWrapper extends OutputWrapper<RoadEntity> {

    private ObjectMapper objectMapper;

    @Override
    public Object wrapResult(RoadEntity input, BaseQuery query) {
        objectMapper = new ObjectMapper();
        ObjectNode root = objectMapper.createObjectNode();
        root.put(RoadEntity.IO_FIELD_UUID, input.getUUID().toString());
        root.put(RoadEntity.IO_FIELD_DOMAIN, input.getDomain());
        root.put(RoadEntity.IO_FIELD_MUNICIPALITYCODE, input.getKommunekode());
        root.put(RoadEntity.IO_FIELD_ROADCODE, input.getVejkode());
        Bitemporality overlap = new Bitemporality(query.getRegistrationFrom(), query.getRegistrationTo(), query.getEffectFrom(), query.getEffectTo());
        ArrayNode registreringer = this.getRegistrations(input, overlap);
        root.set(RoadEntity.IO_FIELD_REGISTRATIONS, registreringer);
        return root;
    }

    public ArrayNode getRegistrations(RoadEntity entity, Bitemporality mustOverlap) {

        ArrayNode registrationsNode = objectMapper.createArrayNode();
        //HashMap<Bitemporality, ObjectNode> data = new HashMap<>();
        ListHashMap<Bitemporality, RoadBaseData> data = new ListHashMap<>();

        // Populér map med bitemp -> json
        // Loop over alle registrationBorders
        //     apply data i rækkefølge (sorteret efter bitemp)

        for (RoadRegistration registration : entity.getRegistrations()) {
            for (RoadEffect virkning : registration.getEffects()) {
                //ObjectNode dataPiece = objectMapper.createObjectNode();
                Bitemporality bitemporality = new Bitemporality(registration.getRegistrationFrom(), registration.getRegistrationTo(), virkning.getEffectFrom(), virkning.getEffectTo());
                for (RoadBaseData roadBaseData : virkning.getDataItems()) {
                    data.add(bitemporality, roadBaseData);
                }
            }
        }

        ListHashMap<OffsetDateTime, Bitemporality> startTerminators = new ListHashMap<>();
        ListHashMap<OffsetDateTime, Bitemporality> endTerminators = new ListHashMap<>();

        for (Bitemporality bitemporality : data.keySet()) {
            startTerminators.add(bitemporality.registrationFrom, bitemporality);
            endTerminators.add(bitemporality.registrationTo, bitemporality);
        }

        HashSet<OffsetDateTime> allTerminators = new HashSet<>();
        allTerminators.addAll(startTerminators.keySet());
        allTerminators.addAll(endTerminators.keySet());
        // Create a sorted list of all timestamps where Bitemporalities either begin or end
        ArrayList<OffsetDateTime> terminators = new ArrayList<>(allTerminators);
        terminators.sort(Comparator.nullsFirst(OffsetDateTime::compareTo));
        terminators.add(null);

        HashSet<Bitemporality> presentBitemporalities = new HashSet<>();

        for (int i = 0; i < terminators.size(); i++) {
            OffsetDateTime t = terminators.get(i);
            List<Bitemporality> startingHere = startTerminators.get(t);
            List<Bitemporality> endingHere = (t != null) ? endTerminators.get(t) : null;
            if (startingHere != null) {
                presentBitemporalities.addAll(startingHere);
            }
            if (endingHere != null) {
                presentBitemporalities.removeAll(endingHere);
            }
            if (i < terminators.size() - 1) {
                OffsetDateTime next = terminators.get(i + 1);
                if (!presentBitemporalities.isEmpty()) {
                    if (mustOverlap == null || mustOverlap.overlapsRegistration(t, next)) {
                        ObjectNode registrationNode = objectMapper.createObjectNode();
                        registrationsNode.add(registrationNode);
                        registrationNode.put("registreringFra", formatTime(t));
                        registrationNode.put("registreringTil", formatTime(next));

                        ArrayList<Bitemporality> sortedBitemporalities = new ArrayList<>(presentBitemporalities);
                        sortedBitemporalities.sort(effectComparator);

                        for (Bitemporality bitemporality : sortedBitemporalities) {

                            ArrayList<RoadBaseData> dataItems = data.get(bitemporality);

                            for (RoadBaseData dataItem : dataItems) {
                                OffsetDateTime timestamp = dataItem.getLastUpdated();

                                RoadCoreData roadCoreData = dataItem.getCoreData();
                                if (roadCoreData != null) {
                                    addEffectDataToRegistration(
                                            registrationNode,
                                            RoadBaseData.IO_FIELD_CORE,
                                            createKerneDataNode(bitemporality, timestamp, roadCoreData)
                                    );
                                }

                                Collection<RoadCityData> roadCityData = dataItem.getCityData();
                                if (roadCityData != null && !roadCityData.isEmpty()) {
                                    addEffectDataToRegistration(
                                            registrationNode,
                                            RoadBaseData.IO_FIELD_CITY,
                                            createByNode(bitemporality, timestamp, roadCityData)
                                    );
                                }

                                Collection<RoadMemoData> roadMemoData = dataItem.getMemoData();
                                if (roadMemoData != null && !roadMemoData.isEmpty()) {
                                    addEffectDataToRegistration(
                                            registrationNode,
                                            RoadBaseData.IO_FIELD_MEMO,
                                            createMemoNode(bitemporality, timestamp, roadMemoData)
                                    );
                                }

                                Collection<RoadPostcodeData> roadPostcodeData = dataItem.getPostcodeData();
                                if (roadPostcodeData != null && !roadPostcodeData.isEmpty()) {
                                    addEffectDataToRegistration(
                                            registrationNode,
                                            RoadBaseData.IO_FIELD_POSTCODE,
                                            createPostNode(bitemporality, timestamp, roadPostcodeData)
                                    );
                                }


                            }
                        }
                    }
                }
            }
        }
        return registrationsNode;
    }

    protected static final Comparator<Bitemporality> effectComparator =
            Comparator.nullsFirst(new BitemporalityComparator(BitemporalityComparator.Type.EFFECT_FROM))
                    .thenComparing(Comparator.nullsLast(new BitemporalityComparator(BitemporalityComparator.Type.EFFECT_TO)));


    protected static String formatTime(OffsetDateTime time) {
        return formatTime(time, false);
    }

    protected static String formatTime(OffsetDateTime time, boolean asDateOnly) {
        if (time == null) return null;
        return time.format(asDateOnly ? DateTimeFormatter.ISO_LOCAL_DATE : DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    protected static String formatTime(LocalDate time) {
        if (time == null) return null;
        return time.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    protected void addEffectDataToRegistration(ObjectNode output, String key, ObjectNode value) {
        if (!output.has(key) || output.get(key).isNull()) {
            output.set(key, objectMapper.createArrayNode());
        }
        ((ArrayNode) output.get(key)).add(value);
    }

    protected void addEffectDataToRegistration(ObjectNode output, String key, Collection<ObjectNode> value) {
        if (!output.has(key) || output.get(key).isNull()) {
            output.set(key, objectMapper.createArrayNode());
        }
        ((ArrayNode) output.get(key)).addAll(value);
    }

    protected ObjectNode createDataNode(Bitemporality bitemporality, OffsetDateTime lastUpdated) {
        return createDataNode(bitemporality, true, lastUpdated);
    }

    protected ObjectNode createDataNode(Bitemporality bitemporality, boolean includeVirkningTil, OffsetDateTime lastUpdated) {
        ObjectNode output = objectMapper.createObjectNode();
        if (bitemporality != null) {
            output.put(
                    RoadEffect.IO_FIELD_EFFECT_FROM,
                    OffsetDateTimeAdapter.toString(bitemporality.effectFrom)
            );
            if (includeVirkningTil) {
                output.put(
                        RoadEffect.IO_FIELD_EFFECT_TO,
                        OffsetDateTimeAdapter.toString(bitemporality.effectTo)
                );
            }
        }
        output.put(
                RoadBaseData.IO_FIELD_LAST_UPDATED,
                lastUpdated != null ? lastUpdated.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null
        );
        return output;
    }

    protected ObjectNode createKerneDataNode(
            Bitemporality bitemporality, OffsetDateTime lastUpdated, RoadCoreData roadCoreData
    ) {
        ObjectNode output = createDataNode(bitemporality, true, lastUpdated);
        output.put(RoadCoreData.IO_FIELD_ADDRESS_NAME, roadCoreData.getAddressingName());
        output.put(RoadCoreData.IO_FIELD_FROM_MUNICIPALITY, roadCoreData.getFromMunicipality());
        output.put(RoadCoreData.IO_FIELD_TO_MUNICIPALITY, roadCoreData.getToMunicipality());
        output.put(RoadCoreData.IO_FIELD_FROM_ROAD, roadCoreData.getFromRoad());
        output.put(RoadCoreData.IO_FIELD_TO_ROAD, roadCoreData.getToRoad());
        output.put(RoadCoreData.IO_FIELD_ROAD_NAME, roadCoreData.getName());
        return output;
    }

    protected Set<ObjectNode> createByNode(
            Bitemporality bitemporality, OffsetDateTime lastUpdated, Collection<RoadCityData> roadCityDataSet
    ) {
        HashSet<ObjectNode> output = new HashSet<>();
        for (RoadCityData roadCityData : roadCityDataSet) {
            ObjectNode item = createDataNode(bitemporality, lastUpdated);
            item.put(RoadCityData.IO_FIELD_CITYNAME, roadCityData.getCityName());
            item.put(RoadCityData.IO_FIELD_EVEN, roadCityData.isEven());
            item.put(RoadCityData.IO_FIELD_HOUSENUMBER_FROM, roadCityData.getHouseNumberFrom());
            item.put(RoadCityData.IO_FIELD_HOUSENUMBER_TO, roadCityData.getHouseNumberTo());
            output.add(item);
        }
        return output;
    }

    protected Set<ObjectNode> createMemoNode(
            Bitemporality bitemporality, OffsetDateTime lastUpdated, Collection<RoadMemoData> roadMemoDataSet
    ) {
        HashSet<ObjectNode> output = new HashSet<>();
        for (RoadMemoData roadMemoData : roadMemoDataSet) {
            ObjectNode item = createDataNode(bitemporality, lastUpdated);
            item.put(RoadMemoData.IO_FIELD_MEMONUMBER, roadMemoData.getMemoNumber());
            item.put(RoadMemoData.IO_FIELD_MEMOLINE, roadMemoData.getMemoText());
            output.add(item);
        }
        return output;
    }

    protected Set<ObjectNode> createPostNode(
            Bitemporality bitemporality, OffsetDateTime lastUpdated, Collection<RoadPostcodeData> roadPostcodeDataSet
    ) {
        HashSet<ObjectNode> output = new HashSet<>();
        for (RoadPostcodeData roadPostcodeData : roadPostcodeDataSet) {
            ObjectNode item = createDataNode(bitemporality, lastUpdated);
            item.putPOJO(RoadPostcodeData.IO_FIELD_POSTCODE, roadPostcodeData.getPostCode());
            item.put(RoadPostcodeData.IO_FIELD_EVEN, roadPostcodeData.isEven());
            item.put(RoadPostcodeData.IO_FIELD_HOUSENUMBER_FROM, roadPostcodeData.getHouseNumberFrom());
            item.put(RoadPostcodeData.IO_FIELD_HOUSENUMBER_TO, roadPostcodeData.getHouseNumberTo());
            output.add(item);
        }
        return output;
    }

}

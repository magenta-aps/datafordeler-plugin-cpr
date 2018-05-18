package dk.magenta.datafordeler.cpr.data.person;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.magenta.datafordeler.core.database.Effect;
import dk.magenta.datafordeler.core.fapi.OutputWrapper;
import dk.magenta.datafordeler.core.fapi.Query;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.data.person.data.*;
import dk.magenta.datafordeler.cpr.records.Bitemporality;
import dk.magenta.datafordeler.cpr.records.BitemporalityComparator;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PersonOutputWrapper extends OutputWrapper<PersonEntity> {

    private ObjectMapper objectMapper;

    @Override
    public Object wrapResult(PersonEntity input, Query query) {
        objectMapper = new ObjectMapper();

        // Root
        ObjectNode root = objectMapper.createObjectNode();

/*
        root.put(PersonEntity.IO_FIELD_UUID, input.getUUID().toString());
        root.put(PersonEntity.IO_FIELD_CPR_NUMBER, input.getPersonnummer());
        root.putPOJO("id", input.getIdentification());
*/
        //ArrayNode registreringer = this.getRegistrations(input, null);
        //root.set(PersonEntity.IO_FIELD_REGISTRATIONS, registreringer);
        root = objectMapper.valueToTree(input);
        return root;
    }


    public ArrayNode getRegistrations(PersonEntity entity, Bitemporality mustOverlap) {


        ArrayNode registrationsNode = objectMapper.createArrayNode();
        //HashMap<Bitemporality, ObjectNode> data = new HashMap<>();
        ListHashMap<Bitemporality, PersonBaseData> data = new ListHashMap<>();

        // Populér map med bitemp -> json
        // Loop over alle registrationBorders
        //     apply data i rækkefølge (sorteret efter bitemp)

        for (PersonRegistration registration : entity.getRegistrations()) {
            for (PersonEffect virkning : registration.getEffects()) {
                //ObjectNode dataPiece = objectMapper.createObjectNode();
                Bitemporality bitemporality = new Bitemporality(registration.getRegistrationFrom(), registration.getRegistrationTo(), virkning.getEffectFrom(), virkning.getEffectTo());
                for (PersonBaseData personBaseData : virkning.getDataItems()) {
                    data.add(bitemporality, personBaseData);
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
            List<Bitemporality> endingHere = endTerminators.get(t);
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
                        ArrayNode effectsNode = objectMapper.createArrayNode();
                        registrationNode.set("virkninger", effectsNode);
                        ArrayList<Bitemporality> sortedEffects = new ArrayList<>(presentBitemporalities);
                        sortedEffects.sort(effectComparator);
                        Bitemporality lastEffect = null;
                        ObjectNode effectNode = null;

                        System.out.println("----------------------");
                        System.out.println(t+" => "+next);

                        for (Bitemporality bitemporality : sortedEffects) {
                            // Implemented in Hibernate filters instead. Each stored effect can be tested against the query filter
                            // on the database level, but registrations are split here and thus cannot be tested in the database
                            // Also, they lack the range end due to the way the incoming data is formatted
                            //if (mustOverlap == null || mustOverlap.overlapsEffect(bitemporality.effectFrom, bitemporality.effectTo)) {
                            if (lastEffect == null || effectNode == null || !lastEffect.equalEffect(bitemporality)) {
                                effectNode = objectMapper.createObjectNode();
                                effectsNode.add(effectNode);
                            }
                            effectNode.put("virkningFra", formatTime(bitemporality.effectFrom, true));
                            effectNode.put("virkningTil", formatTime(bitemporality.effectTo, true));

                            ArrayList<PersonBaseData> dataItems = data.get(bitemporality);
                            System.out.println(dataItems);

                            /*HashMap<String, ArrayList<JsonNode>> records = this.bitemporalData.get(bitemporality);
                            for (String key : records.keySet()) {
                                this.setValue(objectMapper, effectNode, key, records.get(key));
                            }*/




                            lastEffect = bitemporality;
                            //}
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


                    /*OffsetDateTime timestamp = personBaseData.getLastUpdated();

                    PersonCoreData personCoreData = personBaseData.getCoreData();
                    PersonStatusData personStatusData = personBaseData.getStatus();
                    PersonPositionData stilling = personBaseData.getPosition();
                    if (personCoreData != null) {
                        if (personCoreData.getCprNumber() != null && !personCoreData.getCprNumber().isEmpty()) {
                            dataPiece.set(PersonCoreData.IO_FIELD_CPR_NUMBER,
                                    createPersonNummerNode(null, timestamp, personCoreData)
                            );
                        }
                        if (personCoreData.getGender() != null || personStatusData != null || stilling != null) {
                            dataPiece.set("person",
                                    createKerneDataNode(null, timestamp, personCoreData, personStatusData, stilling)
                            );
                        }
                    }

                    PersonBirthData foedsel = personBaseData.getBirth();
                    if (foedsel != null) {
                        dataPiece.set("fødselsdata",
                                createFoedselNode(null, timestamp, foedsel)
                        );
                    }

                    PersonChurchData folkekirkeforhold = personBaseData.getChurch();
                    if (folkekirkeforhold != null) {
                        dataPiece.set("folkekirkeoplysning",
                                createFolkekirkeoplysningNode(null, timestamp, folkekirkeforhold)
                        );
                    }

                    PersonParentData far = personBaseData.getFather();
                    if (far != null) {
                        dataPiece.set("foraeldreoplysning",
                                createForaeldreoplysningNode(null, timestamp, "FAR_MEDMOR", far)
                        );
                    }

                    PersonParentData mor = personBaseData.getMother();
                    if (mor != null) {
                        dataPiece.set("foraeldreoplysning",
                                createForaeldreoplysningNode(null, timestamp, "MOR", mor)
                        );
                    }

                    PersonAddressData adresse = personBaseData.getAddress();
                    PersonAddressConameData conavn = personBaseData.getConame();
                    PersonMoveMunicipalityData flytteKommune = personBaseData.getMoveMunicipality();
                    if (adresse != null || conavn != null || flytteKommune != null) {
                        dataPiece.set("adresseoplysninger",
                                createAdresseOplysningNode(null, timestamp, adresse, conavn, flytteKommune)
                        );
                    }

                    PersonNameData navn = personBaseData.getName();
                    PersonAddressNameData adressenavn = personBaseData.getAddressingName();
                    if (navn != null || adressenavn != null) {
                        dataPiece.set(PersonBaseData.IO_FIELD_NAME,
                                createNavnNode(null, timestamp, navn, adressenavn)
                        );
                    }

                    Collection<PersonProtectionData> beskyttelse = personBaseData.getProtection();
                    if (beskyttelse != null && !beskyttelse.isEmpty()) {
                        dataPiece.set(PersonBaseData.IO_FIELD_PROTECTION,
                                createBeskyttelseNode(null, timestamp, beskyttelse)
                        );
                    }

                    PersonForeignAddressData udenlandsadresse = personBaseData.getForeignAddress();
                    PersonEmigrationData udrejseIndrejse = personBaseData.getMigration();
                    if (udrejseIndrejse != null) {
                        dataPiece.set("udrejseindrejse",
                                createUdrejseIndrejseNode(null, timestamp, udrejseIndrejse, udenlandsadresse)
                        );
                    }

                    PersonNameAuthorityTextData navnemyndighed = personBaseData.getNameAuthority();
                    if (navnemyndighed != null) {
                        dataPiece.set(PersonBaseData.IO_FIELD_NAME_AUTHORITY,
                                createNavneMyndighedNode(null, timestamp, navnemyndighed)
                        );
                    }
                }
                if (dataPiece.size() > 0) {
                    System.out.println(bitemporality + " => " + dataPiece.toString());
                    data.put(bitemporality, dataPiece);
                }*/




/*
            ObjectNode output = objectMapper.createObjectNode();
            output.put(
                    PersonRegistration.IO_FIELD_REGISTRATION_FROM,
                    registration.getRegistrationFrom() != null ? registration.getRegistrationFrom().toString() : null
            );
            output.put(
                    PersonRegistration.IO_FIELD_REGISTRATION_TO,
                    registration.getRegistrationTo() != null ? registration.getRegistrationTo().toString() : null
            );

            for (PersonEffect virkning : registration.getEffects()) {
                for (PersonBaseData personBaseData : virkning.getDataItems()) {
                    PersonCoreData personCoreData = personBaseData.getCoreData();
                    PersonStatusData personStatusData = personBaseData.getStatus();
                    PersonParentData far = personBaseData.getFather();
                    PersonParentData mor = personBaseData.getMother();
                    PersonPositionData stilling = personBaseData.getPosition();
                    PersonBirthData foedsel = personBaseData.getBirth();
                    PersonChurchData folkekirkeforhold = personBaseData.getChurch();
                    PersonAddressData adresse = personBaseData.getAddress();
                    PersonAddressConameData conavn = personBaseData.getConame();
                    PersonMoveMunicipalityData flytteKommune = personBaseData.getMoveMunicipality();
                    PersonNameData navn = personBaseData.getName();
                    PersonAddressNameData adressenavn = personBaseData.getAddressingName();
                    Collection<PersonProtectionData> beskyttelse = personBaseData.getProtection();
                    PersonEmigrationData udrejseIndrejse = personBaseData.getMigration();
                    PersonForeignAddressData udenlandsadresse = personBaseData.getForeignAddress();
                    PersonNameAuthorityTextData navnemyndighed = personBaseData.getNameAuthority();

                    OffsetDateTime timestamp = personBaseData.getLastUpdated();

                    if (personCoreData != null) {
                        if (personCoreData.getCprNumber() != null && !personCoreData.getCprNumber().isEmpty()) {
                            addEffectDataToRegistration(
                                    output, PersonCoreData.IO_FIELD_CPR_NUMBER,
                                    createPersonNummerNode(virkning, timestamp, personCoreData)
                            );
                        }
                        if (personCoreData.getGender() != null || personStatusData != null || stilling != null) {
                            addEffectDataToRegistration(
                                    output,
                                    "person",
                                    createKerneDataNode(virkning, timestamp, personCoreData, personStatusData, stilling)
                            );
                        }
                    }
                    if (foedsel != null) {
                        addEffectDataToRegistration(
                                output,
                                "fødselsdata",
                                createFoedselNode(virkning, timestamp, foedsel)
                        );
                    }
                    if (folkekirkeforhold != null) {
                        addEffectDataToRegistration(
                                output,
                                "folkekirkeoplysning",
                                createFolkekirkeoplysningNode(virkning, timestamp, folkekirkeforhold)
                        );
                    }
                    if (far != null) {
                        addEffectDataToRegistration(
                                output,
                                "foraeldreoplysning",
                                createForaeldreoplysningNode(virkning, timestamp, "FAR_MEDMOR", far)
                        );
                    }
                    if (mor != null) {
                        addEffectDataToRegistration(
                                output,
                                "foraeldreoplysning",
                                createForaeldreoplysningNode(virkning, timestamp, "MOR", mor)
                        );
                    }
                    if (adresse != null || conavn != null || flytteKommune != null) {
                        addEffectDataToRegistration(
                                output,
                                "adresseoplysninger",
                                createAdresseOplysningNode(virkning, timestamp, adresse, conavn, flytteKommune)
                        );
                    }
                    if (navn != null || adressenavn != null) {
                        addEffectDataToRegistration(
                                output,
                                PersonBaseData.IO_FIELD_NAME,
                                createNavnNode(virkning, timestamp, navn, adressenavn)
                        );
                    }
                    if (beskyttelse != null && !beskyttelse.isEmpty()) {
                        addEffectDataToRegistration(
                                output,
                                PersonBaseData.IO_FIELD_PROTECTION,
                                createBeskyttelseNode(virkning, timestamp, beskyttelse)
                        );
                    }
                    if (udrejseIndrejse != null) {
                        addEffectDataToRegistration(
                                output,
                                "udrejseindrejse",
                                createUdrejseIndrejseNode(virkning, timestamp, udrejseIndrejse, udenlandsadresse)
                        );
                    }
                    if (navnemyndighed != null) {
                        addEffectDataToRegistration(
                                output,
                                PersonBaseData.IO_FIELD_NAME_AUTHORITY,
                                createNavneMyndighedNode(virkning, timestamp, navnemyndighed)
                        );
                    }
                }
            }
            registreringer.add(output);
        }
        
        return root;
    }*/


    protected void addEffectDataToRegistration(ObjectNode output, String key, JsonNode value) {
        if (!output.has(key) || output.get(key).isNull()) {
            output.set(key, objectMapper.createArrayNode());
        }
        ((ArrayNode) output.get(key)).add(value);
    }

    protected ObjectNode createDataNode(Effect virkning, OffsetDateTime lastUpdated) {
        return createDataNode(virkning, true, lastUpdated);
    }

    protected ObjectNode createDataNode(Effect virkning, boolean includeVirkningTil, OffsetDateTime lastUpdated) {
        ObjectNode output = objectMapper.createObjectNode();
        if (virkning != null) {
            output.put(
                    PersonEffect.IO_FIELD_EFFECT_FROM,
                    virkning.getEffectFrom() != null ? virkning.getEffectFrom().toString() : null
            );
            if (includeVirkningTil) {
                output.put(
                        PersonEffect.IO_FIELD_EFFECT_TO,
                        virkning.getEffectTo() != null ? virkning.getEffectTo().toString() : null
                );
            }
        }
        output.put(
                PersonBaseData.IO_FIELD_LAST_UPDATED,
                lastUpdated != null ? lastUpdated.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null
        );
        return output;
    }

    protected ObjectNode createPersonNummerNode(Effect virkning, OffsetDateTime lastUpdated, PersonCoreData personCoreData) {
        ObjectNode personnummer = createDataNode(virkning, lastUpdated);
        personnummer.put(PersonCoreData.IO_FIELD_CPR_NUMBER, personCoreData.getCprNumber());
        // TODO: Personnummer status enum?
        return personnummer;
    }

    protected ObjectNode createKerneDataNode(
                    Effect virkning, OffsetDateTime lastUpdated, PersonCoreData personCoreData, PersonStatusData personStatusData,
                    PersonPositionData stilling
    ) {
        ObjectNode output = createDataNode(virkning, lastUpdated);
        if (personCoreData.getGender() != null) {
            output.put(
                    PersonCoreData.IO_FIELD_GENDER,
                    personCoreData.getGender().toString()
            );
        }
        if (personStatusData != null) {
            output.put(
                    PersonStatusData.IO_FIELD_STATUS,
                    personStatusData.getStatus()
            );
        }
        if (stilling != null) {
            output.put(
                    PersonPositionData.IO_FIELD_POSITION,
                    stilling.getPosition()
            );
        }
        return output;
    }

    protected ObjectNode createFoedselNode(
            Effect virkning, OffsetDateTime lastUpdated, PersonBirthData personBirthData
    ) {
        ObjectNode output = createDataNode(virkning, true, lastUpdated);
        if (personBirthData.getBirthDatetime() != null) {
            output.put(
                    PersonBirthData.IO_FIELD_BIRTH_DATETIME,
                    personBirthData.getBirthDatetime().toLocalDate().toString()
            );
            output.put(
                    PersonBirthData.IO_FIELD_BIRTH_DATETIME_UNCERTAIN,
                    personBirthData.isBirthDatetimeUncertain()
            );
        }
        if (personBirthData.getBirthPlaceCode() != null) {
            output.put(
                    PersonBirthData.IO_FIELD_BIRTH_PLACE_CODE,
                    personBirthData.getBirthPlaceCode()
            );
        }
        if (personBirthData.getBirthAuthorityText() != null) {
            output.put(
                    PersonBirthData.IO_FIELD_BIRTH_AUTHORITY_TEXT,
                    personBirthData.getBirthAuthorityText()
            );
        }
        if (personBirthData.getBirthPlaceName() != null) {
            output.put(
                    PersonBirthData.IO_FIELD_BIRTH_PLACE_NAME,
                    personBirthData.getBirthPlaceName()
            );
        }
        if (personBirthData.getBirthSupplementalText() != null) {
            output.put(
                    PersonBirthData.IO_FIELD_BIRTH_SUPPLEMENTAL_TEXT,
                    personBirthData.getBirthSupplementalText()
            );
        }
        return output;
    }

    protected ObjectNode createFolkekirkeoplysningNode(
            Effect virkning, OffsetDateTime lastUpdated, PersonChurchData personChurchData
    ) {
        ObjectNode output = createDataNode(virkning, true, lastUpdated);
        Character relation = personChurchData.getChurchRelation();
        output.put(PersonChurchData.IO_FIELD_CHURCH_RELATION, relation != null ? relation.toString() : null);
        return output;
    }

    protected ObjectNode createForaeldreoplysningNode(
                    Effect virkning, OffsetDateTime lastUpdated, String foraelderrolle, PersonParentData personParentData
    ) {
        ObjectNode output = createDataNode(virkning, false, lastUpdated);
        output.put(PersonParentData.IO_FIELD_CPR_NUMBER, personParentData.getCprNumber());
        output.put("foraelderrolle", foraelderrolle);
        return output;
    }

    protected ObjectNode createAdresseOplysningNode(
                    Effect virkning, OffsetDateTime lastUpdated, PersonAddressData adresse, PersonAddressConameData conavn,
                    PersonMoveMunicipalityData flytteKommune
    ) {
        ObjectNode output = createDataNode(virkning, lastUpdated);
        output.put(
                PersonAddressConameData.IO_FIELD_CONAME,
                conavn != null ? conavn.getConame() : null
        );
        if (flytteKommune != null) {
            output.put(
                PersonMoveMunicipalityData.IO_FIELD_OUT_DATETIME,
                flytteKommune.getOutDatetime() != null ?
                    flytteKommune.getOutDatetime().toLocalDate().toString() :
                    null
            );
            output.put(
                    PersonMoveMunicipalityData.IO_FIELD_OUT_MUNICIPALITY,
                    flytteKommune.getOutMunicipality()
            );
            output.put(
                PersonMoveMunicipalityData.IO_FIELD_IN_DATETIME,
                flytteKommune.getInDatetime() != null ?
                    flytteKommune.getInDatetime().toLocalDate().toString() :
                    null
            );
        }
        output.set(PersonBaseData.IO_FIELD_ADDRESS, createCprAdresseNode(adresse));
        return output;
    }

    protected ObjectNode createCprAdresseNode(PersonAddressData adresse) {
        if (adresse != null) {
            ObjectNode output = objectMapper.createObjectNode();
            output.put(PersonAddressData.IO_FIELD_BUILDING_NUMBER, adresse.getBuildingNumber());
            output.put(PersonAddressData.IO_FIELD_CITY_NAME, adresse.getCityName());
            output.put(PersonAddressData.IO_FIELD_MUNICIPALITY_CODE, adresse.getMunicipalityCode());
            output.put(PersonAddressData.IO_FIELD_MUNICIPALITY_NAME, adresse.getMunicipalityName());
            output.put(PersonAddressData.IO_FIELD_ROAD_CODE, adresse.getRoadCode());
            output.put(PersonAddressData.IO_FIELD_FLOOR, adresse.getFloor());
            output.put(PersonAddressData.IO_FIELD_HOUSENUMBER, adresse.getHouseNumber());
            output.put(PersonAddressData.IO_FIELD_POSTAL_DISTRICT, adresse.getPostalDistrict());
            output.put(PersonAddressData.IO_FIELD_POSTAL_CODE, adresse.getPostalCode());
            output.put(PersonAddressData.IO_FIELD_DOOR, adresse.getDoor());
            output.put(PersonAddressData.IO_FIELD_ROAD_ADDRESS_NAME, adresse.getRoadAddressName());
            return output;
        }
        return null;
    }

    protected ObjectNode createNavnNode(
                    Effect virkning, OffsetDateTime lastUpdated, PersonNameData navn, PersonAddressNameData addresseringsnavn
    ) {
        ObjectNode output = createDataNode(virkning, lastUpdated);
        output.put(
                PersonBaseData.IO_FIELD_ADDRESSING_NAME,
                addresseringsnavn != null ? addresseringsnavn.getAddressName() : null
        );
        if (navn != null) {
            output.put(
                PersonNameData.IO_FIELD_LAST_NAME,
                navn.getLastName()
            );
            output.put(
                PersonNameData.IO_FIELD_FIRST_NAMES,
                navn.getFirstNames()
            );
            output.put(
                PersonNameData.IO_FIELD_MIDDLE_NAME,
                navn.getMiddleName()
            );
        } else {
            output.putNull(PersonNameData.IO_FIELD_LAST_NAME);
            output.putNull(PersonNameData.IO_FIELD_FIRST_NAMES);
            output.putNull(PersonNameData.IO_FIELD_MIDDLE_NAME);
        }

        return output;
    }

    protected ArrayNode createBeskyttelseNode(Effect virkning, OffsetDateTime lastUpdated, Collection<PersonProtectionData> beskyttelse) {
        ArrayNode output = objectMapper.createArrayNode();
        for (PersonProtectionData personProtectionData : beskyttelse) {
            ObjectNode item = createDataNode(virkning, lastUpdated);
            item.put(PersonProtectionData.IO_FIELD_TYPE, personProtectionData.getProtectionType());
            output.add(item);
        }
        return output;
    }

    protected ObjectNode createUdrejseIndrejseNode(
                    Effect virkning, OffsetDateTime lastUpdated, PersonEmigrationData udrejseIndrejse,
                    PersonForeignAddressData udenlandsadresse
    ) {
        ObjectNode output = createDataNode(virkning, lastUpdated);
        output.put(PersonEmigrationData.IO_FIELD_COUNTRY_CODE, udrejseIndrejse.getCountryCode());
        if (udenlandsadresse != null) {
            output.set("simpeladresse", createUdrejseAdresseNode(udenlandsadresse));
        }
        return output;
    }

    protected ObjectNode createUdrejseAdresseNode(PersonForeignAddressData udenlandsadresse) {
        ObjectNode output = objectMapper.createObjectNode();
        output.put(PersonForeignAddressData.IO_FIELD_ADDRESS_LINE1, udenlandsadresse.getAddressLine1());
        output.put(PersonForeignAddressData.IO_FIELD_ADDRESS_LINE2, udenlandsadresse.getAddressLine2());
        output.put(PersonForeignAddressData.IO_FIELD_ADDRESS_LINE3, udenlandsadresse.getAddressLine3());
        output.put(PersonForeignAddressData.IO_FIELD_ADDRESS_LINE4, udenlandsadresse.getAddressLine4());
        output.put(PersonForeignAddressData.IO_FIELD_ADDRESS_LINE5, udenlandsadresse.getAddressLine5());
        return output;
    }

    protected ObjectNode createNavneMyndighedNode(Effect virkning, OffsetDateTime lastUpdated, PersonNameAuthorityTextData navnemyndighed) {
        ObjectNode output = createDataNode(virkning, lastUpdated);
        output.put(PersonNameAuthorityTextData.IO_FIELD_AUTHORITY, navnemyndighed.getText());
        return output;
    }
}

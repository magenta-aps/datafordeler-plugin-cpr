package dk.magenta.datafordeler.cpr.data.person;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.magenta.datafordeler.core.database.Effect;
import dk.magenta.datafordeler.core.fapi.OutputWrapper;
import dk.magenta.datafordeler.cpr.data.person.data.*;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class PersonOutputWrapper extends OutputWrapper<PersonEntity> {

    private ObjectMapper objectMapper;

    @Override
    public Object wrapResult(PersonEntity input) {
        objectMapper = new ObjectMapper();

        // Root
        ObjectNode root = objectMapper.createObjectNode();


        root.put(PersonEntity.IO_FIELD_UUID, input.getUUID().toString());
        root.put(PersonEntity.IO_FIELD_CPR_NUMBER, input.getPersonnummer());
        root.putPOJO("id", input.getIdentification());

        ArrayNode registreringer = objectMapper.createArrayNode();
        root.set(PersonEntity.IO_FIELD_REGISTRATIONS, registreringer);

        for (PersonRegistration personRegistration : input.getRegistrations()) {
            registreringer.add(wrapRegistrering(personRegistration));
        }

        /*
        try {
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        */
        return root;
    }

    protected ObjectNode wrapRegistrering(PersonRegistration input) {
        ObjectNode output = objectMapper.createObjectNode();
        output.put(
            PersonRegistration.IO_FIELD_REGISTRATION_FROM,
            input.getRegistrationFrom() != null ? input.getRegistrationFrom().toString() : null
        );
        output.put(
            PersonRegistration.IO_FIELD_REGISTRATION_TO,
            input.getRegistrationTo() != null ? input.getRegistrationTo().toString() : null
        );

        for (PersonEffect virkning : input.getEffects()) {
            for (PersonBaseData personBaseData : virkning.getDataItems()) {
                PersonCoreData personCoreData = personBaseData.getCoreData();
                PersonStatusData personStatusData = personBaseData.getStatus();
                PersonParentData far = personBaseData.getFather();
                PersonParentData mor = personBaseData.getMother();
                PersonPositionData stilling = personBaseData.getPosition();
                PersonBirthData foedsel = personBaseData.getBirth();
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
                    addEffectDataToRegistration(
                            output, PersonCoreData.IO_FIELD_CPR_NUMBER,
                            createPersonNummerNode(virkning, timestamp, personCoreData)
                    );
                    addEffectDataToRegistration(
                            output,
                            "person",
                            createKerneDataNode(virkning, timestamp, personCoreData, personStatusData, stilling, foedsel)
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

        return output;
    }


    protected void addEffectDataToRegistration(ObjectNode output, String key, JsonNode value) {
        if (!output.has(key) || output.get(key).isNull()) {
            output.set(key, objectMapper.createArrayNode());
        }
        ((ArrayNode) output.get(key)).add(value);
    }

    protected ObjectNode createVirkningObjectNode(Effect virkning, OffsetDateTime lastUpdated) {
        return createVirkningObjectNode(virkning, true, lastUpdated);
    }

    protected ObjectNode createVirkningObjectNode(Effect virkning, boolean includeVirkningTil, OffsetDateTime lastUpdated) {
        ObjectNode output = objectMapper.createObjectNode();
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
        output.put(
                PersonBaseData.IO_FIELD_LAST_UPDATED,
                lastUpdated != null ? lastUpdated.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null
        );
        return output;
    }

    protected ObjectNode createPersonNummerNode(Effect virkning, OffsetDateTime lastUpdated, PersonCoreData personCoreData) {
        ObjectNode personnummer = createVirkningObjectNode(virkning, lastUpdated);
        personnummer.put(PersonCoreData.IO_FIELD_CPR_NUMBER, personCoreData.getCprNumber());
        // TODO: Personnummer status enum?
        return personnummer;
    }

    protected ObjectNode createKerneDataNode(
                    Effect virkning, OffsetDateTime lastUpdated, PersonCoreData personCoreData, PersonStatusData personStatusData,
                    PersonPositionData stilling, PersonBirthData foedsel
    ) {
        ObjectNode output = createVirkningObjectNode(virkning, lastUpdated);
        output.put(
                        PersonCoreData.IO_FIELD_GENDER,
                        personCoreData.getGender() != null ? personCoreData.getGender().toString() : null
        );
        output.put(
                        PersonStatusData.IO_FIELD_STATUS,
                        personStatusData != null ? personStatusData.getStatus() : null
        );
        output.put(
                        PersonPositionData.IO_FIELD_POSITION,
                        stilling != null ? stilling.getPosition() : null
        );
        if (foedsel != null) {
            if (foedsel.getBirthDatetime() != null) {
                output.put(
                        PersonBirthData.IO_FIELD_BIRTH_DATETIME,
                        foedsel.getBirthDatetime().toLocalDate().toString()
                );
            }
            output.put(
                    PersonBirthData.IO_FIELD_BIRTH_DATETIME_UNCERTAIN,
                    foedsel.isBirthDatetimeUncertain()
            );
            if (foedsel.getBirthPlaceCode() != null) {
                output.put(
                        PersonBirthData.IO_FIELD_BIRTH_PLACE_CODE,
                        foedsel.getBirthPlaceCode()
                );
            }
            if (foedsel.getBirthPlaceName() != null) {
                output.put(
                        PersonBirthData.IO_FIELD_BIRTH_PLACE_NAME,
                        foedsel.getBirthPlaceName()
                );
            }
        }
        return output;
    }

    protected ObjectNode createForaeldreoplysningNode(
                    Effect virkning, OffsetDateTime lastUpdated, String foraelderrolle, PersonParentData personParentData
    ) {
        ObjectNode output = createVirkningObjectNode(virkning, false, lastUpdated);
        output.put(PersonParentData.IO_FIELD_CPR_NUMBER, personParentData.getCprNumber());
        output.put("foraelderrolle", foraelderrolle);
        return output;
    }

    protected ObjectNode createAdresseOplysningNode(
                    Effect virkning, OffsetDateTime lastUpdated, PersonAddressData adresse, PersonAddressConameData conavn,
                    PersonMoveMunicipalityData flytteKommune
    ) {
        ObjectNode output = createVirkningObjectNode(virkning, lastUpdated);
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
        ObjectNode output = createVirkningObjectNode(virkning, lastUpdated);
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
            ObjectNode item = createVirkningObjectNode(virkning, lastUpdated);
            item.put(PersonProtectionData.IO_FIELD_TYPE, personProtectionData.getProtectionType());
            output.add(item);
        }
        return output;
    }

    protected ObjectNode createUdrejseIndrejseNode(
                    Effect virkning, OffsetDateTime lastUpdated, PersonEmigrationData udrejseIndrejse,
                    PersonForeignAddressData udenlandsadresse
    ) {
        ObjectNode output = createVirkningObjectNode(virkning, lastUpdated);
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
        ObjectNode output = createVirkningObjectNode(virkning, lastUpdated);
        output.put(PersonNameAuthorityTextData.IO_FIELD_AUTHORITY, navnemyndighed.getText());
        return output;
    }
}

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


        root.put("UUID", input.getUUID().toString());
        root.put("personnummer", input.getPersonnummer());
        root.putPOJO("id", input.getIdentification());

        ArrayNode registreringer = objectMapper.createArrayNode();
        root.set("registreringer", registreringer);

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
            "registreringFra",
            input.getRegistrationFrom() != null ? input.getRegistrationFrom().toString() : null
        );
        output.put(
            "registreringTil",
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

                OffsetDateTime timestamp = personBaseData.getLatestUpdated();

                if (personCoreData != null) {
                    addEffectDataToRegistration(
                            output, "personnummer",
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
                            "navn",
                            createNavnNode(virkning, timestamp, navn, adressenavn)
                    );
                }
                if (beskyttelse != null) {
                    addEffectDataToRegistration(
                            output,
                            "beskyttelse",
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
                            "navnemyndighed",
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
            "virkningFra",
            virkning.getEffectFrom() != null ? virkning.getEffectFrom().toString() : null
        );
        if (includeVirkningTil) {
            output.put(
                "virkningTil",
                virkning.getEffectTo() != null ? virkning.getEffectTo().toString() : null
            );
        }
        output.put("lastUpdated", lastUpdated != null ? lastUpdated.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
        return output;
    }

    protected ObjectNode createPersonNummerNode(Effect virkning, OffsetDateTime lastUpdated, PersonCoreData personCoreData) {
        ObjectNode personnummer = createVirkningObjectNode(virkning, lastUpdated);
        personnummer.put("personnummer", personCoreData.getCprNumber());
        // TODO: Personnummer status enum?
        return personnummer;
    }

    protected ObjectNode createKerneDataNode(
                    Effect virkning, OffsetDateTime lastUpdated, PersonCoreData personCoreData, PersonStatusData personStatusData,
                    PersonPositionData stilling, PersonBirthData foedsel
    ) {
        ObjectNode output = createVirkningObjectNode(virkning, lastUpdated);
        output.put(
                        "koen",
                        personCoreData.getGender() != null ? personCoreData.getGender().toString() : null
        );
        output.put(
                        "personstatus",
                        personStatusData != null ? personStatusData.getStatus() : null
        );
        output.put(
                        "stilling",
                        stilling != null ? stilling.getPosition() : null
        );
        if (foedsel != null) {
            output.put("foedselsdato", foedsel.getBirthDatetime().toLocalDate().toString());
            output.put(
                            "cprFoedselsregistreringsstedskode",
                            foedsel.getBirthPlaceCode()
            );
            output.put(
                            "cprFoedselsregistreringsstedsnavn",
                            foedsel.getBirthPlaceName()
            );
            output.put(
                            "foedselsdatoUsikkerhedsmarkering",
                            foedsel.isBirthDatetimeUncertain()
            );
        }
        return output;
    }

    protected ObjectNode createForaeldreoplysningNode(
                    Effect virkning, OffsetDateTime lastUpdated, String foraelderrolle, PersonParentData personParentData
    ) {
        ObjectNode output = createVirkningObjectNode(virkning, false, lastUpdated);
        output.put("personnummer", personParentData.getCprNumber());
        output.put("foraelderrolle", foraelderrolle);
        return output;
    }

    protected ObjectNode createAdresseOplysningNode(
                    Effect virkning, OffsetDateTime lastUpdated, PersonAddressData adresse, PersonAddressConameData conavn,
                    PersonMoveMunicipalityData flytteKommune
    ) {
        ObjectNode output = createVirkningObjectNode(virkning, lastUpdated);
        output.put("conavn", conavn != null ? conavn.getConame() : null);
        if (flytteKommune != null) {
            output.put(
                "fraflytningsdatoKommune",
                flytteKommune.getOutDatetime() != null ?
                    flytteKommune.getOutDatetime().toLocalDate().toString() :
                    null
            );
            output.put("fraflytningsKommunekode", flytteKommune.getOutMunicipality());
            output.put(
                "tilflytningsdatoKommune",
                flytteKommune.getInDatetime() != null ?
                    flytteKommune.getInDatetime().toLocalDate().toString() :
                    null
            );
        }
        if (adresse != null) {
            output.set("cpradresse", createCprAdresseNode(adresse));
        } else {
            output.putNull("cpradresse");
        }
        return output;
    }

    protected ObjectNode createCprAdresseNode(PersonAddressData adresse) {
        ObjectNode output = objectMapper.createObjectNode();
        output.put("bygningsnummer", adresse.getBuildingNumber());
        output.put("bynavn", adresse.getCityName());
        output.put("cprKommunekode", adresse.getMunicipalityCode());
        output.put("cprKommunenavn", adresse.getMunicipalityName());
        output.put("cprVejkode", adresse.getRoadCode());
        output.put("etage", adresse.getFloor());
        output.put("husnummer", adresse.getHouseNumber());
        output.put("postdistrikt", adresse.getPostalDistrict());
        output.put("postnummer", adresse.getPostalCode());
        output.put("sideDoer", adresse.getDoor());
        output.put("vejaddresseringsnavn", adresse.getRoadAddressName());
        return output;
    }

    protected ObjectNode createNavnNode(
                    Effect virkning, OffsetDateTime lastUpdated, PersonNameData navn, PersonAddressNameData addresseringsnavn
    ) {
        ObjectNode output = createVirkningObjectNode(virkning, lastUpdated);
        output.put(
                        "addresseringsnavn",
                        addresseringsnavn != null ? addresseringsnavn.getAddressName() : null
        );
        if (navn != null) {
            output.put(
                "efternavn",
                navn.getLastName()
            );
            output.put(
                "fornavne",
                navn.getFirstNames()
            );
            output.put(
                "mellemnavn",
                navn.getMiddleName()
            );
        } else {
            output.putNull("efternavn");
            output.putNull("fornavne");
            output.putNull("mellemnavn");
        }

        return output;
    }

    protected ArrayNode createBeskyttelseNode(Effect virkning, OffsetDateTime lastUpdated, Collection<PersonProtectionData> beskyttelse) {
        ArrayNode output = objectMapper.createArrayNode();
        for (PersonProtectionData personProtectionData : beskyttelse) {
            ObjectNode item = createVirkningObjectNode(virkning, lastUpdated);
            item.put("beskyttelsestype", personProtectionData.getProtectionType());
            output.add(item);
        }
        return output;
    }

    protected ObjectNode createUdrejseIndrejseNode(
                    Effect virkning, OffsetDateTime lastUpdated, PersonEmigrationData udrejseIndrejse,
                    PersonForeignAddressData udenlandsadresse
    ) {
        ObjectNode output = createVirkningObjectNode(virkning, lastUpdated);
        output.put("cprLandekodeUdrejse", udrejseIndrejse.getCountryCode());
        if (udenlandsadresse != null) {
            output.set("simpeladresse", createUdrejseAdresseNode(udenlandsadresse));
        }
        return output;
    }

    protected ObjectNode createUdrejseAdresseNode(PersonForeignAddressData udenlandsadresse) {
        ObjectNode output = objectMapper.createObjectNode();
        output.put("adresselinie1", udenlandsadresse.getAddressLine1());
        output.put("adresselinie2", udenlandsadresse.getAddressLine2());
        output.put("adresselinie3", udenlandsadresse.getAddressLine3());
        output.put("adresselinie4", udenlandsadresse.getAddressLine4());
        output.put("adresselinie5", udenlandsadresse.getAddressLine5());
        return output;
    }

    protected ObjectNode createNavneMyndighedNode(Effect virkning, OffsetDateTime lastUpdated, PersonNameAuthorityTextData navnemyndighed) {
        ObjectNode output = createVirkningObjectNode(virkning, lastUpdated);
        output.put("myndighed", navnemyndighed.getText());
        return output;
    }
}

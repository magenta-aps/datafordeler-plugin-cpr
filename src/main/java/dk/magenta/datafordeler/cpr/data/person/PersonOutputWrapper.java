package dk.magenta.datafordeler.cpr.data.person;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.magenta.datafordeler.core.database.Effect;
import dk.magenta.datafordeler.core.fapi.OutputWrapper;
import dk.magenta.datafordeler.cpr.data.person.data.PersonAddressConameData;
import dk.magenta.datafordeler.cpr.data.person.data.PersonAddressData;
import dk.magenta.datafordeler.cpr.data.person.data.PersonAddressNameData;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBirthData;
import dk.magenta.datafordeler.cpr.data.person.data.PersonCoreData;
import dk.magenta.datafordeler.cpr.data.person.data.PersonEmigrationData;
import dk.magenta.datafordeler.cpr.data.person.data.PersonForeignAddressData;
import dk.magenta.datafordeler.cpr.data.person.data.PersonMoveMunicipalityData;
import dk.magenta.datafordeler.cpr.data.person.data.PersonNameData;
import dk.magenta.datafordeler.cpr.data.person.data.PersonParentData;
import dk.magenta.datafordeler.cpr.data.person.data.PersonPositionData;
import dk.magenta.datafordeler.cpr.data.person.data.PersonProtectionData;
import dk.magenta.datafordeler.cpr.data.person.data.PersonStatusData;

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
    root.set("regisreringer", registreringer);

    for(PersonRegistration personRegistration : input.getRegistrations()) {
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
        PersonCoreData personCoreData = personBaseData.getKerneData();
        PersonStatusData personStatusData = personBaseData.getStatus();
        PersonParentData far = personBaseData.getFar();
        PersonParentData mor = personBaseData.getMor();
        PersonPositionData stilling = personBaseData.getStilling();
        PersonBirthData foedsel = personBaseData.getFoedsel();
        PersonAddressData adresse = personBaseData.getAdresse();
        PersonAddressConameData conavn = personBaseData.getConavn();
        PersonMoveMunicipalityData flytteKommune = personBaseData.getFlytKommune();
        PersonNameData navn = personBaseData.getNavn();
        PersonAddressNameData adressenavn = personBaseData.getAdressenavn();
        PersonProtectionData beskyttelse = personBaseData.getBeskyttelse();
        PersonEmigrationData udrejseIndrejse = personBaseData.getUdrejseIndrejse();
        PersonForeignAddressData udenlandsadresse =personBaseData.getUdenlandsadresse();
        if(personCoreData != null) {
          addEffectDataToRegistration(
              output, "personnummer", createPersonNummerNode(virkning, personCoreData)
          );
          addEffectDataToRegistration(
              output,
              "person",
              createKerneDataNode(virkning, personCoreData, personStatusData, stilling, foedsel)
          );
        }
        if(far != null) {
          addEffectDataToRegistration(
              output,
              "foraeldreoplysning",
              createForaeldreoplysningNode(virkning, "FAR_MEDMOR", far)
          );
        }
        if(mor != null) {
          addEffectDataToRegistration(
              output,
              "foraeldreoplysning",
              createForaeldreoplysningNode(virkning, "MOR", mor)
          );
        }
        if(adresse != null || conavn != null || flytteKommune != null) {
          addEffectDataToRegistration(
              output,
              "adresseoplysninger",
              createAdresseOplysningNode(virkning, adresse, conavn, flytteKommune)
          );
        }
        if(navn != null || adressenavn != null) {
          addEffectDataToRegistration(
              output,
              "navn",
              createNavnNode(virkning, navn, adressenavn)
          );
        }
        if(beskyttelse != null) {
          addEffectDataToRegistration(
              output,
              "beskyttelse",
              createBeskyttelseNode(virkning, beskyttelse)
          );
        }
        if(udrejseIndrejse != null) {
          addEffectDataToRegistration(
              output,
              "udrejseindrejse",
              createUdrejseIndrejseNode(virkning, udrejseIndrejse, udenlandsadresse)
          );

        }
      }
    }

    return output;
  }

  protected void addEffectDataToRegistration(ObjectNode output, String key, JsonNode value) {
    if(!output.has(key) || output.get(key).isNull()) {
      output.set(key, objectMapper.createArrayNode());
    }
    ((ArrayNode)output.get(key)).add(value);
  }


  protected ObjectNode createVirkningObjectNode(Effect virkning) {
    return createVirkningObjectNode(virkning, true);
  }

  protected ObjectNode createVirkningObjectNode(Effect virkning, boolean includeVirkningTil) {
    ObjectNode output = objectMapper.createObjectNode();
    output.put(
        "virkningFra",
        virkning.getEffectFrom() != null ? virkning.getEffectFrom().toString() : null
    );
    if(includeVirkningTil) {
      output.put(
          "virkningTil",
          virkning.getEffectTo() != null ? virkning.getEffectTo().toString() : null
      );
    }
    return output;
  }

  protected ObjectNode createPersonNummerNode(Effect virkning, PersonCoreData personCoreData) {
    ObjectNode personnummer = createVirkningObjectNode(virkning);
    personnummer.put("personnummer", personCoreData.getPersonnummer());
    // TODO: Personnummer status enum?
    return personnummer;
  }

  protected ObjectNode createKerneDataNode(
      Effect virkning, PersonCoreData personCoreData, PersonStatusData personStatusData,
      PersonPositionData stilling, PersonBirthData foedsel
  ) {
    ObjectNode output = createVirkningObjectNode(virkning);
    output.put(
        "koen",
        personCoreData.getKoen() != null ? personCoreData.getKoen().toString() : null
    );
    output.put(
        "personstatus",
        personStatusData != null ? personStatusData.getStatus() : null
    );
    output.put(
        "stilling",
        stilling != null ? stilling.getStilling() : null
    );
    if(foedsel != null) {
      output.put("foedselsdato", foedsel.getFoedselsdato().toLocalDate().toString());
      output.put(
          "cprFoedselsregistreringsstedskode",
          foedsel.getCprFoedselsregistreringsstedskode()
      );
      output.put(
          "cprFoedselsregistreringsstedsnavn",
          foedsel.getCprFoedselsregistreringsstedsnavn()
      );
      output.put(
          "foedselsdatoUsikkerhedsmarkering",
          foedsel.isFoedselsdatoUsikkerhedsmarkering()
      );
    }
    return output;
  }

  protected ObjectNode createForaeldreoplysningNode(
      Effect virkning, String foraelderrolle, PersonParentData personParentData
  ) {
    ObjectNode output = createVirkningObjectNode(virkning, false);
    output.put("personnummer", personParentData.getCprNumber());
    output.put("foraelderrolle", foraelderrolle);
    return output;
  }

  protected ObjectNode createAdresseOplysningNode(
      Effect virkning, PersonAddressData adresse, PersonAddressConameData conavn,
      PersonMoveMunicipalityData flytteKommune
  ) {
    ObjectNode output = createVirkningObjectNode(virkning);
    output.put("conavn", conavn != null ? conavn.getConavn() : null);
    if(flytteKommune != null) {
      output.put(
          "fraflytningsdatoKommune",
          flytteKommune.getFraflytningsdatoKommune() != null ?
              flytteKommune.getFraflytningsdatoKommune().toLocalDate().toString() :
              null
      );
      output.put("fraflytningsKommunekode", flytteKommune.getFraflytningskommunekode());
      output.put(
          "tilflytningsdatoKommune",
          flytteKommune.getTilflytningsdatoKommune() != null ?
              flytteKommune.getTilflytningsdatoKommune().toLocalDate().toString() :
              null
      );
    }
    if(adresse != null) {
      output.set("cpradresse", createCprAdresseNode(adresse));
    } else {
      output.putNull("cpradresse");
    }
    return output;
  }

  protected ObjectNode createCprAdresseNode(PersonAddressData adresse) {
    ObjectNode output = objectMapper.createObjectNode();
    output.put("bygningsnummer", adresse.getBygningsnummer());
    output.put("bynavn", adresse.getBynavn());
    output.put("cprKommunekode", adresse.getCprKommunekode());
    output.put("cprKommunenavn", adresse.getCprKommunenavn());
    output.put("cprVejkode", adresse.getCprVejkode());
    output.put("etage", adresse.getEtage());
    output.put("husnummer", adresse.getHusnummer());
    output.put("postdistrikt", adresse.getPostdistrikt());
    output.put("postnummer", adresse.getPostnummer());
    output.put("sideDoer", adresse.getSideDoer());
    output.put("vejaddresseringsnavn", adresse.getVejadresseringsnavn());
    return output;
  }

  protected ObjectNode createNavnNode(
      Effect virkning, PersonNameData navn, PersonAddressNameData addresseringsnavn
  ) {
    ObjectNode output = createVirkningObjectNode(virkning);
    output.put(
        "addresseringsnavn",
        addresseringsnavn != null ? addresseringsnavn.getAdressenavn() : null
    );
    if(navn != null) {
      output.put(
          "efternavn",
          navn.getEfternavn()
      );
      output.put(
          "fornavne",
          navn.getFornavne()
      );
      output.put(
          "mellemnavn",
          navn.getMellemnavn()
      );
    } else {
      output.putNull("efternavn");
      output.putNull("fornavne");
      output.putNull("mellemnavn");
    }
    return output;
  }

  protected ObjectNode createBeskyttelseNode(Effect virkning, PersonProtectionData beskyttelse) {
    ObjectNode output = createVirkningObjectNode(virkning);
    output.put("beskyttelsestype", beskyttelse.getBeskyttelsestype());
    return output;
  }

  protected ObjectNode createUdrejseIndrejseNode(
      Effect virkning, PersonEmigrationData udrejseIndrejse,
      PersonForeignAddressData udenlandsadresse
  ) {
    ObjectNode output = createVirkningObjectNode(virkning);
    output.put("cprLandekodeUdrejse", udrejseIndrejse.getLandekode());
    if (udenlandsadresse != null) {
      output.set("simpeladresse", createUdrejseAdresseNode(udenlandsadresse));
    }
    return output;
  }

  protected ObjectNode createUdrejseAdresseNode(PersonForeignAddressData udenlandsadresse) {
    ObjectNode output = objectMapper.createObjectNode();
    output.put("adresselinie1", udenlandsadresse.getAdresselinie1());
    output.put("adresselinie2", udenlandsadresse.getAdresselinie2());
    output.put("adresselinie3", udenlandsadresse.getAdresselinie3());
    output.put("adresselinie4", udenlandsadresse.getAdresselinie4());
    output.put("adresselinie5", udenlandsadresse.getAdresselinie5());
    return output;
  }

}

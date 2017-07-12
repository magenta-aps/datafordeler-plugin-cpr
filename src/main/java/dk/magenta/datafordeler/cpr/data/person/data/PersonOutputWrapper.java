package dk.magenta.datafordeler.cpr.data.person.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.magenta.datafordeler.core.database.Effect;
import dk.magenta.datafordeler.core.fapi.OutputWrapper;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.PersonEntity;
import dk.magenta.datafordeler.cpr.data.person.PersonRegistration;

public class PersonOutputWrapper extends OutputWrapper<PersonEntity> {

  private ObjectMapper objectMapper;

  @Override
  public Object wrapResult(PersonEntity input) {
    objectMapper = new ObjectMapper();

    // Root
    ObjectNode root = objectMapper.createObjectNode();


    root.put("UUID", input.getUUID().toString());
    root.put("personnummer", input.getPersonnummer());
    root.putPOJO("id", input.getIdentifikation());

    ArrayNode registreringer = objectMapper.createArrayNode();
    root.set("regisreringer", registreringer);

    for(PersonRegistration personRegistration : input.getRegistreringer()) {
      registreringer.add(wrapRegistrering(personRegistration));
    }

    try {
      System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return root;
  }

  protected ObjectNode wrapRegistrering(PersonRegistration input) {
    ObjectNode output = objectMapper.createObjectNode();
    output.put(
        "registreringFra",
        input.getRegistreringFra() != null ? input.getRegistreringFra().toString() : null
    );
    output.put(
        "registreringTil",
        input.getRegistreringTil() != null ? input.getRegistreringTil().toString() : null
    );

    for(PersonEffect virkning : input.getVirkninger()) {
      for(PersonBaseData personBaseData : virkning.getDataItems()) {
        PersonCoreData personCoreData = personBaseData.getKerneData();
        PersonStatusData personStatusData = personBaseData.getStatus();
        PersonParentData far = personBaseData.getFar();
        PersonParentData mor = personBaseData.getMor();
        if(personCoreData != null) {
          addEffectDataToRegistration(
              output, "personnummer", createPersonNummerNode(virkning, personCoreData)
          );
          addEffectDataToRegistration(
              output,
              "kernedata",
              createKerneDataNode(virkning, personCoreData, personStatusData)
          );
        }
      }
    }

    return output;
  }

  protected void addEffectDataToRegistration(ObjectNode output, String key, JsonNode value) {
    if(!output.has(key)) {
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
        virkning.getVirkningFra() != null ? virkning.getVirkningFra().toString() : null
    );
    if(includeVirkningTil) {
      output.put(
          "virkningTil",
          virkning.getVirkningTil() != null ? virkning.getVirkningTil().toString() : null
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
      Effect virkning, PersonCoreData personCoreData, PersonStatusData personStatusData
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
    return output;
  }

  protected ObjectNode createForaeldreoplysningNode(
      Effect virkning, String foraeldrerolle, PersonParentData personParentData
  ) {
    ObjectNode output = createVirkningObjectNode(virkning, false);
    return output;
  }
}

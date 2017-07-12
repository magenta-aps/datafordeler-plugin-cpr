package dk.magenta.datafordeler.cpr.data.person.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.magenta.datafordeler.core.fapi.OutputWrapper;
import dk.magenta.datafordeler.cpr.data.person.PersonEntity;

public class PersonOutputWrapper extends OutputWrapper<PersonEntity> {

  @Override
  public Object wrapResult(PersonEntity input) {

    ObjectMapper objectMapper = new ObjectMapper();

    // Root
    ObjectNode root = objectMapper.createObjectNode();

    // Registreringer
    ArrayNode registreringer = objectMapper.createArrayNode();




    root.putPOJO("registreringer", registreringer);

    return root;
  }
}

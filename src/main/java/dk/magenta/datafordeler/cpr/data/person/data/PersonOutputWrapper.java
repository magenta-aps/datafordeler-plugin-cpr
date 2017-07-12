package dk.magenta.datafordeler.cpr.data.person.data;

import dk.magenta.datafordeler.core.fapi.OutputWrapper;
import dk.magenta.datafordeler.cpr.data.person.PersonEntity;

public class PersonOutputWrapper extends OutputWrapper<PersonEntity> {

  @Override
  public Object wrapResult(PersonEntity input) {
    return null;
  }
}

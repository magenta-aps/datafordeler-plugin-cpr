package dk.magenta.datafordeler.cpr.data.person;

import dk.magenta.datafordeler.core.fapi.ParameterMap;
import dk.magenta.datafordeler.core.fapi.QueryField;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.data.CprQuery;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 19-05-17.
 */
public class PersonQuery extends CprQuery<PersonEntity> {

    public static final String FIRSTNAME = "firstName";
    public static final String LASTNAME = "lastName";

    @QueryField(type = QueryField.FieldType.STRING)
    private String firstName;

    @QueryField(type = QueryField.FieldType.STRING)
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public Map<String, Object> getSearchParameters() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(FIRSTNAME, this.firstName);
        map.put(LASTNAME, this.lastName);
        return map;
    }

    @Override
    public void setFromParameters(ParameterMap parameters) {
        this.setFirstName(parameters.getFirst(FIRSTNAME));
        this.setLastName(parameters.getFirst(LASTNAME));
    }

    @Override
    public Class<PersonEntity> getEntityClass() {
        return PersonEntity.class;
    }

}

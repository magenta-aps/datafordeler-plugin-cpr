package dk.magenta.datafordeler.cpr.data.person;

import dk.magenta.datafordeler.core.database.LookupDefinition;
import dk.magenta.datafordeler.core.fapi.ParameterMap;
import dk.magenta.datafordeler.core.fapi.QueryField;
import dk.magenta.datafordeler.cpr.data.CprQuery;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 19-05-17.
 */
public class PersonQuery extends CprQuery<PersonEntity> {

    public static final String CPR = "personnummer";
    public static final String FIRSTNAME = "fornavn";
    public static final String LASTNAME = "efternavn";

    @QueryField(type = QueryField.FieldType.STRING, queryName = CPR)
    private String cprNumber;

    public String getCprNumber() {
        return this.cprNumber;
    }

    public void setCprNumber(String cprNumber) {
        this.cprNumber = cprNumber;
    }

    @QueryField(type = QueryField.FieldType.STRING, queryName = FIRSTNAME)
    private String firstName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @QueryField(type = QueryField.FieldType.STRING, queryName = LASTNAME)
    private String lastName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public Map<String, Object> getSearchParameters() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(CPR, this.cprNumber);
        map.put(FIRSTNAME, this.firstName);
        map.put(LASTNAME, this.lastName);
        return map;
    }

    @Override
    public void setFromParameters(ParameterMap parameters) {
        this.setCprNumber(parameters.getFirst(CPR));
        this.setFirstName(parameters.getFirst(FIRSTNAME));
        this.setLastName(parameters.getFirst(LASTNAME));
    }

    @Override
    public Class<PersonEntity> getEntityClass() {
        return PersonEntity.class;
    }

    @Override
    public Class getDataClass() {
        return PersonBaseData.class;
    }


    @Override
    public LookupDefinition getLookupDefinition() {
        LookupDefinition lookupDefinition = new LookupDefinition(this);
        if (this.cprNumber != null) {
            lookupDefinition.put(LookupDefinition.entityref + ".cprNumber", this.cprNumber);
        }
        if (this.firstName != null) {
            lookupDefinition.put("nameData.firstName", this.firstName);
        }
        if (this.lastName != null) {
            lookupDefinition.put("nameData.lastName", this.lastName);
        }
        return lookupDefinition;
    }

}

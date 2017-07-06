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

    public static final String PERSONNUMMER = "personnummer";
    public static final String FORNAVN = "fornavn";
    public static final String EFTERNAVN = "efternavn";

    @QueryField(type = QueryField.FieldType.STRING, queryName = PERSONNUMMER)
    private String personnummer;

    public String getPersonnummer() {
        return this.personnummer;
    }

    public void setPersonnummer(String personnummer) {
        this.personnummer = personnummer;
    }

    @QueryField(type = QueryField.FieldType.STRING, queryName = FORNAVN)
    private String fornavn;

    public String getFornavn() {
        return fornavn;
    }

    public void setFornavn(String fornavn) {
        this.fornavn = fornavn;
    }

    @QueryField(type = QueryField.FieldType.STRING, queryName = EFTERNAVN)
    private String efternavn;

    public String getEfternavn() {
        return efternavn;
    }

    public void setEfternavn(String efternavn) {
        this.efternavn = efternavn;
    }

    @Override
    public Map<String, Object> getSearchParameters() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(PERSONNUMMER, this.personnummer);
        map.put(FORNAVN, this.fornavn);
        map.put(EFTERNAVN, this.efternavn);
        return map;
    }

    @Override
    public void setFromParameters(ParameterMap parameters) {
        this.setPersonnummer(parameters.getFirst(PERSONNUMMER));
        this.setFornavn(parameters.getFirst(FORNAVN));
        this.setEfternavn(parameters.getFirst(EFTERNAVN));
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
        if (this.personnummer != null) {
            lookupDefinition.put(LookupDefinition.entityref + ".personnummer", this.personnummer);
        }
        if (this.fornavn != null) {
            lookupDefinition.put("nameData.fornavn", this.fornavn);
        }
        if (this.efternavn != null) {
            lookupDefinition.put("nameData.efternavn", this.efternavn);
        }
        return lookupDefinition;
    }

}

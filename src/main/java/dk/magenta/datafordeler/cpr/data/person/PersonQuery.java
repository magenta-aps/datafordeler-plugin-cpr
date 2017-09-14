package dk.magenta.datafordeler.cpr.data.person;

import dk.magenta.datafordeler.core.database.LookupDefinition;
import dk.magenta.datafordeler.core.fapi.ParameterMap;
import dk.magenta.datafordeler.core.fapi.QueryField;
import dk.magenta.datafordeler.cpr.data.CprQuery;
import dk.magenta.datafordeler.cpr.data.person.data.PersonAddressData;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.data.person.data.PersonCoreData;
import dk.magenta.datafordeler.cpr.data.person.data.PersonNameData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 19-05-17.
 */
public class PersonQuery extends CprQuery<PersonEntity> {

    public static final String PERSONNUMMER = "personnummer";
    public static final String FORNAVNE = "fornavne";
    public static final String EFTERNAVN = "efternavn";
    public static final String KOMMUNEKODE = "kommunekode";

    @QueryField(type = QueryField.FieldType.STRING, queryName = PERSONNUMMER)
    private String personnummer;

    public String getPersonnummer() {
        return this.personnummer;
    }

    public void setPersonnummer(String personnummer) {
        this.personnummer = personnummer;
    }

    @QueryField(type = QueryField.FieldType.STRING, queryName = FORNAVNE)
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




    @QueryField(type = QueryField.FieldType.STRING, queryName = KOMMUNEKODE)
    private String kommunekode;

    public String getKommunekode() {
        return this.kommunekode;
    }

    public void setKommunekode(String kommunekode) {
        this.kommunekode = kommunekode;
    }



    @Override
    public Map<String, Object> getSearchParameters() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(PERSONNUMMER, this.personnummer);
        map.put(FORNAVNE, this.fornavn);
        map.put(EFTERNAVN, this.efternavn);
        map.put(KOMMUNEKODE, this.kommunekode);
        return map;
    }

    @Override
    public void setFromParameters(ParameterMap parameters) {
        this.setPersonnummer(parameters.getFirst(PERSONNUMMER));
        this.setFornavn(parameters.getFirst(FORNAVNE));
        this.setEfternavn(parameters.getFirst(EFTERNAVN));
        this.setKommunekode(parameters.getFirst(KOMMUNEKODE));
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
            lookupDefinition.put(LookupDefinition.entityref + LookupDefinition.separator + PersonCoreData.DB_FIELD_CPR_NUMBER, this.personnummer, String.class);
        }
        if (this.fornavn != null) {
            lookupDefinition.put(PersonBaseData.DB_FIELD_NAME + LookupDefinition.separator + PersonNameData.DB_FIELD_FIRST_NAMES, this.fornavn, String.class);
        }
        if (this.efternavn != null) {
            lookupDefinition.put(PersonBaseData.DB_FIELD_NAME + LookupDefinition.separator + PersonNameData.DB_FIELD_LAST_NAME, this.efternavn, String.class);
        }
        if (this.kommunekode != null) {
            lookupDefinition.put(PersonBaseData.DB_FIELD_ADDRESS + LookupDefinition.separator + PersonAddressData.DB_FIELD_MUNICIPALITY_CODE, this.kommunekode, Integer.class);
        }
        return lookupDefinition;
    }

}

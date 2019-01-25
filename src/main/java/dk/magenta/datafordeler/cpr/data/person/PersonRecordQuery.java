package dk.magenta.datafordeler.cpr.data.person;

import dk.magenta.datafordeler.core.database.BaseLookupDefinition;
import dk.magenta.datafordeler.core.database.LookupDefinition;
import dk.magenta.datafordeler.core.fapi.BaseQuery;
import dk.magenta.datafordeler.core.fapi.ParameterMap;
import dk.magenta.datafordeler.core.fapi.QueryField;
import dk.magenta.datafordeler.cpr.records.person.data.AddressDataRecord;
import dk.magenta.datafordeler.cpr.records.person.data.NameDataRecord;

import java.util.*;

/**
 * Container for a query for Persons, defining fields and database lookup
 */
public class PersonRecordQuery extends BaseQuery {

    
    public static final String PERSONNUMMER = PersonEntity.IO_FIELD_CPR_NUMBER;
    public static final String FORNAVNE = "fornavn";
    public static final String EFTERNAVN = "efternavn";
    public static final String KOMMUNEKODE = "cprKommunekode";

            @QueryField(type = QueryField.FieldType.STRING, queryName = PERSONNUMMER)
    private List<String> personnumre = new ArrayList<>();

            public Collection<String> getPersonnumre() {
                return this.personnumre;
            }

            public void addPersonnummer(String personnummer) {
                this.personnumre.add(personnummer);
                if (personnummer != null) {
                        this.increaseDataParamCount();
                    }
            }

            public void setPersonnummer(String personnummer) {
                this.personnumre.clear();
                this.addPersonnummer(personnummer);
            }

        
            @QueryField(type = QueryField.FieldType.STRING, queryName = FORNAVNE)
    private String fornavn;

            public String getFornavn() {
                return fornavn;
            }

            public void setFornavn(String fornavn) {
                this.fornavn = fornavn;
                if (fornavn != null) {
                        this.increaseDataParamCount();
                    }
            }

            @QueryField(type = QueryField.FieldType.STRING, queryName = EFTERNAVN)
    private String efternavn;

            public String getEfternavn() {
                return efternavn;
            }

            public void setEfternavn(String efternavn) {
                this.efternavn = efternavn;
                if (efternavn != null) {
                        this.increaseDataParamCount();
                    }
            }

        
        
            @QueryField(type = QueryField.FieldType.STRING, queryName = KOMMUNEKODE)
    private List<String> kommunekoder = new ArrayList<>();

            public Collection<String> getKommunekoder() {
                return this.kommunekoder;
            }

            public void addKommunekode(String kommunekode) {
                this.kommunekoder.add(kommunekode);
            }

            public void addKommunekode(int kommunekode) {
                this.addKommunekode(String.format("%03d", kommunekode));
            }

            public void clearKommunekode() {
                this.kommunekoder.clear();
            }

        
        
            @Override
    public Map<String, Object> getSearchParameters() {
                HashMap<String, Object> map = new HashMap<>();
                map.put(PERSONNUMMER, this.personnumre);
                map.put(FORNAVNE, this.fornavn);
                map.put(EFTERNAVN, this.efternavn);
                map.put(KOMMUNEKODE, this.kommunekoder);
                return map;
            }

            @Override
    public void setFromParameters(ParameterMap parameters) {
                if (parameters.containsKey(PERSONNUMMER)) {
                        for (String personnummer : parameters.get(PERSONNUMMER)) {
                                this.addPersonnummer(personnummer);
                            }
                    }
                this.setFornavn(parameters.getFirst(FORNAVNE));
                this.setEfternavn(parameters.getFirst(EFTERNAVN));
                if (parameters.containsKey(KOMMUNEKODE)) {
                        for (String kommunekode : parameters.get(KOMMUNEKODE)) {
                                this.addKommunekode(kommunekode);
                            }
                    }
            }


        
    
    @Override
    public BaseLookupDefinition getLookupDefinition() {
        BaseLookupDefinition lookupDefinition = new BaseLookupDefinition();
        if (!this.getPersonnumre().isEmpty()) {
            lookupDefinition.put(LookupDefinition.entityref + LookupDefinition.separator + PersonEntity.DB_FIELD_CPR_NUMBER, this.getPersonnumre(), String.class);
        }
        if (this.getFornavn() != null) {
            lookupDefinition.put(LookupDefinition.entityref + LookupDefinition.separator + PersonEntity.DB_FIELD_NAME + LookupDefinition.separator + NameDataRecord.DB_FIELD_FIRST_NAMES, this.getFornavn(), String.class);
        }
        if (this.getEfternavn() != null) {
            lookupDefinition.put(LookupDefinition.entityref + LookupDefinition.separator + PersonEntity.DB_FIELD_NAME + LookupDefinition.separator + NameDataRecord.DB_FIELD_LAST_NAME, this.getEfternavn(), String.class);
        }
        if (!this.getKommunekoder().isEmpty()) {
            lookupDefinition.put(LookupDefinition.entityref + LookupDefinition.separator + PersonEntity.DB_FIELD_ADDRESS + LookupDefinition.separator + AddressDataRecord.DB_FIELD_MUNICIPALITY_CODE, this.getKommunekoder(), Integer.class);
        }
        if (!this.getKommunekodeRestriction().isEmpty()) {
            lookupDefinition.put(LookupDefinition.entityref + LookupDefinition.separator + PersonEntity.DB_FIELD_ADDRESS + LookupDefinition.separator + AddressDataRecord.DB_FIELD_MUNICIPALITY_CODE, this.getKommunekodeRestriction(), Integer.class);
        }
        return lookupDefinition;
    }

}

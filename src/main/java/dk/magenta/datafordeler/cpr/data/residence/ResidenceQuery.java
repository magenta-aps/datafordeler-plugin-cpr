package dk.magenta.datafordeler.cpr.data.residence;

import dk.magenta.datafordeler.core.database.LookupDefinition;
import dk.magenta.datafordeler.core.fapi.ParameterMap;
import dk.magenta.datafordeler.core.fapi.QueryField;
import dk.magenta.datafordeler.cpr.data.CprQuery;
import dk.magenta.datafordeler.cpr.data.residence.data.ResidenceBaseData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 19-05-17.
 */
public class ResidenceQuery extends CprQuery<ResidenceEntity> {

    public static final String KOMMUNEKODE = "kommunekode";
    public static final String VEJKODE = "vejkode";
    public static final String HUSNUMMER = "husnummer";
    public static final String ETAGE = "etage";
    public static final String SIDE_DOER = "sideDoer";

    @QueryField(type = QueryField.FieldType.INT, queryName = KOMMUNEKODE)
    private String kommunekode;

    public String getKommunekode() {
        return this.kommunekode;
    }

    public void setKommunekode(String kommunekode) {
        this.kommunekode = kommunekode;
    }

    @QueryField(type = QueryField.FieldType.STRING, queryName = VEJKODE)
    private String vejkode;

    public String getVejkode() {
        return this.vejkode;
    }

    public void setVejkode(String vejkode) {
        this.vejkode = vejkode;
    }

    @QueryField(type = QueryField.FieldType.STRING, queryName = HUSNUMMER)
    private String husnummer;

    public String getHusnummer() {
        return this.husnummer;
    }

    public void setHusnummer(String husnummer) {
        this.husnummer = husnummer;
    }

    @QueryField(type = QueryField.FieldType.STRING, queryName = ETAGE)
    private String etage;

    public String getEtage() {
        return this.etage;
    }

    public void setEtage(String etage) {
        this.etage = etage;
    }

    @QueryField(type = QueryField.FieldType.STRING, queryName = SIDE_DOER)
    private String sideDoer;

    public String getSideDoer() {
        return this.sideDoer;
    }

    public void setSideDoer(String sideDoer) {
        this.sideDoer = sideDoer;
    }

    @Override
    public Map<String, Object> getSearchParameters() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(KOMMUNEKODE, this.kommunekode);
        map.put(VEJKODE, this.vejkode);
        map.put(HUSNUMMER, this.husnummer);
        map.put(ETAGE, this.etage);
        map.put(SIDE_DOER, this.sideDoer);
        return map;
    }

    @Override
    public void setFromParameters(ParameterMap parameters) {
        this.setKommunekode(parameters.getFirst(KOMMUNEKODE));
        this.setVejkode(parameters.getFirst(VEJKODE));
        this.setHusnummer(parameters.getFirst(HUSNUMMER));
        this.setEtage(parameters.getFirst(ETAGE));
        this.setSideDoer(parameters.getFirst(SIDE_DOER));
    }

    @Override
    public Class<ResidenceEntity> getEntityClass() {
        return ResidenceEntity.class;
    }

    @Override
    public Class getDataClass() {
        return ResidenceBaseData.class;
    }


    @Override
    public LookupDefinition getLookupDefinition() {
        LookupDefinition lookupDefinition = new LookupDefinition(this);
        if (this.kommunekode != null) {
            lookupDefinition.put(KOMMUNEKODE, this.kommunekode);
        }
        if (this.vejkode != null) {
            lookupDefinition.put(VEJKODE, this.vejkode);
        }
        if (this.husnummer != null) {
            lookupDefinition.put(HUSNUMMER, this.husnummer);
        }
        if (this.etage != null) {
            lookupDefinition.put(ETAGE, this.etage);
        }
        if (this.sideDoer != null) {
            lookupDefinition.put(SIDE_DOER, this.sideDoer);
        }
        return lookupDefinition;
    }

}

package dk.magenta.datafordeler.cpr.data.person.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.CprPlugin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Storage for data on a Person's civilstatus authority name,
 * referenced by {@link dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData}
 */
@Entity
@Table(name = CprPlugin.DEBUG_TABLE_PREFIX + "cpr_person_civil_status_authority_text")
public class PersonCivilStatusAuthorityTextData extends AuthorityDetailData {


    public static final String DB_FIELD_TEXT = "text";
    public static final String IO_FIELD_TEXT = "tekst";
    @Column(name = DB_FIELD_TEXT)
    @JsonProperty(value = IO_FIELD_TEXT)
    @XmlElement(name = IO_FIELD_TEXT)
    private String text;


    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public static final String DB_FIELD_CORRECTION_MARKING = "correctionMarking";
    public static final String IO_FIELD_CORRECTION_MARKING = "retFortrydMarkering";
    @Column(name = DB_FIELD_CORRECTION_MARKING, length = 1)
    @JsonProperty(value = IO_FIELD_CORRECTION_MARKING)
    @XmlElement(name = IO_FIELD_CORRECTION_MARKING)
    private String correctionMarking;

    public String getCorrectionMarking() {
        return this.correctionMarking;
    }

    public void setCorrectionMarking(String correctionMarking) {
        this.correctionMarking = correctionMarking;
    }


    @Override
    public Map<String, Object> databaseFields() {
        HashMap<String, Object> map = new HashMap<>(super.databaseFields());
        map.put(DB_FIELD_TEXT, this.text);
        map.put(DB_FIELD_CORRECTION_MARKING, this.correctionMarking);
        return map;
    }

    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>(super.asMap());
        map.put("text", this.text);
        map.put("correctionMarking", this.correctionMarking);
        return map;
    }

    @Override
    protected PersonCivilStatusAuthorityTextData clone() {
        PersonCivilStatusAuthorityTextData clone = new PersonCivilStatusAuthorityTextData();
        clone.text = this.text;
        clone.correctionMarking = this.correctionMarking;
        clone.setAuthority(this.getAuthority());
        clone.setDafoUpdated(this.getDafoUpdated());
        return clone;
    }
}

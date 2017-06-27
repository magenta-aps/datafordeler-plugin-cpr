package dk.magenta.datafordeler.cpr.data.person.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.data.DetailData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 27-06-17.
 */
@Entity
@Table(name = "cpr_person_emigration")
public class PersonEmigrationData extends DetailData {


    @Column
    @JsonProperty
    @XmlElement
    private int authority;

    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }

    @Column
    @JsonProperty
    @XmlElement
    private int countryCode;

    public int getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(int countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("authority", this.authority);
        map.put("countryCode", this.countryCode);
        return map;
    }
}

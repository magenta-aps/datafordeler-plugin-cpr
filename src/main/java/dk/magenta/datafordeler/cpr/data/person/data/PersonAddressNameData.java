package dk.magenta.datafordeler.cpr.data.person.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 22-06-17.
 */
@Entity
@Table(name = "cpr_person_addressname")
public class PersonAddressNameData extends AuthorityDetailData {

    @Column
    @JsonProperty(value = "adressenavn")
    @XmlElement(name = "adressenavn")
    private String adressenavn;

    public String getAdressenavn() {
        return this.adressenavn;
    }

    public void setAdressenavn(String adressenavn) {
        this.adressenavn = adressenavn;
    }

    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>(super.asMap());
        map.put("adressenavn", this.adressenavn);
        return map;
    }
}

package dk.magenta.datafordeler.cpr.data.person.data;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class PersonEmigrationData extends AuthorityDetailData {


    @Column
    @JsonProperty(value = "landekode")
    @XmlElement(name = "landekode")
    private int landekode;

    public int getLandekode() {
        return landekode;
    }

    public void setLandekode(int landekode) {
        this.landekode = landekode;
    }

    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>(super.asMap());
        map.put("landekode", this.landekode);
        return map;
    }
}

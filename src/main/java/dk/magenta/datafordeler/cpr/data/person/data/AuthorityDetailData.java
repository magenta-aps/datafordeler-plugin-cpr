package dk.magenta.datafordeler.cpr.data.person.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.data.DetailData;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 29-06-17.
 */
@MappedSuperclass
public abstract class AuthorityDetailData extends DetailData {

    @Column
    @JsonProperty(value = "myndighed")
    @XmlElement(name = "myndighed")
    private int myndighed;

    public int getMyndighed() {
        return this.myndighed;
    }

    public void setMyndighed(int myndighed) {
        this.myndighed = myndighed;
    }


    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("myndighed", this.myndighed);
        return map;
    }
}

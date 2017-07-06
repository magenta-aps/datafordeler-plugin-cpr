package dk.magenta.datafordeler.cpr.data.person.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 21-06-17.
 */
@Entity
@Table(name = "cpr_person_name_verification")
public class PersonNameVerificationData extends AuthorityDetailData {


    @Column
    @JsonProperty(value = "verificeret")
    @XmlElement(name = "verificeret")
    private boolean verificeret;

    public boolean isVerificeret() {
        return this.verificeret;
    }

    public void setVerificeret(boolean verificeret) {
        this.verificeret = verificeret;
    }



    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>(super.asMap());
        map.put("verificeret", this.verificeret);
        return map;
    }
}

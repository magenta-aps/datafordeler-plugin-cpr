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
 * Created by lars on 21-06-17.
 */
@Entity
@Table(name = "cpr_person_name_verification")
public class PersonNameVerificationData extends DetailData {


    @Column
    @JsonProperty
    @XmlElement
    private int authority;

    public int getAuthority() {
        return this.authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }



    @Column
    @JsonProperty
    @XmlElement
    private boolean verification;

    public boolean isVerification() {
        return this.verification;
    }

    public void setVerification(boolean verification) {
        this.verification = verification;
    }



    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("authority", this.authority);
        map.put("verification", this.verification);
        return map;
    }
}
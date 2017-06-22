package dk.magenta.datafordeler.cpr.data.person.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.data.DetailData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 21-06-17.
 */
@Entity
@Table(name = "cpr_person_parent_verification")
public class PersonParentVerificationData extends DetailData {

    @Column
    @JsonIgnore
    @XmlTransient
    private boolean isMother;

    @JsonIgnore
    @XmlTransient
    public boolean isMother() {
        return this.isMother;
    }

    public void setMother(boolean mother) {
        isMother = mother;
    }



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
    private boolean verified;

    public boolean isVerified() {
        return this.verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }



    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("authority", this.authority);
        map.put("verified", this.verified);
        return map;
    }
}

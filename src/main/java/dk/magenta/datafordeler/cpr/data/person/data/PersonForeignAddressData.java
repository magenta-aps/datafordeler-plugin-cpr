package dk.magenta.datafordeler.cpr.data.person.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.data.DetailData;

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
@Table(name = "cpr_person_foreign_address")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PersonForeignAddressData extends DetailData {

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
    private String addressLine1;

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    @Column
    @JsonProperty
    @XmlElement
    private String addressLine2;

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    @Column
    @JsonProperty
    @XmlElement
    private String addressLine3;

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    @Column
    @JsonProperty
    @XmlElement
    private String addressLine4;

    public String getAddressLine4() {
        return addressLine4;
    }

    public void setAddressLine4(String addressLine4) {
        this.addressLine4 = addressLine4;
    }

    @Column
    @JsonProperty
    @XmlElement
    private String addressLine5;

    public String getAddressLine5() {
        return addressLine5;
    }

    public void setAddressLine5(String addressLine5) {
        this.addressLine5 = addressLine5;
    }

    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("authority", this.authority);
        map.put("addressLine1", this.addressLine1);
        map.put("addressLine2", this.addressLine2);
        map.put("addressLine3", this.addressLine3);
        map.put("addressLine4", this.addressLine4);
        map.put("addressLine5", this.addressLine5);
        return map;
    }
}

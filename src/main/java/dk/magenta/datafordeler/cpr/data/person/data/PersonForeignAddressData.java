package dk.magenta.datafordeler.cpr.data.person.data;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@Table(name = "cpr_person_foreign_address")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PersonForeignAddressData extends AuthorityDetailData {


    @Column
    @JsonProperty(value = "adresselinie1")
    @XmlElement(name = "adresselinie1")
    private String adresselinie1;

    public String getAdresselinie1() {
        return adresselinie1;
    }

    public void setAdresselinie1(String adresselinie1) {
        this.adresselinie1 = adresselinie1;
    }

    @Column
    @JsonProperty(value = "adresselinie2")
    @XmlElement(name = "adresselinie2")
    private String adresselinie2;

    public String getAdresselinie2() {
        return adresselinie2;
    }

    public void setAdresselinie2(String adresselinie2) {
        this.adresselinie2 = adresselinie2;
    }

    @Column
    @JsonProperty(value = "adresselinie3")
    @XmlElement(name = "adresselinie3")
    private String adresselinie3;

    public String getAdresselinie3() {
        return adresselinie3;
    }

    public void setAdresselinie3(String adresselinie3) {
        this.adresselinie3 = adresselinie3;
    }

    @Column
    @JsonProperty(value = "adresselinie4")
    @XmlElement(name = "adresselinie4")
    private String adresselinie4;

    public String getAdresselinie4() {
        return adresselinie4;
    }

    public void setAdresselinie4(String adresselinie4) {
        this.adresselinie4 = adresselinie4;
    }

    @Column
    @JsonProperty(value = "adresselinie5")
    @XmlElement(name = "adresselinie5")
    private String adresselinie5;

    public String getAdresselinie5() {
        return adresselinie5;
    }

    public void setAdresselinie5(String adresselinie5) {
        this.adresselinie5 = adresselinie5;
    }

    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>(super.asMap());
        map.put("adresselinie1", this.adresselinie1);
        map.put("adresselinie2", this.adresselinie2);
        map.put("adresselinie3", this.adresselinie3);
        map.put("adresselinie4", this.adresselinie4);
        map.put("adresselinie5", this.adresselinie5);
        return map;
    }
}

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
@Table(name = "cpr_person_protection")
public class PersonProtectionData extends DetailData {

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
    private int protectionType;

    public int getProtectionType() {
        return protectionType;
    }

    public void setProtectionType(int protectionType) {
        this.protectionType = protectionType;
    }

    @Column
    @JsonProperty
    @XmlElement
    private boolean reportMarking;

    public boolean getReportMarking() {
        return reportMarking;
    }

    public void setReportMarking(boolean reportMarking) {
        this.reportMarking = reportMarking;
    }

    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("authority", this.authority);
        map.put("protectionType", this.protectionType);
        map.put("reportMarking", this.reportMarking);
        return map;
    }
}

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
@Table(name = "cpr_person_protection")
public class PersonProtectionData extends AuthorityDetailData {


    public static final String DB_FIELD_TYPE = "protectionType";
    public static final String IO_FIELD_TYPE = "beskyttelsestype";
    @Column(name = DB_FIELD_TYPE)
    @JsonProperty(value = IO_FIELD_TYPE)
    @XmlElement(name = IO_FIELD_TYPE)
    private int protectionType;

    public int getProtectionType() {
        return protectionType;
    }

    public void setProtectionType(int protectionType) {
        this.protectionType = protectionType;
    }



    public static final String DB_FIELD_REPORTMARKING = "reportMarking";
    public static final String IO_FIELD_REPORTMARKING = "rapportMarkering";
    @Column(name = DB_FIELD_REPORTMARKING)
    @JsonProperty(value = IO_FIELD_REPORTMARKING)
    @XmlElement(name = IO_FIELD_REPORTMARKING)
    private boolean reportMarking;

    public boolean getReportMarking() {
        return reportMarking;
    }

    public void setReportMarking(boolean reportMarking) {
        this.reportMarking = reportMarking;
    }

    @Override
    public Map<String, Object> databaseFields() {
        HashMap<String, Object> map = new HashMap<>(super.databaseFields());
        map.put(DB_FIELD_TYPE, this.protectionType);
        map.put(DB_FIELD_REPORTMARKING, this.reportMarking);
        return map;
    }

    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>(super.asMap());
        map.put("protectionType", this.protectionType);
        map.put("reportMarking", this.reportMarking);
        return map;
    }
}

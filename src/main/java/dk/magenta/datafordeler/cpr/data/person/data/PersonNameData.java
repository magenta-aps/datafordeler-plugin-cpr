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
 * Created by lars on 22-06-17.
 */
@Entity
@Table(name = "cpr_person_name")
public class PersonNameData extends DetailData {

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
    private String firstName;

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column
    @JsonProperty
    @XmlElement
    private boolean firstNameMarking;

    public boolean isFirstNameMarking() {
        return this.firstNameMarking;
    }

    public void setFirstNameMarking(boolean firstNameMarking) {
        this.firstNameMarking = firstNameMarking;
    }

    @Column
    @JsonProperty
    @XmlElement
    private String middleName;

    public String getMiddleName() {
        return this.middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @Column
    @JsonProperty
    @XmlElement
    private boolean middleNameMarking;

    public boolean isMiddleNameMarking() {
        return this.middleNameMarking;
    }

    public void setMiddleNameMarking(boolean middleNameMarking) {
        this.middleNameMarking = middleNameMarking;
    }

    @Column
    @JsonProperty
    @XmlElement
    private String lastName;

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column
    @JsonProperty
    @XmlElement
    private boolean lastNameMarking;

    public boolean isLastNameMarking() {
        return this.lastNameMarking;
    }

    public void setLastNameMarking(boolean lastNameMarking) {
        this.lastNameMarking = lastNameMarking;
    }

    @Column
    @JsonProperty
    @XmlElement
    private String ownLastName;

    public String getOwnLastName() {
        return this.ownLastName;
    }

    public void setOwnLastName(String ownLastName) {
        this.ownLastName = ownLastName;
    }

    @Column
    @JsonProperty
    @XmlElement
    private boolean ownLastNameMarking;

    public boolean isOwnLastNameMarking() {
        return this.ownLastNameMarking;
    }

    public void setOwnLastNameMarking(boolean ownLastNameMarking) {
        this.ownLastNameMarking = ownLastNameMarking;
    }


    @Column
    @JsonProperty
    @XmlElement
    private boolean reportNames;

    public boolean isReportNames() {
        return this.reportNames;
    }

    public void setReportNames(boolean reportNames) {
        this.reportNames = reportNames;
    }

    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("authority", this.authority);
        map.put("firstName", this.firstName);
        map.put("firstNameMarking", this.firstNameMarking);
        map.put("middleName", this.middleName);
        map.put("middleNameMarking", this.middleNameMarking);
        map.put("lastName", this.lastName);
        map.put("lastNameMarking", this.lastNameMarking);
        map.put("ownLastName", this.ownLastName);
        map.put("ownLastNameMarking", this.ownLastNameMarking);
        map.put("reportNames", this.reportNames);
        return map;
    }
}

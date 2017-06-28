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
@Table(name = "cpr_person_parent")
public class PersonParentData extends DetailData {

    @Column
    @JsonIgnore
    @XmlTransient
    private boolean isMother;

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
    private int cprNumber;

    public int getCprNumber() {
        return this.cprNumber;
    }

    public void setCprNumber(int cprNumber) {
        this.cprNumber = cprNumber;
    }



    @Column
    @JsonProperty
    @XmlElement
    private LocalDate birthDate;

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }



    @Column
    @JsonProperty
    @XmlElement
    private boolean birthDateUncertain;

    public boolean isBirthDateUncertain() {
        return this.birthDateUncertain;
    }

    public void setBirthDateUncertain(boolean birthDateUncertain) {
        this.birthDateUncertain = birthDateUncertain;
    }



    @Column
    @JsonProperty
    @XmlElement
    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }



    @Column
    @JsonProperty
    @XmlElement
    private boolean nameMarking;

    public boolean isNameMarking() {
        return this.nameMarking;
    }

    public void setNameMarking(boolean nameMarking) {
        this.nameMarking = nameMarking;
    }



    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("authority", this.authority);
        map.put("cprNumber", this.cprNumber);
        map.put("birthDate", this.birthDate);
        map.put("birthDateUncertain", this.birthDateUncertain);
        map.put("name", this.name);
        map.put("nameMarking", this.nameMarking);
        return map;
    }
}

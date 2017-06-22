package dk.magenta.datafordeler.cpr.data.person.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.data.DetailData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 21-06-17.
 */
@Entity
@Table(name = "cpr_person_birth")
public class PersonBirthData extends DetailData {

    @Column
    @JsonProperty
    @XmlElement
    private LocalDateTime birthDateTime;

    public LocalDateTime getBirthDateTime() {
        return this.birthDateTime;
    }

    public void setBirthDateTime(LocalDateTime birthDateTime) {
        this.birthDateTime = birthDateTime;
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
    private int birthSequence;

    public int getBirthSequence() {
        return this.birthSequence;
    }

    public void setBirthSequence(int birthSequence) {
        this.birthSequence = birthSequence;
    }



    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("birthDateTime", this.birthDateTime);
        map.put("birthDateUncertain", this.birthDateUncertain);
        map.put("birthSequence", this.birthSequence);
        return map;
    }
}

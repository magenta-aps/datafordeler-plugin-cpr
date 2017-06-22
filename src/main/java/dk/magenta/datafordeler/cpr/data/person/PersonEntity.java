package dk.magenta.datafordeler.cpr.data.person;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dk.magenta.datafordeler.core.database.Entity;

import javax.persistence.Column;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by lars on 16-05-17.
 */
@javax.persistence.Entity
@Table(name="cpr_person_entity", indexes = {@Index(name = "cprNumber", columnList = "cprNumber")})
public class PersonEntity extends Entity<PersonEntity, PersonRegistration> {

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="type")
    public static final String schema = "Person";

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
}
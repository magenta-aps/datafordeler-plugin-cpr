package dk.magenta.datafordeler.cpr.data.person;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.data.CprData;
import dk.magenta.datafordeler.cpr.records.CprRecord;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 16-05-17.
 */
@Entity
@Table(name="cpr_person_data")
public class PersonBaseData extends CprData<PersonEffect, PersonBaseData> {

    @Column(unique = true, nullable = false, insertable = true, updatable = false)
    @JsonProperty(value = "firstName")
    @XmlElement(name = "firstName")
    private String firstName;

    @Column(unique = true, nullable = false, insertable = true, updatable = false)
    @JsonProperty(value = "lastName")
    @XmlElement(name = "lastName")
    private String lastName;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonCoreData coreData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonStatusData statusData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonParentData motherData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonParentData fatherData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonParentVerificationData motherVerificationData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonParentVerificationData fatherVerificationData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonPositionData positionData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonBirthData birthData;

    public void setCurrentCprNumber(int cprNumber) {
        if (this.coreData == null) {
            this.coreData = new PersonCoreData();
        }
        this.coreData.setCurrentCprNumber(cprNumber);
    }

    public void setGender(String gender) {
        if (this.coreData == null) {
            this.coreData = new PersonCoreData();
        }
        this.coreData.setGender(gender);
    }

    public void setStartAuthority(int authority) {
        if (this.coreData == null) {
            this.coreData = new PersonCoreData();
        }
        this.coreData.setStartAuthority(authority);
    }

    public void setStatus(String status) {
        if (this.statusData == null) {
            this.statusData = new PersonStatusData();
        }
        this.statusData.setStatus(status);
    }

    public void setMother(String name, boolean nameMarking, int cprNumber, LocalDate birthDate, boolean birthDateUncertain, int authorityCode) {
        if (this.motherData == null) {
            this.motherData = new PersonParentData();
            this.motherData.setMother(true);
        }
        this.motherData.setName(name);
        this.motherData.setNameMarking(nameMarking);
        this.motherData.setCprNumber(cprNumber);
        this.motherData.setBirthDate(birthDate);
        this.motherData.setBirthDateUncertain(birthDateUncertain);
        this.motherData.setAuthority(authorityCode);
    }

    public void setFather(String name, boolean nameMarking, int cprNumber, LocalDate birthDate, boolean birthDateUncertain, int authorityCode) {
        if (this.fatherData == null) {
            this.fatherData = new PersonParentData();
            this.fatherData.setMother(false);
        }
        this.fatherData.setName(name);
        this.fatherData.setNameMarking(nameMarking);
        this.fatherData.setCprNumber(cprNumber);
        this.fatherData.setBirthDate(birthDate);
        this.fatherData.setBirthDateUncertain(birthDateUncertain);
        this.fatherData.setAuthority(authorityCode);
    }

    public void setMotherVerification(int authorityCode, boolean verified) {
        if (this.motherVerificationData == null) {
            this.motherVerificationData = new PersonParentVerificationData();
            this.motherVerificationData.setMother(true);
        }
        this.motherVerificationData.setAuthority(authorityCode);
        this.motherVerificationData.setVerified(verified);
    }

    public void setFatherVerification(int authorityCode, boolean verified) {
        if (this.fatherVerificationData == null) {
            this.fatherVerificationData = new PersonParentVerificationData();
            this.fatherVerificationData.setMother(false);
        }
        this.fatherVerificationData.setAuthority(authorityCode);
        this.fatherVerificationData.setVerified(verified);
    }

    public void setPosition(int authorityCode, String position) {
        if (this.positionData == null) {
            this.positionData = new PersonPositionData();
        }
        this.positionData.setAuthority(authorityCode);
        this.positionData.setPosition(position);
    }

    public void setBirth(LocalDateTime birthDateTime, boolean birthDateUncertain, int birthSequence) {
        if (this.birthData == null) {
            this.birthData = new PersonBirthData();
        }
        this.birthData.setBirthDateTime(birthDateTime);
        this.birthData.setBirthDateUncertain(birthDateUncertain);
        this.birthData.setBirthSequence(birthSequence);
    }


    /**
     * Return a map of attributes, including those from the superclass
     * @return
     */
    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        if (this.coreData != null) {
            map.putAll(this.coreData.asMap());
        }
        if (this.statusData != null) {
            map.put("status", this.statusData.getStatus());
        }
        if (this.motherData != null) {
            map.put("mother", this.motherData);
        }
        if (this.fatherData != null) {
            map.put("father", this.fatherData);
        }
        if (this.motherVerificationData != null) {
            map.put("motherVerification", this.motherVerificationData);
        }
        if (this.fatherVerificationData != null) {
            map.put("fatherVerification", this.fatherVerificationData);
        }
        if (this.positionData != null) {
            map.put("position", this.positionData);
        }
        if (this.birthData != null) {
            map.put("birth", this.birthData);
        }

        return map;
    }

}

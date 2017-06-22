package dk.magenta.datafordeler.cpr.data.person.data;

import dk.magenta.datafordeler.cpr.data.CprData;
import dk.magenta.datafordeler.cpr.data.person.*;

import javax.persistence.*;
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

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonAddressData addressData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonAddressConameData coNameData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonMoveMunicipalityData moveMunicipalityData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonNameData nameData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonAddressNameData addressNameData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonNameVerificationData nameVerificationData;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PersonNameAuthorityTextData nameAuthorityTextData;



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

    public void setAddress(int authority, int municipalityCode, int roadCode,
                           String houseNumber, String floor, String door, String bNumber,
                           int addressTextType, int startAuthority,
                           String supplementaryAddress1, String supplementaryAddress2, String supplementaryAddress3, String supplementaryAddress4, String supplementaryAddress5) {
        if (this.addressData == null) {
            this.addressData = new PersonAddressData();
        }
        this.addressData.setAuthority(authority);
        this.addressData.setMunicipalityCode(municipalityCode);
        this.addressData.setRoadCode(roadCode);
        this.addressData.setHouseNumber(houseNumber);
        this.addressData.setFloor(floor);
        this.addressData.setDoor(door);
        this.addressData.setbNumber(bNumber);
        this.addressData.setAddressTextType(addressTextType);
        this.addressData.setStartAuthority(startAuthority);
        this.addressData.setSupplementaryAddress1(supplementaryAddress1);
        this.addressData.setSupplementaryAddress2(supplementaryAddress2);
        this.addressData.setSupplementaryAddress3(supplementaryAddress3);
        this.addressData.setSupplementaryAddress4(supplementaryAddress4);
        this.addressData.setSupplementaryAddress5(supplementaryAddress5);
    }

    public void setCoName(String coName) {
        if (this.coNameData == null) {
            this.coNameData = new PersonAddressConameData();
        }
        this.coNameData.setCoName(coName);
    }

    public void setMoveMunicipality(int authority, LocalDateTime moveToDate, boolean moveToDateUncertain, int moveFromMunicipality, LocalDateTime moveFromDate, boolean moveFromDateUncertain) {
        if (this.moveMunicipalityData == null) {
            this.moveMunicipalityData = new PersonMoveMunicipalityData();
        }
        this.moveMunicipalityData.setAuthority(authority);
        this.moveMunicipalityData.setMoveToDate(moveToDate);
        this.moveMunicipalityData.setMoveToDateUncertain(moveToDateUncertain);
        this.moveMunicipalityData.setMoveFromMunicipality(moveFromMunicipality);
        this.moveMunicipalityData.setMoveFromDate(moveFromDate);
        this.moveMunicipalityData.setMoveFromDateUncertain(moveFromDateUncertain);
    }

    public void setName(int authority, String firstName, boolean firstNameMarking, String middleName, boolean middleNameMarking,
                        String lastName, boolean lastNameMarking, String ownLastName, boolean ownLastNameMarking, boolean reportNames) {
        if (this.nameData == null) {
            this.nameData = new PersonNameData();
        }
        this.nameData.setAuthority(authority);
        this.nameData.setFirstName(firstName);
        this.nameData.setFirstNameMarking(firstNameMarking);
        this.nameData.setMiddleName(middleName);
        this.nameData.setMiddleNameMarking(middleNameMarking);
        this.nameData.setLastName(lastName);
        this.nameData.setLastNameMarking(lastNameMarking);
        this.nameData.setOwnLastName(ownLastName);
        this.nameData.setOwnLastNameMarking(ownLastNameMarking);
        this.nameData.setReportNames(reportNames);
    }

    public void setAddressName(int authority, String addressName) {
        if (this.addressNameData == null) {
            this.addressNameData = new PersonAddressNameData();
        }
        this.addressNameData.setAuthority(authority);
        this.addressNameData.setAddressName(addressName);
        System.out.println("this.addressNameData: "+this.addressNameData);
    }

    public void setNameVerification(int authority, boolean verification) {
        if (this.nameVerificationData == null) {
            this.nameVerificationData = new PersonNameVerificationData();
        }
        this.nameVerificationData.setAuthority(authority);
        this.nameVerificationData.setVerification(verification);
    }

    public void setNameAuthorityText(int authority, String text) {
        if (this.nameAuthorityTextData == null) {
            this.nameAuthorityTextData = new PersonNameAuthorityTextData();
        }
        this.nameAuthorityTextData.setAuthority(authority);
        this.nameAuthorityTextData.setText(text);
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
        if (this.addressData != null) {
            map.put("address", this.addressData);
        }
        if (this.nameData != null) {
            map.put("name", this.nameData);
        }
        if (this.addressNameData != null) {
            map.put("addressNameData", this.addressNameData);
        }
        if (this.nameVerificationData != null) {
            map.put("nameVerification", this.nameVerificationData);
        }
        if (this.nameAuthorityTextData != null) {
            map.put("nameAuthority", this.nameAuthorityTextData);
        }

        return map;
    }

}

package dk.magenta.datafordeler.cpr.data.person.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.data.DetailData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.util.Map;

/**
 * Created by lars on 22-06-17.
 */
@Entity
@Table(name = "cpr_person_address")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PersonAddressData extends DetailData {

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
    private int municipalityCode;

    public int getMunicipalityCode() {
        return this.municipalityCode;
    }

    public void setMunicipalityCode(int municipalityCode) {
        this.municipalityCode = municipalityCode;
    }



    @Column
    @JsonProperty
    @XmlElement
    private int roadCode;

    public int getRoadCode() {
        return this.roadCode;
    }

    public void setRoadCode(int roadCode) {
        this.roadCode = roadCode;
    }



    @Column
    @JsonProperty
    @XmlElement
    private String houseNumber;

    public String getHouseNumber() {
        return this.houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }



    @Column
    @JsonProperty
    @XmlElement
    private String floor;

    public String getFloor() {
        return this.floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }



    @Column
    @JsonProperty
    @XmlElement
    private String door;

    public String getDoor() {
        return this.door;
    }

    public void setDoor(String door) {
        this.door = door;
    }



    @Column
    @JsonProperty
    @XmlElement
    private String bNumber;

    public String getbNumber() {
        return this.bNumber;
    }

    public void setbNumber(String bNumber) {
        this.bNumber = bNumber;
    }



    @Column
    @JsonProperty
    @XmlElement
    private int addressTextType;

    public int getAddressTextType() {
        return this.addressTextType;
    }

    public void setAddressTextType(int addressTextType) {
        this.addressTextType = addressTextType;
    }



    @Column
    @JsonProperty
    @XmlElement
    private int startAuthority;

    public int getStartAuthority() {
        return this.startAuthority;
    }

    public void setStartAuthority(int startAuthority) {
        this.startAuthority = startAuthority;
    }



    @Column
    @JsonProperty
    @XmlElement
    private String supplementaryAddress1;

    public String getSupplementaryAddress1() {
        return this.supplementaryAddress1;
    }

    public void setSupplementaryAddress1(String supplementaryAddress1) {
        this.supplementaryAddress1 = supplementaryAddress1;
    }



    @Column
    @JsonProperty
    @XmlElement
    private String supplementaryAddress2;

    public String getSupplementaryAddress2() {
        return this.supplementaryAddress2;
    }

    public void setSupplementaryAddress2(String supplementaryAddress2) {
        this.supplementaryAddress2 = supplementaryAddress2;
    }



    @Column
    @JsonProperty
    @XmlElement
    private String supplementaryAddress3;

    public String getSupplementaryAddress3() {
        return this.supplementaryAddress3;
    }

    public void setSupplementaryAddress3(String supplementaryAddress3) {
        this.supplementaryAddress3 = supplementaryAddress3;
    }



    @Column
    @JsonProperty
    @XmlElement
    private String supplementaryAddress4;

    public String getSupplementaryAddress4() {
        return this.supplementaryAddress4;
    }

    public void setSupplementaryAddress4(String supplementaryAddress4) {
        this.supplementaryAddress4 = supplementaryAddress4;
    }



    @Column
    @JsonProperty
    @XmlElement
    private String supplementaryAddress5;

    public String getSupplementaryAddress5() {
        return this.supplementaryAddress5;
    }

    public void setSupplementaryAddress5(String supplementaryAddress5) {
        this.supplementaryAddress5 = supplementaryAddress5;
    }



    @Override
    public Map<String, Object> asMap() {
        return null;
    }
}
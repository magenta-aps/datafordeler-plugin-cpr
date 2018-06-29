package dk.magenta.datafordeler.cpr.records.person;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.CprPlugin;
import dk.magenta.datafordeler.cpr.records.CprBitemporalRecord;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

/**
 * Storage for data on a Person's addressing name,
 * referenced by {@link dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData}
 */
@Entity
@Table(name = CprPlugin.DEBUG_TABLE_PREFIX + AddressNameDataRecord.TABLE_NAME, indexes = {
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + AddressNameDataRecord.TABLE_NAME + CprBitemporalRecord.DB_FIELD_REGISTRATION_FROM, columnList = CprBitemporalRecord.DB_FIELD_REGISTRATION_FROM),
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + AddressNameDataRecord.TABLE_NAME + CprBitemporalRecord.DB_FIELD_REGISTRATION_TO, columnList = CprBitemporalRecord.DB_FIELD_REGISTRATION_TO),
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + AddressNameDataRecord.TABLE_NAME + CprBitemporalRecord.DB_FIELD_EFFECT_FROM, columnList = CprBitemporalRecord.DB_FIELD_EFFECT_FROM),
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + AddressNameDataRecord.TABLE_NAME + CprBitemporalRecord.DB_FIELD_EFFECT_TO, columnList = CprBitemporalRecord.DB_FIELD_EFFECT_TO),
})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AddressNameDataRecord extends CprBitemporalPersonRecord {

    public static final String TABLE_NAME = "cpr_person_address_name_record";

    public AddressNameDataRecord() {
    }

    public AddressNameDataRecord(String addressName, boolean rapportnavne) {
        this.addressName = addressName;
        this.rapportnavne = rapportnavne;
    }

    public static final String DB_FIELD_ADDRESS_NAME = "addressName";
    public static final String IO_FIELD_ADDRESS_NAME = "adressenavn";
    @Column(name = DB_FIELD_ADDRESS_NAME)
    @JsonProperty(value = IO_FIELD_ADDRESS_NAME)
    @XmlElement(name = IO_FIELD_ADDRESS_NAME)
    private String addressName;

    public String getAddressName() {
        return this.addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    @Column
    @JsonProperty
    @XmlElement
    private boolean rapportnavne;

    public boolean isRapportnavne() {
        return this.rapportnavne;
    }

    public void setRapportnavne(boolean rapportnavne) {
        this.rapportnavne = rapportnavne;
    }

    @Override
    protected AddressNameDataRecord clone() {
        AddressNameDataRecord clone = new AddressNameDataRecord();
        clone.addressName = this.addressName;
        clone.rapportnavne = this.rapportnavne;
        CprBitemporalRecord.copy(this, clone);
        return clone;
    }
}

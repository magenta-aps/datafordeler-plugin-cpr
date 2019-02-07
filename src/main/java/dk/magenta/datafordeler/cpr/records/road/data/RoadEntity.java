package dk.magenta.datafordeler.cpr.records.road.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.core.database.*;
import dk.magenta.datafordeler.cpr.CprPlugin;
import dk.magenta.datafordeler.cpr.data.CprRecordEntity;
import dk.magenta.datafordeler.cpr.records.CprBitemporalRecord;
import dk.magenta.datafordeler.cpr.records.CprMonotemporalRecord;
import dk.magenta.datafordeler.cpr.records.CprNontemporalRecord;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.*;

/**
 * An Entity representing a road. Bitemporal data is structured as
 * described in {@link dk.magenta.datafordeler.core.database.Entity}
 */
@javax.persistence.Entity
@Table(name= CprPlugin.DEBUG_TABLE_PREFIX + "road_entity", indexes = {
        @Index(name = CprPlugin.DEBUG_TABLE_PREFIX + "road_identification", columnList = RoadEntity.DB_FIELD_IDENTIFICATION, unique = true)
})
@FilterDefs({
        @FilterDef(name = Bitemporal.FILTER_EFFECTFROM_AFTER, parameters = @ParamDef(name = Bitemporal.FILTERPARAM_EFFECTFROM_AFTER, type = CprBitemporalRecord.FILTERPARAMTYPE_EFFECTFROM)),
        @FilterDef(name = Bitemporal.FILTER_EFFECTFROM_BEFORE, parameters = @ParamDef(name = Bitemporal.FILTERPARAM_EFFECTFROM_BEFORE, type = CprBitemporalRecord.FILTERPARAMTYPE_EFFECTFROM)),
        @FilterDef(name = Bitemporal.FILTER_EFFECTTO_AFTER, parameters = @ParamDef(name = Bitemporal.FILTERPARAM_EFFECTTO_AFTER, type = CprBitemporalRecord.FILTERPARAMTYPE_EFFECTTO)),
        @FilterDef(name = Bitemporal.FILTER_EFFECTTO_BEFORE, parameters = @ParamDef(name = Bitemporal.FILTERPARAM_EFFECTTO_BEFORE, type = CprBitemporalRecord.FILTERPARAMTYPE_EFFECTTO)),
        @FilterDef(name = Monotemporal.FILTER_REGISTRATIONFROM_AFTER, parameters = @ParamDef(name = Monotemporal.FILTERPARAM_REGISTRATIONFROM_AFTER, type = CprMonotemporalRecord.FILTERPARAMTYPE_REGISTRATIONFROM)),
        @FilterDef(name = Monotemporal.FILTER_REGISTRATIONFROM_BEFORE, parameters = @ParamDef(name = Monotemporal.FILTERPARAM_REGISTRATIONFROM_BEFORE, type = CprMonotemporalRecord.FILTERPARAMTYPE_REGISTRATIONFROM)),
        @FilterDef(name = Monotemporal.FILTER_REGISTRATIONTO_AFTER, parameters = @ParamDef(name = Monotemporal.FILTERPARAM_REGISTRATIONTO_AFTER, type = CprMonotemporalRecord.FILTERPARAMTYPE_REGISTRATIONTO)),
        @FilterDef(name = Monotemporal.FILTER_REGISTRATIONTO_BEFORE, parameters = @ParamDef(name = Monotemporal.FILTERPARAM_REGISTRATIONTO_BEFORE, type = CprMonotemporalRecord.FILTERPARAMTYPE_REGISTRATIONTO)),
        @FilterDef(name = Nontemporal.FILTER_LASTUPDATED_AFTER, parameters = @ParamDef(name = Nontemporal.FILTERPARAM_LASTUPDATED_AFTER, type = CprNontemporalRecord.FILTERPARAMTYPE_LASTUPDATED)),
        @FilterDef(name = Nontemporal.FILTER_LASTUPDATED_BEFORE, parameters = @ParamDef(name = Nontemporal.FILTERPARAM_LASTUPDATED_BEFORE, type = CprNontemporalRecord.FILTERPARAMTYPE_LASTUPDATED))
})

@XmlAccessorType(XmlAccessType.FIELD)
public class RoadEntity extends CprRecordEntity {

    public RoadEntity() {
    }

    public RoadEntity(Identification identification) {
        super(identification);
    }

    public RoadEntity(UUID uuid, String domain) {
        super(uuid, domain);
    }

    public static final String DB_FIELD_MUNIPALITY_CODE = "municipalityCode";
    public static final String IO_FIELD_MUNIPALITY_CODE = "kommunekode";
    @Column(name = DB_FIELD_MUNIPALITY_CODE)
    @JsonProperty(IO_FIELD_MUNIPALITY_CODE)
    private int municipalityCode;

    public static final String DB_FIELD_ROAD_CODE = "roadcode";
    public static final String IO_FIELD_ROAD_CODE = "vejkode";
    @Column(name = DB_FIELD_ROAD_CODE)
    @JsonProperty(IO_FIELD_ROAD_CODE)
    private int roadcode;

    public static final String DB_FIELD_ADDRESS_NAME_CODE = "addressname";
    public static final String IO_FIELD_ADDRESS_NAME_CODE = "adressenavn";
    @OneToMany(mappedBy = RoadBitemporalRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    @JsonProperty(IO_FIELD_ADDRESS_NAME_CODE)
    Set<RoadBitemporalRecord> name = new HashSet<>();

    public Set<RoadBitemporalRecord> getNames() {
        return this.name;
    }

    public static final String DB_FIELD_CITY_CODE = "city";
    public static final String IO_FIELD_CITY_CODE = "by";
    @OneToMany(mappedBy = RoadCityBitemporalRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    @JsonProperty(IO_FIELD_CITY_CODE)
    Set<RoadCityBitemporalRecord> city = new HashSet<>();

    public Set<RoadCityBitemporalRecord> getCitys() {
        return this.city;
    }



    public static final String DB_FIELD_MEMO_CODE = "memo";
    public static final String IO_FIELD_MEMO_CODE = "note";
    @OneToMany(mappedBy = RoadMemoBitemporalRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    @JsonProperty(IO_FIELD_MEMO_CODE)
    Set<RoadMemoBitemporalRecord> memo = new HashSet<>();

    public Set<RoadMemoBitemporalRecord> getMemos() {
        return this.memo;
    }



    public static final String DB_FIELD_POST_CODE = "postcode";
    public static final String IO_FIELD_POST_CODE = "postnr";
    @OneToMany(mappedBy = RoadPostalcodeBitemporalRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    @JsonProperty(IO_FIELD_POST_CODE)
    Set<RoadPostalcodeBitemporalRecord> postcode = new HashSet<>();

    public Set<RoadPostalcodeBitemporalRecord> getPostcodes() {
        return this.postcode;
    }



    public static UUID generateUUID(String roadNumber) {
        String uuidInput = "road:"+roadNumber;
        return UUID.nameUUIDFromBytes(uuidInput.getBytes());
    }



    @JsonIgnore
    public Set<CprBitemporalRoadRecord> getBitemporalRecords() {
        HashSet<CprBitemporalRoadRecord> records = new HashSet<>();

        return records;
    }

    @Override
    public IdentifiedEntity getNewest(Collection<IdentifiedEntity> collection) {
        return null;
    }
}

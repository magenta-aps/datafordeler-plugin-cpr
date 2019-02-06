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

import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.CascadeType;
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


    public static final String DB_FIELD_ADDRESS_CONAME = "coname";
    public static final String IO_FIELD_ADDRESS_CONAME = "conavn";
    @OneToMany(mappedBy = RoadBitemporalRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    @JsonProperty(IO_FIELD_ADDRESS_CONAME)
    Set<RoadBitemporalRecord> coname = new HashSet<>();

    public Set<RoadBitemporalRecord> getConame() {
        return this.coname;
    }

    public static final String DB_FIELD_ADDRESS_CONAME2 = "coname2";
    public static final String IO_FIELD_ADDRESS_CONAME2 = "conavn2";
    @OneToMany(mappedBy = RoadCityBitemporalRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    @JsonProperty(IO_FIELD_ADDRESS_CONAME2)
    Set<RoadCityBitemporalRecord> coname2 = new HashSet<>();

    public Set<RoadCityBitemporalRecord> getConame2() {
        return this.coname2;
    }



    public static final String DB_FIELD_ADDRESS_CONAME3 = "coname3";
    public static final String IO_FIELD_ADDRESS_CONAME3 = "conavn3";
    @OneToMany(mappedBy = RoadMemoBitemporalRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    @JsonProperty(IO_FIELD_ADDRESS_CONAME3)
    Set<RoadMemoBitemporalRecord> coname3 = new HashSet<>();

    public Set<RoadMemoBitemporalRecord> getConame3() {
        return this.coname3;
    }



    public static final String DB_FIELD_ADDRESS_CONAME4 = "coname4";
    public static final String IO_FIELD_ADDRESS_CONAME4 = "conavn4";
    @OneToMany(mappedBy = RoadPostalcodeBitemporalRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    @JsonProperty(IO_FIELD_ADDRESS_CONAME4)
    Set<RoadPostalcodeBitemporalRecord> coname4 = new HashSet<>();

    public Set<RoadPostalcodeBitemporalRecord> getConame4() {
        return this.coname4;
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

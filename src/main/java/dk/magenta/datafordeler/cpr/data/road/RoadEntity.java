package dk.magenta.datafordeler.cpr.data.road;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dk.magenta.datafordeler.core.database.Entity;
import dk.magenta.datafordeler.core.database.Identification;

import javax.persistence.Column;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.UUID;

/**
 * Created by lars on 16-05-17.
 */
@javax.persistence.Entity
@Table(
    name="cpr_road_entity",
    indexes = {
        @Index(name = "vejKode", columnList = "vejkode"),
        @Index(name = "komKodeVejKode", columnList = "kommunekode,vejkode")
    }
)
@XmlAccessorType(XmlAccessType.FIELD)
public class RoadEntity extends Entity<RoadEntity, RoadRegistration> {

    public RoadEntity() {
    }

    public RoadEntity(Identification identification) {
        super(identification);
    }

    public RoadEntity(UUID uuid, String domain) {
        super(uuid, domain);
    }

    @Override
    protected RoadRegistration createEmptyRegistration() {
        return new RoadRegistration();
    }

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="type")
    public static final String schema = "Vej";


    @Column
    @JsonProperty(value = "kommunekode")
    @XmlElement(name = "kommunekode")
    private int kommunekode;

    public int getKommunekode() {
        return this.kommunekode;
    }

    public void setKommunekode(int kommunekode) {
        this.kommunekode = kommunekode;
    }

    @Column
    @JsonProperty(value = "vejkode")
    @XmlElement(name = "vejkode")
    private int vejkode;

    public int getVejkode() {
        return this.vejkode;
    }

    public void setVejkode(int vejkode) {
        this.vejkode = vejkode;
    }

    public static UUID generateUUID(int municipalityCode, int roadCode) {
        String uuidInput = "road:"+municipalityCode+"/"+roadCode;
        return UUID.nameUUIDFromBytes(uuidInput.getBytes());
    }
}

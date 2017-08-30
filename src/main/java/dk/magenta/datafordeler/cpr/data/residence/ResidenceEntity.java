package dk.magenta.datafordeler.cpr.data.residence;

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
@Table(name="cpr_residence_entity")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResidenceEntity extends Entity<ResidenceEntity, ResidenceRegistration> {

    public ResidenceEntity() {
    }

    public ResidenceEntity(Identification identification) {
        super(identification);
    }

    public ResidenceEntity(UUID uuid, String domain) {
        super(uuid, domain);
    }

    @Override
    protected ResidenceRegistration createEmptyRegistration() {
        return new ResidenceRegistration();
    }

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="type")
    public static final String schema = "Bolig";


    public static UUID generateUUID(int komkod, int vejkod, String husnr, String etage, String sidedoer) {
        String uuidInput = "residence:"+komkod+":"+vejkod+":"+husnr+":"+etage+":"+sidedoer;
        return UUID.nameUUIDFromBytes(uuidInput.getBytes());
    }

}

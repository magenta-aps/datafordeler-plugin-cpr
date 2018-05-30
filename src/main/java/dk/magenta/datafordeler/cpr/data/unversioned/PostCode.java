package dk.magenta.datafordeler.cpr.data.unversioned;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.cpr.CprPlugin;
import org.hibernate.Session;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.UUID;

/**
 * Nontemporal storage of postcodes, to be referenced by bitemporal data items.
 */
@Entity
@Table(name="cpr_postcode", indexes = {
        @Index(name = "cpr_postcode", columnList = "postnummer")
})
public class PostCode extends UnversionedEntity {

    @Column
    @JsonProperty(value = "postnummer")
    @XmlElement(name = "postnummer")
    private int postnummer;

    public int getPostnummer() {
        return this.postnummer;
    }

    public void setPostnummer(int postnummer) {
        this.postnummer = postnummer;
    }

    @Column
    @JsonProperty(value = "postdistrikt")
    @XmlElement(name = "postdistrikt")
    private String postdistrikt;

    public String getPostdistrikt() {
        return this.postdistrikt;
    }

    public void setPostdistrikt(String postdistrikt) {
        this.postdistrikt = postdistrikt;
    }

    public static String getDomain() {
        return "https://data.gl/cpr/postcode/1/rest/";
    }

    public static PostCode getPostcode(int code, String text, Session session) {
        PostCode postcode = QueryManager.getItem(session, PostCode.class, Collections.singletonMap("postnummer", code));
        if (postcode == null) {
            postcode = new PostCode();
            postcode.setPostnummer(code);
            postcode.setPostdistrikt(text);
            postcode.setIdentification(
                    QueryManager.getOrCreateIdentification(session, generateUUID(code), PostCode.getDomain())
            );
            session.save(postcode);
        }
        return postcode;
    }

    public static UUID generateUUID(int postnummer) {
        String uuidInput = "postcode:" + postnummer;
        return UUID.nameUUIDFromBytes(uuidInput.getBytes());
    }

}

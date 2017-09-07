package dk.magenta.datafordeler.cpr.data.unversioned;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.core.database.QueryManager;
import org.hibernate.Session;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;

/**
 * Created by lars on 29-06-17.
 */
@Entity
@Table(name="cpr_postcode")
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

    public static PostCode getPostcode(int code, String text, QueryManager queryManager, Session session) {
        PostCode postcode = queryManager.getItem(session, PostCode.class, Collections.singletonMap("postnummer", code));
        if (postcode == null) {
            postcode = new PostCode();
            postcode.setPostnummer(code);
            postcode.setPostdistrikt(text);
            session.save(postcode);
        }
        return postcode;
    }

}

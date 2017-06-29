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
    private int code;

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Column
    @JsonProperty(value = "postdistrikt")
    @XmlElement(name = "postdistrikt")
    private String text;

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static PostCode getPostcode(int code, String text, QueryManager queryManager, Session session) {
        PostCode postcode = queryManager.getItem(session, PostCode.class, Collections.singletonMap("code", code));
        if (postcode == null) {
            postcode = new PostCode();
            postcode.setCode(code);
            postcode.setText(text);
            session.save(postcode);
        }
        return postcode;
    }

}

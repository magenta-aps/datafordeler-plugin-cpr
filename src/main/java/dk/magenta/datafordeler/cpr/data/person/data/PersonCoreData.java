package dk.magenta.datafordeler.cpr.data.person.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
 * Created by lars on 21-06-17.
 */
@Entity
@Table(name = "cpr_person_core")
public class PersonCoreData extends AuthorityDetailData {

    public enum Koen {
        MAND,
        KVINDE
    }

    @Column
    @JsonProperty(value = "personnummer")
    @XmlElement(name = "personnummer")
    private String personnummer;

    public String getPersonnummer() {
        return this.personnummer;
    }

    public void setPersonnummer(String personnummer) {
        this.personnummer = personnummer;
    }



    @Column
    @JsonProperty(value = "koen")
    @XmlElement(name = "koen")
    private Koen koen;

    public Koen getKoen() {
        return this.koen;
    }

    public void setKoen(Koen koen) {
        this.koen = koen;
    }

    public void setKoen(String koen) {
        if (koen != null) {
            if (koen.equalsIgnoreCase("M")) {
                this.setKoen(Koen.MAND);
            } else if (koen.equalsIgnoreCase("K")) {
                this.setKoen(Koen.KVINDE);
            }
        }
    }


    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>(super.asMap());
        if (StringUtils.isNotEmpty(personnummer)) {
            map.put("personnummer", this.personnummer);
        }
        if (this.koen != null) {
            map.put("koen", this.koen);
        }
        return map;
    }
}

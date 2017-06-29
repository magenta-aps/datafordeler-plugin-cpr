package dk.magenta.datafordeler.cpr.data.person.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.data.DetailData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 22-06-17.
 */
@Entity
@Table(name = "cpr_person_address")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PersonMoveMunicipalityData extends AuthorityDetailData {


    @Column
    @JsonProperty(value = "tilflytningsdato")
    @XmlElement(name = "tilflytningsdato")
    private LocalDateTime moveToDate;

    public LocalDateTime getMoveToDate() {
        return this.moveToDate;
    }

    public void setMoveToDate(LocalDateTime moveToDate) {
        this.moveToDate = moveToDate;
    }



    @Column
    @JsonProperty(value = "tilflytningsdatoUsikker")
    @XmlElement(name = "tilflytningsdatoUsikker")
    private boolean moveToDateUncertain;

    public boolean isMoveToDateUncertain() {
        return this.moveToDateUncertain;
    }

    public void setMoveToDateUncertain(boolean moveToDateUncertain) {
        this.moveToDateUncertain = moveToDateUncertain;
    }



    @Column
    @JsonProperty(value = "fraflytningskommune")
    @XmlElement(name = "fraflytningskommune")
    private int moveFromMunicipality;

    public int getMoveFromMunicipality() {
        return this.moveFromMunicipality;
    }

    public void setMoveFromMunicipality(int moveFromMunicipality) {
        this.moveFromMunicipality = moveFromMunicipality;
    }



    @Column
    @JsonProperty(value = "fraflytningsdato")
    @XmlElement(name = "fraflytningsdato")
    private LocalDateTime moveFromDate;

    public LocalDateTime getMoveFromDate() {
        return this.moveFromDate;
    }

    public void setMoveFromDate(LocalDateTime moveFromDate) {
        this.moveFromDate = moveFromDate;
    }



    @Column
    @JsonProperty(value = "fraflytningsdatoUsikker")
    @XmlElement(name = "fraflytningsdatoUsikker")
    private boolean moveFromDateUncertain;

    public boolean isMoveFromDateUncertain() {
        return this.moveFromDateUncertain;
    }

    public void setMoveFromDateUncertain(boolean moveFromDateUncertain) {
        this.moveFromDateUncertain = moveFromDateUncertain;
    }



    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>(super.asMap());
        map.put("moveToDate", this.moveToDate);
        map.put("moveToDateUncertain", this.moveToDateUncertain);
        map.put("moveFromMunicipality", this.moveFromMunicipality);
        map.put("moveFromDate", this.moveFromDate);
        map.put("moveFromDateUncertain", this.moveFromDateUncertain);
        return map;
    }
}

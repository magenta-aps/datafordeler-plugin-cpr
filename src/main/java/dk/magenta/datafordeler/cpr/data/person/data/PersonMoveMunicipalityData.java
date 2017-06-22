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
public class PersonMoveMunicipalityData extends DetailData {


    @Column
    @JsonProperty
    @XmlElement
    private int authority;

    public int getAuthority() {
        return this.authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }



    @Column
    @JsonProperty
    @XmlElement
    private LocalDateTime moveToDate;

    public LocalDateTime getMoveToDate() {
        return this.moveToDate;
    }

    public void setMoveToDate(LocalDateTime moveToDate) {
        this.moveToDate = moveToDate;
    }



    @Column
    @JsonProperty
    @XmlElement
    private boolean moveToDateUncertain;

    public boolean isMoveToDateUncertain() {
        return this.moveToDateUncertain;
    }

    public void setMoveToDateUncertain(boolean moveToDateUncertain) {
        this.moveToDateUncertain = moveToDateUncertain;
    }



    @Column
    @JsonProperty
    @XmlElement
    private int moveFromMunicipality;

    public int getMoveFromMunicipality() {
        return this.moveFromMunicipality;
    }

    public void setMoveFromMunicipality(int moveFromMunicipality) {
        this.moveFromMunicipality = moveFromMunicipality;
    }



    @Column
    @JsonProperty
    @XmlElement
    private LocalDateTime moveFromDate;

    public LocalDateTime getMoveFromDate() {
        return this.moveFromDate;
    }

    public void setMoveFromDate(LocalDateTime moveFromDate) {
        this.moveFromDate = moveFromDate;
    }



    @Column
    @JsonProperty
    @XmlElement
    private boolean moveFromDateUncertain;

    public boolean isMoveFromDateUncertain() {
        return this.moveFromDateUncertain;
    }

    public void setMoveFromDateUncertain(boolean moveFromDateUncertain) {
        this.moveFromDateUncertain = moveFromDateUncertain;
    }



    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("authority", this.authority);
        map.put("moveToDate", this.moveToDate);
        map.put("moveToDateUncertain", this.moveToDateUncertain);
        map.put("moveFromMunicipality", this.moveFromMunicipality);
        map.put("moveFromDate", this.moveFromDate);
        map.put("moveFromDateUncertain", this.moveFromDateUncertain);
        return map;
    }
}

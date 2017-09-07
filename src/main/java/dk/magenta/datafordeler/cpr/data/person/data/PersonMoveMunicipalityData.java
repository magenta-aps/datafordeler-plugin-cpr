package dk.magenta.datafordeler.cpr.data.person.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonProperty(value = "fraflytningsdatoKommune")
    @XmlElement(name = "fraflytningsdatoKommune")
    private LocalDateTime fraflytningsdatoKommune;

    public LocalDateTime getFraflytningsdatoKommune() {
        return this.fraflytningsdatoKommune;
    }

    public void setFraflytningsdatoKommune(LocalDateTime fraflytningsdatoKommune) {
        this.fraflytningsdatoKommune = fraflytningsdatoKommune;
    }



    @Column
    @JsonProperty(value = "fraflytningsdatoKommuneUsikkerhedsmarkering")
    @XmlElement(name = "fraflytningsdatoKommuneUsikkerhedsmarkering")
    private boolean fraflytningsdatoKommuneUsikkerhedsmarkering;

    public boolean isFraflytningsdatoKommuneUsikkerhedsmarkering() {
        return this.fraflytningsdatoKommuneUsikkerhedsmarkering;
    }

    public void setFraflytningsdatoKommuneUsikkerhedsmarkering(boolean fraflytningsdatoKommuneUsikkerhedsmarkering) {
        this.fraflytningsdatoKommuneUsikkerhedsmarkering = fraflytningsdatoKommuneUsikkerhedsmarkering;
    }



    @Column
    @JsonProperty(value = "fraflytningskommunekode")
    @XmlElement(name = "fraflytningskommunekode")
    private int fraflytningskommunekode;

    public int getFraflytningskommunekode() {
        return this.fraflytningskommunekode;
    }

    public void setFraflytningskommunekode(int fraflytningskommunekode) {
        this.fraflytningskommunekode = fraflytningskommunekode;
    }


    @Column
    @JsonProperty(value = "tilflytningsdatoKommune")
    @XmlElement(name = "tilflytningsdatoKommune")
    private LocalDateTime tilflytningsdatoKommune;

    public LocalDateTime getTilflytningsdatoKommune() {
        return this.tilflytningsdatoKommune;
    }

    public void setTilflytningsdatoKommune(LocalDateTime tilflytningsdatoKommune) {
        this.tilflytningsdatoKommune = tilflytningsdatoKommune;
    }



    @Column
    @JsonProperty(value = "tilflytningsdatoKommuneUsikkerhedsmarkering")
    @XmlElement(name = "tilflytningsdatoKommuneUsikkerhedsmarkering")
    private boolean tilflytningsdatoKommuneUsikkerhedsmarkering;

    public boolean isTilflytningsdatoKommuneUsikkerhedsmarkering() {
        return this.tilflytningsdatoKommuneUsikkerhedsmarkering;
    }

    public void setTilflytningsdatoKommuneUsikkerhedsmarkering(boolean tilflytningsdatoKommuneUsikkerhedsmarkering) {
        this.tilflytningsdatoKommuneUsikkerhedsmarkering = tilflytningsdatoKommuneUsikkerhedsmarkering;
    }



    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>(super.asMap());
        map.put("fraflytningsdatoKommune", this.fraflytningsdatoKommune);
        map.put("fraflytningsdatoKommuneUsikkerhedsmarkering", this.fraflytningsdatoKommuneUsikkerhedsmarkering);
        map.put("fraflytningskommunekode", this.fraflytningskommunekode);
        map.put("tilflytningsdatoKommune", this.tilflytningsdatoKommune);
        map.put("tilflytningsdatoKommuneUsikkerhedsmarkering", this.tilflytningsdatoKommuneUsikkerhedsmarkering);
        return map;
    }
}

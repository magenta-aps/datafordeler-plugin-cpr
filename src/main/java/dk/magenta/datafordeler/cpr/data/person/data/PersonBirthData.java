package dk.magenta.datafordeler.cpr.data.person.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.data.DetailData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 21-06-17.
 */
@Entity
@Table(name = "cpr_person_birth")
public class PersonBirthData extends DetailData {

    @Column
    @JsonProperty(value = "cprFoedselsregistreringsstedskode")
    @XmlElement(name = "cprFoedselsregistreringsstedskode")
    private String cprFoedselsregistreringsstedskode;

    public String getCprFoedselsregistreringsstedskode() {
        return this.cprFoedselsregistreringsstedskode;
    }

    public void setCprFoedselsregistreringsstedskode(String cprFoedselsregistreringsstedskode) {
        this.cprFoedselsregistreringsstedskode = cprFoedselsregistreringsstedskode;
    }



    @Column
    @JsonProperty(value = "cprFoedselsregistreringsstedsnavn")
    @XmlElement(name = "cprFoedselsregistreringsstedsnavn")
    private String cprFoedselsregistreringsstedsnavn;

    public String getCprFoedselsregistreringsstedsnavn() {
        return this.cprFoedselsregistreringsstedsnavn;
    }

    public void setCprFoedselsregistreringsstedsnavn(String cprFoedselsregistreringsstedsnavn) {
        this.cprFoedselsregistreringsstedsnavn = cprFoedselsregistreringsstedsnavn;
    }



    @Column
    @JsonProperty(value = "foedselsdato")
    @XmlElement(name = "foedselsdato")
    private LocalDateTime foedselsdato;

    public LocalDateTime getFoedselsdato() {
        return this.foedselsdato;
    }

    public void setFoedselsdato(LocalDateTime foedselsdato) {
        this.foedselsdato = foedselsdato;
    }



    @Column
    @JsonProperty(value = "foedselsdatoUsikkerhedsmarkering")
    @XmlElement(name = "foedselsdatoUsikkerhedsmarkering")
    private boolean foedselsdatoUsikkerhedsmarkering;

    public boolean isFoedselsdatoUsikkerhedsmarkering() {
        return this.foedselsdatoUsikkerhedsmarkering;
    }

    public void setFoedselsdatoUsikkerhedsmarkering(boolean foedselsdatoUsikkerhedsmarkering) {
        this.foedselsdatoUsikkerhedsmarkering = foedselsdatoUsikkerhedsmarkering;
    }



    //Ikke i grunddatamodellen

    @Transient
    private int foedselsraekkefoelge;

    public int getFoedselsraekkefoelge() {
        return this.foedselsraekkefoelge;
    }

    public void setFoedselsraekkefoelge(int foedselsraekkefoelge) {
        this.foedselsraekkefoelge = foedselsraekkefoelge;
    }



    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        //Person
        map.put("cprFoedselsregistreringsstedskode", this.cprFoedselsregistreringsstedskode);
        map.put("cprFoedselsregistreringsstedsnavn", this.cprFoedselsregistreringsstedsnavn);
        map.put("foedselsdato", this.foedselsdato);
        map.put("foedselsdatoUsikkerhedsmarkering", this.foedselsdatoUsikkerhedsmarkering);

        //Ikke i grunddatamodellen
        map.put("foedselsraekkefoelge", this.foedselsraekkefoelge);
        return map;
    }
}

package dk.magenta.datafordeler.cpr.data.person.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 21-06-17.
 */
@Entity
@Table(name = "cpr_person_parent")
public class PersonParentData extends AuthorityDetailData {

    @Column
    @JsonIgnore
    @XmlTransient
    private boolean isMor;

    @JsonIgnore
    @XmlTransient
    public boolean isMor() {
        return this.isMor;
    }

    public void setMor(boolean mor) {
        isMor = mor;
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
    @JsonProperty(value = "foedselsdato")
    @XmlElement(name = "foedselsdato")
    private LocalDate foedselsdato;

    public LocalDate getFoedselsdato() {
        return this.foedselsdato;
    }

    public void setFoedselsdato(LocalDate foedselsdato) {
        this.foedselsdato = foedselsdato;
    }



    @Column
    @JsonProperty(value = "foedselsdatoUsikker")
    @XmlElement(name = "foedselsdatoUsikker")
    private boolean foedselsdatoUsikker;

    public boolean isFoedselsdatoUsikker() {
        return this.foedselsdatoUsikker;
    }

    public void setFoedselsdatoUsikker(boolean foedselsdatoUsikker) {
        this.foedselsdatoUsikker = foedselsdatoUsikker;
    }



    @Column
    @JsonProperty(value = "navn")
    @XmlElement(name = "navn")
    private String navn;

    public String getNavn() {
        return this.navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }



    @Column
    @JsonProperty(value = "navneMarkering")
    @XmlElement(name = "navneMarkering")
    private boolean navneMarkering;

    public boolean isNavneMarkering() {
        return this.navneMarkering;
    }

    public void setNavneMarkering(boolean navneMarkering) {
        this.navneMarkering = navneMarkering;
    }



    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>(super.asMap());
        map.put("personnummer", this.personnummer);
        map.put("foedselsdato", this.foedselsdato);
        map.put("foedselsdatoUsikker", this.foedselsdatoUsikker);
        map.put("name", this.navn);
        map.put("navneMarkering", this.navneMarkering);
        return map;
    }
}

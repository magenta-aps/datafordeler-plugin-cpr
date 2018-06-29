package dk.magenta.datafordeler.cpr.records.person;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.records.CprBitemporalRecord;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlElement;


@MappedSuperclass
public abstract class AuthorityTextDataRecord extends CprBitemporalPersonRecord {

    public AuthorityTextDataRecord(String text, String correctionMarking) {
        this.text = text;
        this.correctionMarking = correctionMarking;
    }

    public static final String DB_FIELD_TEXT = "text";
    public static final String IO_FIELD_TEXT = "tekst";
    @Column(name = DB_FIELD_TEXT)
    @JsonProperty(value = IO_FIELD_TEXT)
    @XmlElement(name = IO_FIELD_TEXT)
    private String text;

    protected AuthorityTextDataRecord() {
    }


    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public static final String DB_FIELD_CORRECTION_MARKING = "correctionMarking";
    public static final String IO_FIELD_CORRECTION_MARKING = "retFortrydMarkering";
    @Column(name = DB_FIELD_CORRECTION_MARKING, length = 1)
    @JsonProperty(value = IO_FIELD_CORRECTION_MARKING)
    @XmlElement(name = IO_FIELD_CORRECTION_MARKING)
    private String correctionMarking;

    public String getCorrectionMarking() {
        return this.correctionMarking;
    }

    public void setCorrectionMarking(String correctionMarking) {
        this.correctionMarking = correctionMarking;
    }

    protected static void copy(AuthorityTextDataRecord from, AuthorityTextDataRecord to) {
        CprBitemporalRecord.copy(from, to);
        to.text = from.text;
        to.correctionMarking = from.correctionMarking;
    }

}

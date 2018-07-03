package dk.magenta.datafordeler.cpr.records;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.core.database.DatabaseEntry;
import dk.magenta.datafordeler.cpr.records.person.data.ChurchDataRecord;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlElement;
import java.time.OffsetDateTime;
import java.util.Objects;

@MappedSuperclass
public class CprAuthorityRecord extends DatabaseEntry {

    public static final String DB_FIELD_AUTHORITY = "authority";
    public static final String IO_FIELD_AUTHORITY = "myndighed";
    @Column(name = DB_FIELD_AUTHORITY)
    @JsonProperty(value = IO_FIELD_AUTHORITY)
    @XmlElement(name = IO_FIELD_AUTHORITY)
    private int authority;

    public int getAuthority() {
        return this.authority;
    }

    public CprAuthorityRecord setAuthority(int authority) {
        if (authority != 0 || this.authority == 0) {
            this.authority = authority;
        }
        return this;
    }



    public static final String DB_FIELD_UPDATED = "dafoUpdated";
    public static final String IO_FIELD_UPDATED = "opdateret";
    @Column(name = DB_FIELD_UPDATED)
    @JsonProperty(value = IO_FIELD_UPDATED)
    @XmlElement(name = IO_FIELD_UPDATED)
    public OffsetDateTime dafoUpdated;

    public OffsetDateTime getDafoUpdated() {
        return this.dafoUpdated;
    }

    public CprAuthorityRecord setDafoUpdated(OffsetDateTime dafoUpdated) {
        this.dafoUpdated = dafoUpdated;
        return this;
    }

    protected static void copy(CprAuthorityRecord from, CprAuthorityRecord to) {
        to.authority = from.authority;
        to.dafoUpdated = from.dafoUpdated;
    }

    public boolean equalData(Object o) {
        if (o==null || (getClass() != o.getClass())) return false;
        CprAuthorityRecord that = (CprAuthorityRecord) o;
        return Objects.equals(this.authority, that.authority);
    }
}

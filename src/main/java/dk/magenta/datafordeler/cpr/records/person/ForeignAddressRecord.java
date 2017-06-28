package dk.magenta.datafordeler.cpr.records.person;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;

import java.util.HashSet;

/**
 * Created by lars on 27-06-17.
 */
public class ForeignAddressRecord extends PersonDataRecord<PersonBaseData> {

    public ForeignAddressRecord(String line) throws ParseException {
        super(line);
        this.obtain("start_mynkod-udrindrejse", 14, 4);
        this.obtain("udr_ts", 18, 12);
        this.obtain("udr_landekod", 30, 4);
        this.obtain("udrdto", 34, 12);
        this.obtain("udrdto_umrk", 46, 1);
        this.obtain("udlandadr_mynkod", 47, 4);
        this.obtain("udlandadr_ts", 51, 12);
        this.obtain("udlandadr1", 63, 34);
        this.obtain("udlandadr2", 97, 34);
        this.obtain("udlandadr3", 131, 34);
        this.obtain("udlandadr4", 165, 34);
        this.obtain("udlandadr5", 199, 34);
    }

    @Override
    protected PersonBaseData createEmptyBaseData() {
        return new PersonBaseData();
    }

    @Override
    public ListHashMap<PersonEffect, PersonBaseData> getDataEffects(String registrationFrom) {
        ListHashMap<PersonEffect, PersonBaseData> data = new ListHashMap<>();
        PersonBaseData personBaseData;
        if (registrationFrom.equals(this.get("udr_ts"))) {
            personBaseData = this.getBaseDataItem(data, this.getOffsetDateTime("udrdto"), this.getMarking("udrdto_umrk"));
            personBaseData.setEmigration(this.getInt("start_mynkod-udrindrejs"), this.getInt("udr_landekod"));
        }
        if (registrationFrom.equals(this.get("udlandadr_ts"))) {
            personBaseData = this.getBaseDataItem(data, this.getOffsetDateTime("udrdto"), this.getMarking("udrdto_umrk"));
            personBaseData.setForeignAddress(this.getInt("udlandadr_mynkod"), this.get("udlandadr1"), this.get("udlandadr2"), this.get("udlandadr3"), this.get("udlandadr4"), this.get("udlandadr5"));
        }
        return data;
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_FOREIGN_ADDRESS;
    }

    @Override
    public HashSet<String> getTimestamps() {
        HashSet<String> timestamps = super.getTimestamps();
        timestamps.add(this.get("udr_ts"));
        timestamps.add(this.get("udlandadr_ts"));
        return timestamps;
    }
}

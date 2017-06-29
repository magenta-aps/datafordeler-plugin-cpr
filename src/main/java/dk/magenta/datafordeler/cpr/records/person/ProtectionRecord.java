package dk.magenta.datafordeler.cpr.records.person;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by lars on 27-06-17.
 */
public class ProtectionRecord extends PersonDataRecord {

    public ProtectionRecord(String line) throws ParseException {
        super(line);
        this.obtain("beskyttype",14,4);
        this.obtain("start_mynkod-beskyttelse",18,4);
        this.obtain("start_ts-beskyttelse",22,12	);
        this.obtain("start_dt-beskyttelse",34,10	);
        this.obtain("indrap-beskyttelse",44	,3);
        this.obtain("slet_dt-beskyttelse",47,10);
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_PROTECTION;
    }

    @Override
    public void getDataEffects(HashMap<PersonEffect, PersonBaseData> data, String registrationFrom) {
        PersonBaseData personBaseData;
        if (registrationFrom.equals(this.get("start_ts-beskyttelse"))) {
            personBaseData = this.getBaseDataItem(data, this.getOffsetDateTime("start_dt-beskyttelse"), false, this.getOffsetDateTime("slet_dt-beskyttelse"), false);
            personBaseData.setProtection(
                    this.getInt("start_mynkod-beskyttelse"),
                    this.getInt("beskyttype"),
                    this.getBoolean("indrap-beskyttelse")
            );
        }
    }

    @Override
    protected PersonBaseData createEmptyBaseData() {
        return new PersonBaseData();
    }

    @Override
    public HashSet<String> getTimestamps() {
        HashSet<String> timestamps = super.getTimestamps();
        timestamps.add(this.get("start_ts-beskyttelse"));
        return timestamps;
    }
}

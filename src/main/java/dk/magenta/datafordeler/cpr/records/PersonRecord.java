package dk.magenta.datafordeler.cpr.records;

import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.HashSet;

/**
 * Created by lars on 22-06-17.
 */
public class PersonRecord extends PersonDataRecord<PersonBaseData> {
    public PersonRecord(String line) throws ParseException {
        super(line);
        this.obtain("pnrgaeld", 14, 10);
        this.obtain("status_ts", 24, 12);
        this.obtain("status", 36, 2);
        this.obtain("statushaenstart", 38, 12);
        this.obtain("statusdto_umrk", 50, 1);
        this.obtain("start_mynkod-person", 51, 4);
        this.obtain("start_ts-person", 55, 12);
        this.obtain("koen", 67, 1);
        this.obtain("foed_dt", 68, 10);
        this.obtain("foed_dt_umrk", 78, 1);
        this.obtain("foed_tm", 79, 8);
        this.obtain("foedsekvens", 87, 4);
        this.obtain("start_dt-person", 91, 10);
        this.obtain("start_dt_umrk-person", 101, 1);
        this.obtain("slut_dt-person", 102, 10);
        this.obtain("slut_dt_umrk-person", 112, 1);
        this.obtain("stilling_mynkod", 113, 4);
        this.obtain("stilling_ts", 117, 12);
        this.obtain("stilling", 129, 34);
        this.obtain("mor_ts", 163, 12);
        this.obtain("mor_mynkod", 175, 4);
        this.obtain("mor_dt", 179, 10);
        this.obtain("mor_dt_umrk", 189, 1);
        this.obtain("pnrmor", 190, 10);
        this.obtain("mor_foed_dt", 200, 10);
        this.obtain("mor_foed_dt_umrk", 210, 1);
        this.obtain("mornvn", 211, 34);
        this.obtain("mornvn_mrk", 245, 1);
        this.obtain("mor_dok_mynkod", 246, 4);
        this.obtain("mor_dok_ts", 250, 12);
        this.obtain("mor_dok", 262, 3);
        this.obtain("far_ts", 265, 12);
        this.obtain("far_mynkod", 277, 4);
        this.obtain("far_dt", 281, 10);
        this.obtain("far_dt_umrk", 291, 1);
        this.obtain("pnrfar", 292, 10);
        this.obtain("far_foed_dt", 302, 10);
        this.obtain("far_foed_dt_umrk", 312, 1);
        this.obtain("farnvn", 313, 34);
        this.obtain("farnvn_mrk", 347, 1);
        this.obtain("far_dok_mynkod", 348, 4);
        this.obtain("far_dok_ts", 352, 12);
        this.obtain("far_dok", 364, 3);
    }

    /**
     * Create a set of populated PersonBaseData objects, each with its own unique effect period
     *
     * @param registrationFrom
     * @return
     */
    @Override
    public ListHashMap<PersonEffect, PersonBaseData> getDataEffects(String registrationFrom) {
        ListHashMap<PersonEffect, PersonBaseData> data = new ListHashMap<>();
        PersonBaseData personBaseData;

        if (registrationFrom.equals(this.get("status_ts"))) {
            personBaseData = this.getBaseDataItem(data, this.getOffsetDateTime("statushaenstart"), this.getBoolean("statusdto_umrk"));
            personBaseData.setStatus(this.get("status"));
        }

        if (registrationFrom.equals(this.get("mor_ts"))) {
            personBaseData = this.getBaseDataItem(data, this.getOffsetDateTime("mor_dt"), this.getBoolean("mor_dt_umrk"));
            personBaseData.setMother(
                    this.get("mornvn"),
                    this.getBoolean("mornvn_mrk"),
                    this.getInt("pnrmor"),
                    this.getDate("mor_foed_dt"),
                    this.getBoolean("mor_foed_dt_umrk"),
                    this.getInt("mor_mynkod")
            );
        }

        if (registrationFrom.equals(this.get("far_ts"))) {
            personBaseData = this.getBaseDataItem(data, this.getOffsetDateTime("far_dt"), this.getBoolean("far_dt_umrk"));
            personBaseData.setFather(
                    this.get("farnvn"),
                    this.getBoolean("farnvn_mrk"),
                    this.getInt("pnrfar"),
                    this.getDate("far_foed_dt"),
                    this.getBoolean("far_foed_dt_umrk"),
                    this.getInt("far_mynkod")
            );
        }

        if (registrationFrom.equals(this.get("mor_dok_ts"))) {
            personBaseData = this.getBaseDataItem(data);
            personBaseData.setMotherVerification(
                    this.getInt("mor_dok_mynkod"),
                    this.getBoolean("mor_dok")
            );
        }

        if (registrationFrom.equals(this.get("far_dok_ts"))) {
            personBaseData = this.getBaseDataItem(data);
            personBaseData.setFatherVerification(
                    this.getInt("far_dok_mynkod"),
                    this.getBoolean("far_dok")
            );
        }

        if (registrationFrom.equals(this.get("stilling_ts"))) {
            personBaseData = this.getBaseDataItem(data);
            personBaseData.setPosition(
                    this.getInt("stilling_mynkod"),
                    this.get("stilling")
            );
        }

        if (registrationFrom.equals(this.get("start_ts-person"))) {

            personBaseData = this.getBaseDataItem(data, this.getOffsetDateTime("start_dt-person"), this.getBoolean("start_dt_umrk-person"), this.getOffsetDateTime("slut_dt-person"), this.getBoolean("slut_dt_umrk-person"));
            personBaseData.setBirth(
                    LocalDateTime.of(this.getDate("foed_dt"), this.getTime("foed_tm")),
                    this.getBoolean("foed_dt_umrk"),
                    this.getInt("foedsekvens")
            );
            personBaseData.setCurrentCprNumber(this.getInt("pnrgaeld"));
            personBaseData.setGender(this.get("koen"));
            personBaseData.setStartAuthority(this.getInt("start_mynkod-person"));
        }

        return data;
    }

    @Override
    protected PersonBaseData createEmptyBaseData() {
        return new PersonBaseData();
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_PERSON;
    }

    @Override
    public HashSet<String> getTimestamps() {
        HashSet<String> timestamps = super.getTimestamps();
        timestamps.add(this.get("status_ts"));
        timestamps.add(this.get("mor_ts"));
        timestamps.add(this.get("stilling_ts"));
        timestamps.add(this.get("far_ts"));
        timestamps.add(this.get("mor_dok_ts"));
        timestamps.add(this.get("far_dok_ts"));
        return timestamps;
    }
}

package dk.magenta.datafordeler.cpr.records;

import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;

import java.text.ParseException;
import java.util.HashSet;

/**
 * Created by lars on 22-06-17.
 */
public class AddressRecord extends PersonDataRecord<PersonBaseData> {
    public AddressRecord(String line) throws ParseException {
        super(line);
        this.obtain("start_mynkod-personbolig", 14, 4);
        this.obtain("adr_ts", 18, 12);
        this.obtain("komkod", 30, 4);
        this.obtain("vejkod", 34, 4);
        this.obtain("husnr", 38, 4);
        this.obtain("etage", 42, 2);
        this.obtain("sidedoer", 44, 4);
        this.obtain("bnr", 48, 4);
        this.obtain("convn", 52, 34);
        this.obtain("convn_ts", 86, 12);
        this.obtain("tilflydto", 98, 12);
        this.obtain("tilflydto_umrk", 110, 1);
        this.obtain("tilfra_mynkod", 111, 4);
        this.obtain("tilfra_ts", 115, 12);
        this.obtain("tilflykomdto", 127, 12);
        this.obtain("tilflykomdt_umrk", 139, 1);
        this.obtain("fraflykomkod", 140, 4);
        this.obtain("fraflykomdto", 144, 12);
        this.obtain("fraflykomdt_umrk", 156, 1);
        this.obtain("adrtxttype", 157, 4);
        this.obtain("start_mynkod-adrtxt", 161, 4);
        this.obtain("adr1-supladr", 165, 34);
        this.obtain("adr2-supladr", 199, 34);
        this.obtain("adr3-supladr", 233, 34);
        this.obtain("adr4-supladr", 267, 34);
        this.obtain("adr5-supladr", 301, 34);
        this.obtain("start_dt-adrtxt", 335, 10);
        this.obtain("slet_dt-adrtxt", 345, 10);
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_DOMESTIC_ADDRESS;
    }

    @Override
    protected PersonBaseData createEmptyBaseData() {
        return new PersonBaseData();
    }

    @Override
    public ListHashMap<PersonEffect, PersonBaseData> getDataEffects(String registrationFrom) {
        ListHashMap<PersonEffect, PersonBaseData> data = new ListHashMap<>();
        PersonBaseData personBaseData;
        if (registrationFrom.equals(this.get("adr_ts"))) {
            personBaseData = this.getBaseDataItem(data, this.getOffsetDateTime("tilflydto"), this.getBoolean("tilflydto_umrk"));
            personBaseData.setAddress(
                    this.getInt("start_mynkod-personbolig"),
                    this.getInt("komkod"),
                    this.getInt("vejkod"),
                    this.get("husnr"),
                    this.get("etage"),
                    this.get("sidedoer"),
                    this.get("bnr"),
                    this.getInt("adrtxttype"),
                    this.getInt("start_mynkod-adrtxt"),
                    this.get("adr1-supladr"),
                    this.get("adr2-supladr"),
                    this.get("adr3-supladr"),
                    this.get("adr4-supladr"),
                    this.get("adr5-supladr")
            );
        }
        if (registrationFrom.equals(this.get("convn_ts"))) {
            personBaseData = this.getBaseDataItem(data, null, false);
            personBaseData.setCoName(this.get("convn"));
        }
        if (registrationFrom.equals(this.get("tilfra_ts"))) {
            personBaseData = this.getBaseDataItem(data, null, false);
            personBaseData.setMoveMunicipality(
                    this.getInt("tilfra_mynkod"),
                    this.getDateTime("tilflykomdto"),
                    this.getBoolean("tilflykomdt_umrk"),
                    this.getInt("fraflykomkod"),
                    this.getDateTime("fraflykomdto"),
                    this.getBoolean("fraflykomdt_umrk")
            );
        }
        return data;
    }

    @Override
    public HashSet<String> getTimestamps() {
        HashSet<String> timestamps = super.getTimestamps();
        timestamps.add(this.get("adr_ts"));
        timestamps.add(this.get("convn_ts"));
        timestamps.add(this.get("tilfra_ts"));
        return timestamps;
    }
}

package dk.magenta.datafordeler.cpr.records;

import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;

import java.text.ParseException;
import java.util.HashSet;

/**
 * Created by lars on 22-06-17.
 */
public class NameData extends PersonDataRecord<PersonBaseData> {
    public NameData(String line) throws ParseException {
        super(line);
        this.obtain("start_mynkod-navne", 14, 4);
        this.obtain("nvn_ts", 18, 12);
        this.obtain("fornvn", 30, 50);
        this.obtain("fornvn_mrk", 80, 1);
        this.obtain("melnvn", 81, 40);
        this.obtain("melnvn_mrk", 121, 1);
        this.obtain("efternvn", 122, 40);
        this.obtain("efternvn_mrk", 162, 1);
        this.obtain("slægtnvn", 163, 40);
        this.obtain("slægtnvn_mrk", 203, 1);
        this.obtain("nvnhaenstart", 204, 12);
        this.obtain("haenstart_umrk-navne", 216, 1);
        this.obtain("adrnvn_mynkod", 217, 4);
        this.obtain("adrnvn_ts", 221, 12);
        this.obtain("adrnvn", 233, 34);
        this.obtain("indrap-navne", 267, 3);
        this.obtain("dok_mynkod-navne", 270, 4);
        this.obtain("dok_ts-navne", 274, 12);
        this.obtain("dok-navne", 286, 3);
        this.obtain("myntxt_mynkod-navne", 289, 4);
        this.obtain("myntxt_ts-navne", 293, 12);
        this.obtain("myntxt-navne", 305, 20);
    }

    @Override
    protected PersonBaseData createEmptyBaseData() {
        return new PersonBaseData();
    }

    @Override
    public ListHashMap<PersonEffect, PersonBaseData> getDataEffects(String timestamp) {
        ListHashMap<PersonEffect, PersonBaseData> data = new ListHashMap<>();
        PersonBaseData personBaseData;
        if (timestamp.equals(this.get("nvn_ts"))) {
            personBaseData = this.getBaseDataItem(data, this.getOffsetDateTime("nvnhaenstart"), this.getBoolean("haenstart_umrk-navne"));
            personBaseData.setName(
                    this.getInt("start_mynkod-navne"),
                    this.get("fornvn"),
                    this.getMarking("fornvn_mrk"),
                    this.get("melnvn"),
                    this.getMarking("melnvn_mrk"),
                    this.get("efternvn"),
                    this.getMarking("efternvn_mrk"),
                    this.get("slægtsnvn"),
                    this.getMarking("slægtsnvn_mrk"),
                    this.getBoolean("indrap-navne")
            );
        }
        if (timestamp.equals(this.get("adrnvn_ts"))) {
            personBaseData = this.getBaseDataItem(data);
            personBaseData.setAddressName(
                    this.getInt("adrnvn_mynkod"),
                    this.get("adrnvn")
            );
        }
        if (timestamp.equals(this.get("dok_ts-navne"))) {
            personBaseData = this.getBaseDataItem(data);
            personBaseData.setNameVerification(
                    this.getInt("dok_mynkod-navne"),
                    this.getBoolean("dok-navne")
            );
        }
        if (timestamp.equals(this.get("myntxt_ts-navne"))) {
            personBaseData = this.getBaseDataItem(data);
            personBaseData.setNameAuthorityText(
                    this.getInt("myntxt_mynkod-navne"),
                    this.get("myntxt-navne")
            );
        }
        return data;
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_CURRENT_NAME;
    }

    @Override
    public HashSet<String> getTimestamps() {
        HashSet<String> timestamps = super.getTimestamps();
        timestamps.add(this.get("nvn_ts"));
        timestamps.add(this.get("adrnvn_ts"));
        timestamps.add(this.get("dok_ts-navne"));
        timestamps.add(this.get("myntxt_ts-navne"));
        return timestamps;
    }
}

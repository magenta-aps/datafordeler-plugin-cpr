package dk.magenta.datafordeler.cpr.records;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;

import java.time.OffsetDateTime;
import java.util.HashSet;

/**
 * Created by lars on 22-06-17.
 */
public class HistoricAddressRecord extends PersonDataRecord<PersonBaseData> {

    public HistoricAddressRecord(String line) throws ParseException {
        super(line);
        this.obtain("annkor", 14, 1);
        this.obtain("start_mynkod-navne", 15, 4);
        this.obtain("nvn_ts", 19, 12);
        this.obtain("fornvn", 31, 50);
        this.obtain("fornvn_mrk", 81, 1);
        this.obtain("melnvn", 82, 40);
        this.obtain("melnvn_mrk", 122, 1);
        this.obtain("efternvn", 123, 40);
        this.obtain("efternvn_mrk", 163, 1);
        this.obtain("slægtnvn", 164, 40);
        this.obtain("slægtnvn_mrk", 204, 1);
        this.obtain("nvnhaenstart", 205, 12);
        this.obtain("haenstart_umrk-navne", 217, 1);
        this.obtain("nvnhaenslut", 218, 12);
        this.obtain("haenslut_umrk-navne", 230, 1);
        this.obtain("dok_mynkod-navne", 231, 4);
        this.obtain("dok_ts-navne", 235, 12);
        this.obtain("dok-navne", 247, 3);
        this.obtain("myntxt_mynkod-navne", 250, 4);
        this.obtain("myntxt_ts-navne", 254, 12);
        this.obtain("myntxt-navne", 266, 20);
    }


    @Override
    protected PersonBaseData createEmptyBaseData() {
        return new PersonBaseData();
    }

    @Override
    public ListHashMap<PersonEffect, PersonBaseData> getDataEffects(String registrationFrom) {
        ListHashMap<PersonEffect, PersonBaseData> data = new ListHashMap<>();
        PersonBaseData personBaseData;

        OffsetDateTime effectFrom = this.getOffsetDateTime("nvnhaenstart");
        OffsetDateTime effectTo = this.getOffsetDateTime("nvnhaenslut");
        boolean effectFromUncertain = this.getBoolean("haenstart_umrk-navne");
        boolean effectToUncertain = this.getBoolean("haenslut_umrk-navne");

        if (registrationFrom.equals(this.get("nvn_ts"))) {
            personBaseData = this.getBaseDataItem(data, effectFrom, effectFromUncertain, effectTo, effectToUncertain);
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
                    false
            );
        }
        if (registrationFrom.equals(this.get("adrnvn_ts"))) {
            personBaseData = this.getBaseDataItem(data, effectFrom, effectFromUncertain, effectTo, effectToUncertain);
            personBaseData.setAddressName(
                    this.getInt("adrnvn_mynkod"),
                    this.get("adrnvn")
            );
        }
        if (registrationFrom.equals(this.get("dok_ts-navne"))) {
            personBaseData = this.getBaseDataItem(data, effectFrom, effectFromUncertain, effectTo, effectToUncertain);
            personBaseData.setNameVerification(
                    this.getInt("dok_mynkod-navne"),
                    this.getBoolean("dok-navne")
            );
        }
        if (registrationFrom.equals(this.get("myntxt_ts-navne"))) {
            personBaseData = this.getBaseDataItem(data, effectFrom, effectFromUncertain, effectTo, effectToUncertain);
            personBaseData.setNameAuthorityText(
                    this.getInt("myntxt_mynkod-navne"),
                    this.get("myntxt-navne")
            );
        }
        return data;
    }

    @Override
    public String getRecordType() {
        return RECORDTYPE_HISTORIC_NAME;
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

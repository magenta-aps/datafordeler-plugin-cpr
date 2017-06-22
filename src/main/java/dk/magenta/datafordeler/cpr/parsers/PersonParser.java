package dk.magenta.datafordeler.cpr.parsers;

import dk.magenta.datafordeler.core.util.DoubleHashMap;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.data.CprData;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.records.CprRecord;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.lang.model.type.NullType;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.HashSet;


/**
 * Created by lars on 04-11-14.
 */
@Component
public class PersonParser extends CprSubParser {

    /*
    * Inner classes for parsed data
    * */

    public abstract class PersonDataRecord<B extends CprData> extends CprRecord {

        public static final String RECORDTYPE_PERSON = "001";
        public static final String RECORDTYPE_CURRENT_NAME = "020";
        public static final String RECORDTYPE_HISTORIC_NAME = "021";
        public static final String RECORDTYPE_DOMESTIC_ADDRESS = "025";
        // TODO: Add one for each data type

        public PersonDataRecord(String line) throws ParseException {
            super(line);
            this.obtain("pnr", 4, 10);
        }

        protected int getTimestampStart() {
            return 21;
        }

        public int getCprNumber() {
            return Integer.parseInt(this.get("pnr"));
        }

        public HashSet<String> getTimestamps() {
            return new HashSet<>();
        }

        protected B getBaseDataItem(DoubleHashMap<String, String, B> data, String effectStart, String effectEnd) {
            effectStart = CprRecord.normalizeDate(effectStart);
            effectEnd = CprRecord.normalizeDate(effectEnd);
            B personBaseData = data.get(effectStart, effectEnd);
            if (personBaseData == null) {
                personBaseData = this.createEmptyBaseData();
                data.put(effectStart, effectEnd, personBaseData);
            }
            return personBaseData;
        }

        protected B getBaseDataItem(ListHashMap<PersonEffect, B> data) {
            return this.getBaseDataItem(data, null, false,null, false);
        }

        protected B getBaseDataItem(ListHashMap<PersonEffect, B> data, OffsetDateTime effectFrom, boolean effectFromUncertain) {
            return this.getBaseDataItem(data, effectFrom, effectFromUncertain,null, false);
        }

        protected B getBaseDataItem(ListHashMap<PersonEffect, B> data, OffsetDateTime effectFrom, boolean effectFromUncertain, OffsetDateTime effectTo, boolean effectToUncertain) {
            B personBaseData = this.createEmptyBaseData();
            PersonEffect effect = new PersonEffect(null, effectFrom, effectTo);
            effect.setUncertainFrom(effectFromUncertain);
            effect.setUncertainTo(effectToUncertain);
            data.add(effect, personBaseData);
            return personBaseData;
        }

        protected abstract B createEmptyBaseData();
    }

    public class PersonData extends PersonDataRecord<PersonBaseData> {
        public PersonData(String line) throws ParseException {
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

    public class AddressData extends PersonDataRecord<PersonBaseData> {
        public AddressData(String line) throws ParseException {
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

    public class HistoricAddressData extends PersonDataRecord<PersonBaseData> {

        public HistoricAddressData(String line) throws ParseException {
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



            //------------------------------------------------------------------------------------------------------------------

    public PersonParser() {
    }

    private static Logger log = Logger.getLogger(PersonParser.class);


    @Override
    protected CprRecord parseLine(String recordType, String line) {
        CprRecord r = super.parseLine(recordType, line);
        if (r != null) {
            return r;
        }
        try {
            switch (recordType) {
                case PersonDataRecord.RECORDTYPE_PERSON:
                    return new PersonData(line);
                case PersonDataRecord.RECORDTYPE_CURRENT_NAME:
                    return new NameData(line);
                case PersonDataRecord.RECORDTYPE_DOMESTIC_ADDRESS:
                    return new AddressData(line);
                case PersonDataRecord.RECORDTYPE_HISTORIC_NAME:
                    return new HistoricAddressData(line);
                // TODO: Add one of these for each type...
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}

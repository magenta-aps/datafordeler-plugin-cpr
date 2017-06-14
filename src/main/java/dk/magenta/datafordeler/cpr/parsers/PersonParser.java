package dk.magenta.datafordeler.cpr.parsers;

import dk.magenta.datafordeler.cpr.records.CprRecord;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.text.ParseException;


/**
 * Created by lars on 04-11-14.
 */
@Component
public class PersonParser extends CprSubParser {

    /*
    * Inner classes for parsed data
    * */

    public abstract class PersonDataRecord extends CprRecord {
        public static final String RECORDTYPE_EXAMPLE = "001";
        // TODO: Add one for each data type

        public PersonDataRecord(String line) throws ParseException {
            super(line);
            this.obtain("commonData1", 4, 4);
            this.obtain("commonData2", 8, 4);
            this.obtain("timestamp", this.getTimestampStart(), 12);
        }

        protected int getTimestampStart() {
            return 21;
        }
    }

    public class ExampleData extends PersonDataRecord {
        public ExampleData(String line) throws ParseException {
            super(line);
            this.obtain("datapoint1", 12, 4);
            this.obtain("datapoint2", 16, 4);
            this.obtain("datapoint3", 20, 1);
        }
        protected int getTimestampStart() {
            return 22;
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
            if (recordType.equals(PersonDataRecord.RECORDTYPE_EXAMPLE)) {
                return new ExampleData(line);
            }
            // TODO: Add one of these for each type...

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
/*
    @Override
    public DataProviderConfiguration getDefaultConfiguration() {
        JSONObject config = new JSONObject();
        config.put(this.getSourceUrlFieldName(),"https://cpr.dk/media/152096/vejregister_hele_landet_pr_150101.zip");
        return new DataProviderConfiguration(config);
    }
*/
}

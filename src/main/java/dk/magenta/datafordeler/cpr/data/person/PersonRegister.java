package dk.magenta.datafordeler.cpr.data.person;

import dk.magenta.datafordeler.cpr.data.CprSubRegister;
import dk.magenta.datafordeler.cpr.data.records.CprRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.text.ParseException;


/**
 * Created by lars on 04-11-14.
 */
@Component
public class PersonRegister extends CprSubRegister {


    /*
    * Inner classes for parsed data
    * */

    public abstract class PersonDataRecord extends CprRecord {
        public static final String RECORDTYPE_EXAMPLE = "001";
        // Add one for each data type

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

    /*
    * Constructors
    * */

    public PersonRegister() {
    }

    private static Logger log = Logger.getLogger(PersonRegister.class);

    /*
    * Data source spec
    * */

    @Autowired
    private ConfigurableApplicationContext ctx;

    public Resource getRecordResource() {
        return this.ctx.getResource("classpath:/data/cprVejregister.zip");
    }


    /*
    * Parse definition
    * */

    protected CprRecord parseTrimmedLine(String recordType, String line) {
        CprRecord r = super.parseTrimmedLine(recordType, line);
        if (r != null) {
            return r;
        }
        try {
            if (recordType.equals(PersonDataRecord.RECORDTYPE_EXAMPLE)) {
                return new ExampleData(line);
            }
            // Add one for each type...

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

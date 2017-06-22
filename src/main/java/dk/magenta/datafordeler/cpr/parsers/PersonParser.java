package dk.magenta.datafordeler.cpr.parsers;

import dk.magenta.datafordeler.cpr.records.*;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.text.ParseException;


/**
 * Created by lars on 04-11-14.
 */
@Component
public class PersonParser extends CprSubParser {

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

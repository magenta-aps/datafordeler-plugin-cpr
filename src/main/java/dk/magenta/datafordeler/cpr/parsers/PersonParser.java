package dk.magenta.datafordeler.cpr.parsers;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.records.*;
import dk.magenta.datafordeler.cpr.records.person.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;


/**
 * Created by lars on 04-11-14.
 */
@Component
public class PersonParser extends CprSubParser {

    public PersonParser() {
    }

    private static Logger log = LogManager.getLogger(PersonParser.class);


    @Override
    protected CprRecord parseLine(String recordType, String line) {
        CprRecord r = super.parseLine(recordType, line);
        if (r != null) {
            return r;
        }
        try {
            switch (recordType) {
                case PersonDataRecord.RECORDTYPE_PERSON:
                    return new PersonRecord(line);
                case PersonDataRecord.RECORDTYPE_PROTECTION:
                    return new ProtectionRecord(line);
                case PersonDataRecord.RECORDTYPE_CURRENT_NAME:
                    return new NameRecord(line);
                case PersonDataRecord.RECORDTYPE_DOMESTIC_ADDRESS:
                    return new AddressRecord(line);
                case PersonDataRecord.RECORDTYPE_HISTORIC_NAME:
                    return new HistoricNameRecord(line);
                case PersonDataRecord.RECORDTYPE_FOREIGN_ADDRESS:
                    return new ForeignAddressRecord(line);
                // TODO: Add one of these for each type...
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}

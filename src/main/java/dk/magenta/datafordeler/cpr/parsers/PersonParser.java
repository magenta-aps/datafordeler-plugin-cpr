package dk.magenta.datafordeler.cpr.parsers;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.records.person.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;


/**
 * Parser for the Person line format.
 */
@Component
public class PersonParser extends CprSubParser<PersonDataRecord> {

    public PersonParser() {
    }

    private Logger log = LogManager.getLogger(PersonParser.class);

    @Override
    public Logger getLog() {
        return this.log;
    }

    @Override
    protected PersonDataRecord parseLine(String recordType, String line) {
        this.logType(recordType);
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
                case PersonDataRecord.RECORDTYPE_CIVILSTATUS:
                    return new CivilStatusRecord(line);
                case PersonDataRecord.RECORDTYPE_HISTORIC_CIVILSTATUS:
                    return new HistoricCivilStatusRecord(line);
                case PersonDataRecord.RECORDTYPE_HISTORIC_CPRNUMBER:
                    return new HistoricCprNumberRecord(line);
                // TODO: Add one of these for each type...
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}

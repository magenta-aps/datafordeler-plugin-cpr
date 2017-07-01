package dk.magenta.datafordeler.cpr.parsers;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.records.CprRecord;
import dk.magenta.datafordeler.cpr.records.CprSlutRecord;
import dk.magenta.datafordeler.cpr.records.CprStartRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Created by lars on 04-11-14.
 */

@Component
public abstract class CprSubParser extends LineParser {

    public CprSubParser() {
    }

    private Logger log = LogManager.getLogger("CprSubParser");

    @Override
    protected CprRecord parseLine(String line) {
        return this.parseLine(line.substring(0, 3), line);
    }

    protected CprRecord parseLine(String recordType, String line) {
        log.debug("Parsing record of type "+recordType);
        try {
            if (recordType.equals(CprRecord.RECORDTYPE_START)) {
                return new CprStartRecord(line);
            }
            if (recordType.equals(CprRecord.RECORDTYPE_SLUT)) {
                return new CprSlutRecord(line);
            }
        } catch (ParseException e) {
            log.error(e);
        }
        return null;
    }

}

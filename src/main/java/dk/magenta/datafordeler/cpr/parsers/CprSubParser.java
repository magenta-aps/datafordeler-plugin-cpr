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
public abstract class CprSubParser<T extends CprRecord> extends LineParser<T> {

    public CprSubParser() {
    }

    private Logger log = LogManager.getLogger("CprSubParser");

    public Logger getLog() {
        return this.log;
    }

    @Override
    protected T parseLine(String line) {
        return this.parseLine(line.substring(0, 3), line);
    }

    protected void logType(String recordType) {
        this.getLog().debug("Parsing record of type "+recordType);
    }

    protected abstract T parseLine(String recordType, String line);

}

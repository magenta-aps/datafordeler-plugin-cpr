package dk.magenta.datafordeler.cpr.parsers;

import dk.magenta.datafordeler.cpr.records.CprRecord;
import dk.magenta.datafordeler.cpr.records.CprSlutRecord;
import dk.magenta.datafordeler.cpr.records.CprStartRecord;
import org.springframework.stereotype.Component;

import java.text.ParseException;

/**
 * Created by lars on 04-11-14.
 */

@Component
public abstract class CprSubParser extends LineParser {

    public CprSubParser() {
    }

    @Override
    protected CprRecord parseLine(String line) {
        return this.parseLine(line.substring(0, 3), line);
    }

    protected CprRecord parseLine(String recordType, String line) {
        System.out.println(recordType);
        try {
            if (recordType.equals(CprRecord.RECORDTYPE_START)) {
                return new CprStartRecord(line);
            }
            if (recordType.equals(CprRecord.RECORDTYPE_SLUT)) {
                return new CprSlutRecord(line);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}

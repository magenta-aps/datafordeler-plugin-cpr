package dk.magenta.datafordeler.cpr.data;

import dk.magenta.datafordeler.cpr.data.records.CprRecord;
import dk.magenta.datafordeler.cpr.data.records.CprSlutRecord;
import dk.magenta.datafordeler.cpr.data.records.CprStartRecord;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;

/**
 * Created by lars on 04-11-14.
 */

@Component
public abstract class CprSubRegister extends LineRegister {

    public CprSubRegister() {
    }

    @PostConstruct
    protected void postConstruct() {
        // Do nothing
    }

    protected CprRecord parseTrimmedLine(String line) {
        return this.parseTrimmedLine(line.substring(0, 3), line);
    }
    protected CprRecord parseTrimmedLine(String recordType, String line) {
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

/*
    public DataProviderConfiguration getDefaultConfiguration() {
        return new DataProviderConfiguration();
    }*/

}

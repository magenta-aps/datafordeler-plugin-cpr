package dk.magenta.datafordeler.cpr.parsers;

import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.records.CprRecord;
import dk.magenta.datafordeler.cpr.records.person.*;
import dk.magenta.datafordeler.cpr.records.road.RoadDataRecord;
import dk.magenta.datafordeler.cpr.records.road.RoadMemoRecord;
import dk.magenta.datafordeler.cpr.records.road.RoadRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;


/**
 * Created by lars on 04-11-14.
 */
@Component
public class RoadParser extends CprSubParser {

    public RoadParser() {
    }

    private static Logger log = LogManager.getLogger(RoadParser.class);


    @Override
    protected CprRecord parseLine(String recordType, String line) {
        CprRecord r = super.parseLine(recordType, line);
        if (r != null) {
            return r;
        }
        try {
            switch (recordType) {
                case RoadDataRecord.RECORDTYPE_ROAD:
                    return new RoadRecord(line);
                case RoadDataRecord.RECORDTYPE_ROADMEMO:
                    return new RoadMemoRecord(line);
                // TODO: Add one of these for each type...
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}

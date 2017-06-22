package dk.magenta.datafordeler.cpr.parsers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lars on 18-12-14.
 */
@Component
public class CprParser {

    @Autowired
    private PersonParser personParser;

    public static final DateTimeFormatter[] DATETIME_PARSERS = {
            DateTimeFormatter.ofPattern("uuuuMMddHHmm"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
    };

    public static final ZoneId CPR_TIMEZONE = ZoneId.of("Europe/Copenhagen");

    // TODO: Add more parsers

    private List<CprSubParser> subParsers;

    public CprParser() {
    }

    @PostConstruct
    protected void postConstruct() {
        this.subParsers = new ArrayList<>();
        this.subParsers.add(this.personParser);
    }

    public static OffsetDateTime parseTimestamp(String timestamp) {
        if (timestamp != null) {
            LocalDateTime dateTime = parseDateTime(timestamp);
            if (dateTime != null) {
                return OffsetDateTime.from(
                        ZonedDateTime.of(
                                dateTime,
                                CprParser.CPR_TIMEZONE
                        )
                );
            }
        }
        return null;
    }

    private static LocalDateTime parseDateTime(String timestamp) {
        for (DateTimeFormatter parser : DATETIME_PARSERS) {
            try {
                return LocalDateTime.parse(timestamp, parser);
            } catch (DateTimeParseException e) {}
        }
        return null;
    }

}

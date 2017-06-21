package dk.magenta.datafordeler.cpr.parsers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lars on 18-12-14.
 */
@Component
public class CprParser {

    @Autowired
    private PersonParser personParser;

    public static final DateTimeFormatter CPR_DATETIME = DateTimeFormatter.ofPattern("uuuuMMddHHmm");

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
        return OffsetDateTime.from(
            ZonedDateTime.of(
                LocalDateTime.parse(timestamp, CprParser.CPR_DATETIME),
                CprParser.CPR_TIMEZONE
            )
        );
    }

}

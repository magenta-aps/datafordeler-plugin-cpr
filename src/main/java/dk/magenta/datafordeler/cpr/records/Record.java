package dk.magenta.datafordeler.cpr.records;

import dk.magenta.datafordeler.cpr.parsers.CprParser;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;

/**
 * Created by lars on 15-12-14.
 */
public abstract class Record extends HashMap<String, String> {

    public abstract String getRecordType();

    public String getRecordClass() {
        String[] classParts = this.getClass().getCanonicalName().split("\\.");
        return classParts[classParts.length-1];
    }

    public int getInt(String key) {
        return this.getInt(key, false);
    }

    public int getInt(String key, boolean lenient) {
        String value = this.get(key);
        if (lenient) {
            value = value.replaceAll("[^\\d]", "");
        }
        try {
            return Integer.parseInt(value, 10);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    public long getLong(String key) {
        return this.getLong(key, false);
    }

    public long getLong(String key, boolean lenient) {
        String value = this.get(key);
        if (lenient) {
            value = value.replaceAll("[^\\d]", "");
        }
        try {
            return Long.parseLong(value, 10);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean getBoolean(String key) {
        String value = this.get(key).toLowerCase();
        return value.equals("1") || value.equals("yes") || value.equals("true") || value.equals("ja") || value.equals("y");
    }

    public Character getChar(String key) {
        String value = this.get(key);
        return value != null && !value.isEmpty() ? value.charAt(0) : null;
    }


    private static DateTimeFormatter[] dateParsers = {
            DateTimeFormatter.BASIC_ISO_DATE,
            DateTimeFormatter.ISO_LOCAL_DATE
    };
    public LocalDate getDate(String key) {
        String value = this.get(key);
        if (value != null && !value.isEmpty()) {
            for (DateTimeFormatter parser : dateParsers) {
                try {
                    return LocalDate.parse(value, parser);
                } catch (DateTimeParseException e) {
                }
            }
        }
        return null;
    }

    private static DateTimeFormatter[] timeParsers = {
            DateTimeFormatter.ISO_LOCAL_TIME,
            DateTimeFormatter.ofPattern("HH.mm.ss"),
            DateTimeFormatter.ofPattern("HH-mm-ss")
    };
    public LocalTime getTime(String key) {
        String value = this.get(key);
        if (value != null && !value.isEmpty()) {
            for (DateTimeFormatter parser : timeParsers) {
                try {
                    return LocalTime.parse(value, parser);
                } catch (DateTimeParseException e) {
                }
            }
        }
        return null;
    }

    private static DateTimeFormatter[] datetimeParsers = {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("uuuuMMddHHmmss"),
            DateTimeFormatter.ofPattern("uuuuMMddHHmm")
    };
    public LocalDateTime getDateTime(String key) {
        String value = this.get(key);
        if (value != null && !value.isEmpty()) {
            for (DateTimeFormatter parser : datetimeParsers) {
                try {
                    return LocalDateTime.parse(value, parser);
                } catch (DateTimeParseException e) {
                }
            }
            LocalDate localDate = this.getDate(key);
            if (localDate != null) {
                return LocalDateTime.of(localDate, LocalTime.MIDNIGHT);
            }
        }
        return null;
    }

    public OffsetDateTime getOffsetDateTime(String key) {
        LocalDateTime dateTime = this.getDateTime(key);
        if (dateTime != null) {
            return OffsetDateTime.from(
                    ZonedDateTime.of(
                            dateTime,
                            CprParser.CPR_TIMEZONE
                    )
            );
        }
        return null;
    }

}

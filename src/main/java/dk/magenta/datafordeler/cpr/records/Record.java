package dk.magenta.datafordeler.cpr.records;

import java.time.LocalDate;
import java.time.LocalTime;
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
                    System.out.println("Could not parse date "+value+" with parser "+parser);
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
                    System.out.println("Could not parse time "+value+" with parser "+parser);
                }
            }
        }
        return null;
    }

}

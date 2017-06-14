package dk.magenta.datafordeler.cpr.records;

import java.text.ParseException;
import java.util.regex.Pattern;

/**
 * Created by lars on 04-11-14.
 */
public abstract class CprRecord extends Record {

    public static final String RECORDTYPE_START = "000";
    public static final String RECORDTYPE_SLUT = "999";
    private static Pattern leadingZero = Pattern.compile("^0+");

    private String line;

    public CprRecord(String line) throws ParseException {
        if (line == null) {
            throw new ParseException("Invalid NULL input.", 0);
        }
        this.line = line;
        this.obtain("type", 1, 3, false);
        String thisType = this.getRecordType();
        if (!this.get("type").equals(thisType)) {
            throw new ParseException("Invalid recordtype "+this.get("type")+" for class "+this.getClass().getName()+", was expecting the input to begin with "+thisType+". Input was "+line+".", 0);
        }
    }

    protected String substr(String line, int position, int length) {
        return line.substring(Math.min(position - 1, line.length()), Math.min(position + length - 1, line.length())).trim();
    }

    protected void obtain(String key, int position, int length) {
        this.obtain(key, position, length, true);
    }

    protected void obtain(String key, int position, int length, boolean truncateLeadingZeroes) {
        String value = this.substr(this.line, position, length);
        if (truncateLeadingZeroes) {
            value = leadingZero.matcher(value).replaceFirst("");
        }
        this.put(key, value);
    }

    protected void clean() {
        this.line = null;
    }

}
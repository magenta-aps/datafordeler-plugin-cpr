package dk.magenta.datafordeler.cpr.data;

import dk.magenta.datafordeler.cpr.data.records.Record;
import org.apache.log4j.Logger;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lars on 15-12-14.
 */
public abstract class LineRegister {

    protected Logger log = Logger.getLogger(LineRegister.class);

    public LineRegister() {
    }

    public List<Record> parse(InputStream input) {
        try {
            BufferedInputStream inputstream = new BufferedInputStream(input);
            ArrayList<Record> records = new ArrayList<>();

            String encoding = this.getEncoding();
            if (encoding != null) {
                this.log.info("Using explicit encoding " + encoding);
            } else {
                // Try to guess the encoding based on the stream contents
                CharsetDetector detector = new CharsetDetector();
                detector.setText(inputstream);
                CharsetMatch match = detector.detect();
                if (match != null) {
                    encoding = match.getName();
                    this.log.info("Interpreting data as " + encoding);
                } else {
                    encoding = "UTF-8";
                    this.log.info("Falling back to default encoding " + encoding);
                }
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream, encoding.toUpperCase()));

            this.log.info("Reading data");
            int i = 0, j = 0;

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line != null) {
                    line = line.trim();
                    if (line.length() > 3) {
                        try {
                            Record record = this.parseTrimmedLine(line);
                            if (record != null) {
                                records.add(record);
                            }
                        } catch (OutOfMemoryError e) {
                            System.out.println(line);
                        }
                    }
                }
                i++;
                if (i >= 100000) {
                    j++;
                    System.gc();
                    this.log.trace("    parsed " + (j * i) + " lines");
                    i = 0;
                }
            }
            this.log.trace("    parsed " + (j * 100000 + i) + " lines");
            int count = records.size();
            this.log.info("Parse complete (" + count + " usable entries found)");
            return records;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.log.warn("Parse failed");
        return null;
    }

    protected Record parseTrimmedLine(String line) {
        return null;
    }

    // Maybe override in subclass?
    protected String getEncoding() {
        return null;
    }
}

package dk.magenta.datafordeler.cpr.parsers;

import dk.magenta.datafordeler.cpr.records.Record;
import org.apache.log4j.Logger;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lars on 15-12-14.
 */
public abstract class LineParser {

    protected Logger log = Logger.getLogger(LineParser.class);

    public LineParser() {
    }

    // Maybe override in subclass?
    protected String getEncoding() {
        return null;
    }

    public List<Record> parse(InputStream input) {
        BufferedInputStream inputstream = new BufferedInputStream(input);

        String encoding = this.getEncoding();
        if (encoding != null) {
            this.log.info("Using explicit encoding " + encoding);
        } else {
            // Try to guess the encoding based on the stream contents
            CharsetDetector detector = new CharsetDetector();
            try {
                detector.setText(inputstream);
            } catch (IOException e) {
                e.printStackTrace();
                this.log.warn("Parse failed");
                return null;
            }
            CharsetMatch match = detector.detect();
            if (match != null) {
                encoding = match.getName();
                this.log.info("Interpreting data as " + encoding);
            } else {
                encoding = "UTF-8";
                this.log.info("Falling back to default encoding " + encoding);
            }
        }
        return this.parse(inputstream, encoding);
    }

    public List<Record> parse(InputStream input, String encoding) {
        try {
            ArrayList<Record> records = new ArrayList<>();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input, encoding.toUpperCase()));

            this.log.info("Reading data");
            int batchSize = 0, batchCount = 0;

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                line = line.trim();
                if (line.length() > 3) {
                    try {
                        Record record = this.parseLine(line);
                        if (record != null) {
                            records.add(record);
                        }
                    } catch (OutOfMemoryError e) {
                        System.out.println(line);
                    }
                }
                batchSize++;
                if (batchSize >= 100000) {
                    batchCount++;
                    System.gc();
                    this.log.trace("    parsed " + (batchCount * batchSize) + " lines");
                    batchSize = 0;
                }
            }
            this.log.trace("    parsed " + (batchCount * 100000 + batchSize) + " lines");
            int count = records.size();
            this.log.info("Parse complete (" + count + " usable entries found)");
            return records;

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.log.warn("Parse failed");
        return null;
    }

    protected abstract Record parseLine(String line);

}
package dk.magenta.datafordeler.cpr.parsers;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import dk.magenta.datafordeler.cpr.records.CprRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lars on 15-12-14.
 */
public abstract class LineParser<T extends CprRecord> {

    protected Logger log = LogManager.getLogger(LineParser.class);

    public LineParser() {
    }

    // Maybe override in subclass?
    protected String getEncoding() {
        return null;
    }

    public List<T> parse(InputStream input) {
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

    // TODO: output an objectInputStream
    public List<T> parse(InputStream input, String encoding) {
        try {
            ArrayList<T> records = new ArrayList<>();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input, encoding.toUpperCase()));

            this.log.info("Reading data");
            int batchSize = 0, batchCount = 0;

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                line = line.trim();
                if (line.length() > 3) {
                    try {
                        T record = this.parseLine(line);
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
            int count = records.size();
            this.log.info("Parse complete (" + count + " usable entries found)");
            return records;

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.log.warn("Parse failed");
        return null;
    }




    public abstract T parseLine(String line);

}

package dk.magenta.datafordeler.cpr;

import dk.magenta.datafordeler.cpr.parsers.PersonParser;
import dk.magenta.datafordeler.cpr.records.Record;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;
import java.util.List;

/**
 * Created by lars on 14-06-17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ParseTest {

    @Autowired
    private PersonParser personParser;

    @Test
    public void testParse() {
        InputStream testData = ParseTest.class.getResourceAsStream("/cprdata.txt");
        List<Record> records = personParser.parse(testData, "utf-8");
        for (Record record : records) {
            System.out.println(record);
        }
    }
}

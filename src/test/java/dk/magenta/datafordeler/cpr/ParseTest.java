package dk.magenta.datafordeler.cpr;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.util.DoubleHashMap;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import dk.magenta.datafordeler.cpr.data.person.PersonEffect;
import dk.magenta.datafordeler.cpr.data.person.PersonEntity;
import dk.magenta.datafordeler.cpr.data.person.PersonRegistration;
import dk.magenta.datafordeler.cpr.parsers.CprParser;
import dk.magenta.datafordeler.cpr.parsers.PersonParser;
import dk.magenta.datafordeler.cpr.records.Record;
import org.hibernate.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Created by lars on 14-06-17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ParseTest {

    @Autowired
    private PersonParser personParser;

    @Autowired
    private QueryManager queryManager;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testParse() {
        InputStream testData = ParseTest.class.getResourceAsStream("/cprdata.txt");
        List<Record> records = personParser.parse(testData, "utf-8");

        ListHashMap<Integer, PersonParser.PersonDataRecord> recordMap = new ListHashMap<>();
        for (Record record : records) {
            if (record instanceof PersonParser.PersonDataRecord) {
                PersonParser.PersonDataRecord personDataRecord = (PersonParser.PersonDataRecord) record;
                int cprNumber = personDataRecord.getCprNumber();
                recordMap.add(cprNumber, personDataRecord);
            }
        }

        Session session = sessionManager.getSessionFactory().openSession();
        for (int cprNumber : recordMap.keySet()) {
            List<PersonParser.PersonDataRecord> recordList = recordMap.get(cprNumber);
            PersonEntity entity = queryManager.getItem(session, PersonEntity.class, Collections.singletonMap("cprNumber", cprNumber));
            if (entity == null) {
                entity = new PersonEntity();
                entity.setCprNumber(cprNumber);
            }

            ListHashMap<String, PersonParser.PersonDataRecord> ajourRecords = new ListHashMap<>();
            TreeSet<String> sortedTimestamps = new TreeSet<>();
            for (PersonParser.PersonDataRecord record : recordList) {
                System.out.println("record: "+record);
                Set<String> timestamps = record.getTimestamps();
                for (String timestamp : timestamps) {
                    if (timestamp != null && !timestamp.isEmpty()) {
                        ajourRecords.add(timestamp, record);
                        sortedTimestamps.add(timestamp);
                    }
                }
            }

            // Create one Registration per unique timestamp
            ArrayList<PersonRegistration> registrations = new ArrayList<>();
            PersonRegistration lastRegistration = null;
            for (String timestamp : sortedTimestamps) {


                PersonRegistration registration;

                if (lastRegistration == null) {
                    registration = new PersonRegistration();
                } else {
                    registration = this.cloneRegistration(lastRegistration);
                }

                OffsetDateTime time = CprParser.parseTimestamp(timestamp);
                registration.setRegistrationFrom(time);

                // Each record sets its own basedata
                for (PersonParser.PersonDataRecord record : ajourRecords.get(timestamp)) {
                    // Take what we need from the record and put it into dataitems
                    DoubleHashMap<String, String, PersonBaseData> dataItems = record.getDataEffects(timestamp);
                    if (dataItems != null) {
                        for (String from : dataItems.keySet()) {
                            OffsetDateTime effectFrom = CprParser.parseTimestamp(from);
                            HashMap<String, PersonBaseData> inner = dataItems.get(from);
                            for (String to : inner.keySet()) {
                                OffsetDateTime effectTo = CprParser.parseTimestamp(to);
                                PersonBaseData data = inner.get(to);
                                PersonEffect effect = registration.getEffect(effectFrom, effectTo);
                                if (effect == null) {
                                    effect = new PersonEffect(registration, effectFrom, effectTo);
                                }
                                data.addEffect(effect);
                            }
                        }
                    }
                }

                if (lastRegistration != null) {
                    lastRegistration.setRegistrationTo(time);

                }
                lastRegistration = registration;
                registrations.add(registration);

            }
            System.out.println("registrations: "+registrations);
        }
    }

    private PersonRegistration cloneRegistration(PersonRegistration originalRegistration) {
        PersonRegistration newRegistration = new PersonRegistration();
        for (PersonEffect originalEffect : originalRegistration.getEffects()) {
            PersonEffect newEffect = new PersonEffect(newRegistration, originalEffect.getEffectFrom(), originalEffect.getEffectTo());
            for (PersonBaseData originalData : originalEffect.getDataItems()) {
                originalData.addEffect(newEffect);
            }
        }
        return newRegistration;
    }
}

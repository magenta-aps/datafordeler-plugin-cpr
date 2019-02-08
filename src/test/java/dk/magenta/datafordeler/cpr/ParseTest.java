package dk.magenta.datafordeler.cpr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.core.util.Equality;
import dk.magenta.datafordeler.cpr.data.person.PersonEntityManager;
import dk.magenta.datafordeler.cpr.data.residence.*;
import dk.magenta.datafordeler.cpr.data.residence.data.ResidenceBaseData;
import dk.magenta.datafordeler.cpr.data.road.RoadEntityManager;
import dk.magenta.datafordeler.cpr.records.road.RoadRecordQuery;
import dk.magenta.datafordeler.cpr.records.road.data.RoadNameBitemporalRecord;
import dk.magenta.datafordeler.cpr.records.road.data.RoadEntity;
import dk.magenta.datafordeler.cpr.records.road.data.RoadMemoBitemporalRecord;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ParseTest {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private PersonEntityManager personEntityManager;

    @Autowired
    private RoadEntityManager roadEntityManager;

    @Autowired
    private ResidenceEntityManager residenceEntityManager;

    @Autowired
    private ObjectMapper objectMapper;


    @Before
    @After
    public void cleanup() {
        QueryManager.clearCaches();
    }

    private void loadRoad(ImportMetadata importMetadata) throws DataFordelerException, IOException {

        //InputStream testData = new FileInputStream(new File("/home/mmj/Desktop/A370715.txt"));

        InputStream testData = ParseTest.class.getResourceAsStream("/roaddata.txt");
        roadEntityManager.parseData(testData, importMetadata);
        testData.close();
    }

    private void loadResidence(ImportMetadata importMetadata) throws DataFordelerException, IOException {
        InputStream testData = ParseTest.class.getResourceAsStream("/roaddata.txt");
        residenceEntityManager.parseData(testData, importMetadata);
        testData.close();
    }

    @Test
    public void testRoadIdempotence() throws IOException, DataFordelerException {
        Session session = sessionManager.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        ImportMetadata importMetadata = new ImportMetadata();
        importMetadata.setSession(session);
        importMetadata.setTransactionInProgress(true);
        try {
            loadRoad(importMetadata);
            List<RoadEntity> entities = QueryManager.getAllEntities(session, RoadEntity.class);
            JsonNode firstImport = objectMapper.valueToTree(entities);

            loadRoad(importMetadata);
            entities = QueryManager.getAllEntities(session, RoadEntity.class);
            JsonNode secondImport = objectMapper.valueToTree(entities);
            assertJsonEquality(firstImport, secondImport, true, true);
        } finally {
            transaction.rollback();
            session.close();
        }
    }

    @Test
    public void testParseRoad() throws IOException, DataFordelerException {
        Session session = sessionManager.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        ImportMetadata importMetadata = new ImportMetadata();
        importMetadata.setSession(session);
        importMetadata.setTransactionInProgress(true);
        try {
            loadRoad(importMetadata);

            RoadRecordQuery query = new RoadRecordQuery();
            query.addKommunekode("0730");
            query.setVejkode("0004");

            List<RoadEntity> entities = QueryManager.getAllEntities(session, query, RoadEntity.class);
            Assert.assertEquals(1, entities.size());
            RoadEntity entity = entities.get(0);

            Assert.assertEquals(RoadEntity.generateUUID(730, 4), entity.getUUID());
            Assert.assertEquals(730, entity.getMunicipalityCode());
            Assert.assertEquals(4, entity.getRoadcode());
            Assert.assertEquals(1, entity.getNames().size());
            Assert.assertEquals(3, entity.getMemo().size());
            Assert.assertEquals(2, entity.getPostcode().size());
            Assert.assertEquals(0, entity.getCity().size());

            RoadNameBitemporalRecord roadName = entity.getNames().iterator().next();

            Assert.assertTrue(Equality.equal(OffsetDateTime.parse("1900-01-01T12:00+01:00"), roadName.getHaenStart()));
            Assert.assertTrue(Equality.equal(OffsetDateTime.parse("2006-12-22T12:00:00+01:00"), roadName.getTimestamp()));

            Assert.assertTrue(Equality.equal(OffsetDateTime.parse("2006-12-22T12:00:00+01:00"), roadName.getRegistrationFrom()));

            List<RoadMemoBitemporalRecord> memoIterator = entity.getMemo().stream()
                    .sorted(Comparator.comparing(RoadMemoBitemporalRecord::getNoteLine)).collect(Collectors.toList());

            Assert.assertEquals("HUSNR.1 - BØRNEINSTITUTION -", memoIterator.get(0).getNoteLine());
            Assert.assertEquals("HUSNR.2 - EGEDAL -", memoIterator.get(1).getNoteLine());
            Assert.assertEquals("HUSNR.3 - KIRKE -", memoIterator.get(2).getNoteLine());

            String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entity);

            JSONAssert.assertEquals(
                    "{\"names\":[{\"vejensNavn\":\"Aalborggade\"}]}", jsonResponse, JSONCompareMode.LENIENT);

            JSONAssert.assertEquals(
                    "{\"adressenavn\":[{\"vejensNavn\":\"Aalborggade\"}]}", jsonResponse, JSONCompareMode.LENIENT);


        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            transaction.rollback();
            session.close();
        }
    }

    @Test
    public void testResidenceIdempotence() throws IOException, DataFordelerException {
        Session session = sessionManager.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        ImportMetadata importMetadata = new ImportMetadata();
        importMetadata.setSession(session);
        importMetadata.setTransactionInProgress(true);
        try {
            loadResidence(importMetadata);
            List<ResidenceEntity> entities = QueryManager.getAllEntities(session, ResidenceEntity.class);
            JsonNode firstImport = objectMapper.valueToTree(entities);

            loadResidence(importMetadata);
            entities = QueryManager.getAllEntities(session, ResidenceEntity.class);
            JsonNode secondImport = objectMapper.valueToTree(entities);
            assertJsonEquality(firstImport, secondImport, true, true);

        } finally {
            transaction.rollback();
            session.close();
        }
    }

    @Test
    public void testParseResidence() throws Exception {
        Session session = sessionManager.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        ImportMetadata importMetadata = new ImportMetadata();
        importMetadata.setSession(session);
        importMetadata.setTransactionInProgress(true);
        try {
            loadResidence(importMetadata);

            ResidenceQuery query = new ResidenceQuery();
            query.addKommunekode(360);
            query.setVejkode(206);

            List<ResidenceEntity> entities = QueryManager.getAllEntities(session, query, ResidenceEntity.class);
            Assert.assertEquals(1, entities.size());
            ResidenceEntity entity = entities.get(0);
            Assert.assertEquals(ResidenceEntity.generateUUID(360, 206, "44E", "", ""), entity.getUUID());

            System.out.println(new ResidenceOutputWrapper().wrapResult(entities.get(0), query));


            List<ResidenceRegistration> registrations = entities.get(0).getRegistrations();
            Assert.assertEquals(1, registrations.size());
            ResidenceRegistration registration = registrations.get(0);
            Assert.assertTrue(OffsetDateTime.parse("2006-12-22T12:00:00+01:00").isEqual(registration.getRegistrationFrom()));
            Assert.assertNull(registration.getRegistrationTo());
            List<ResidenceEffect> effects = registration.getEffects();
            Assert.assertEquals(1, effects.size());
            ResidenceEffect effect = effects.get(0);
            Assert.assertTrue(OffsetDateTime.parse("1991-09-23T12:00:00+02:00").isEqual(effect.getEffectFrom()));
            Assert.assertNull(effect.getEffectTo());
            Assert.assertFalse(effect.getEffectFromUncertain());
            Assert.assertFalse(effect.getEffectToUncertain());
            List<ResidenceBaseData> dataItems = effect.getDataItems();
            Assert.assertEquals(1, dataItems.size());
            ResidenceBaseData data = dataItems.get(0);
            Assert.assertEquals("", data.getEtage());
            Assert.assertEquals("44E", data.getHusnummer());
            Assert.assertEquals(360, data.getKommunekode());
            Assert.assertEquals("Provstelunden", data.getLokalitet());
            Assert.assertEquals("", data.getSideDoer());
            Assert.assertEquals(206, data.getVejkode());

        } finally {
            transaction.rollback();
            session.close();
        }
    }

    private static HashSet<String> ignoreKeys = new HashSet<String>();
    static {
        ignoreKeys.add("sidstImporteret");
    }
    private void assertJsonEquality(JsonNode node1, JsonNode node2, boolean ignoreArrayOrdering, boolean printDifference) {
        try {
            Assert.assertEquals(node1.isNull(), node2.isNull());
            Assert.assertEquals(node1.isArray(), node2.isArray());
            Assert.assertEquals(node1.isObject(), node2.isObject());
            Assert.assertEquals(node1.isLong(), node2.isLong());
            Assert.assertEquals(node1.isInt(), node2.isInt());
            Assert.assertEquals(node1.isShort(), node2.isShort());
            Assert.assertEquals(node1.isBoolean(), node2.isBoolean());
            Assert.assertEquals(node1.isTextual(), node2.isTextual());
            if (node1.isArray()) {
                Assert.assertEquals(node1.size(), node2.size());
                if (ignoreArrayOrdering) {
                    for (int i = 0; i < node1.size(); i++) {
                        boolean match = false;
                        for (int j = 0; j < node2.size(); j++) {
                            try {
                                assertJsonEquality(node1.get(i), node2.get(j), true, false);
                                match = true;
                            } catch (AssertionError e) {
                            }
                        }
                        if (!match) {
                            throw new AssertionError();
                        }
                    }
                } else {
                    for (int i = 0; i < node1.size(); i++) {
                        assertJsonEquality(node1.get(i), node2.get(i), false, printDifference);
                    }
                }
            } else if (node1.isObject()) {
                Assert.assertEquals(node1.size(), node2.size());
                Iterator<String> keys = node1.fieldNames();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Assert.assertNotNull(node2.get(key));
                    if (!ignoreKeys.contains(key)) {
                        assertJsonEquality(node1.get(key), node2.get(key), ignoreArrayOrdering, printDifference);
                    }
                }
            } else {
                Assert.assertEquals(node1.asText(), node2.asText());
            }
        } catch (AssertionError e) {
            if (printDifference) {
                try {
                    System.out.println("\n" + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node1) + "\n != \n" + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node2) + "\n\n\n");
                } catch (JsonProcessingException e1) {
                    System.out.println("\n" + node1.asText() + "\n != \n" + node2.asText() + "\n\n\n");
                }
            }
            throw e;
        }
    }

}

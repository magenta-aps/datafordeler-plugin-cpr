package dk.magenta.datafordeler.cpr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.Application;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.core.util.Equality;
import dk.magenta.datafordeler.cpr.data.person.PersonEntityManager;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceEntityManager;
import dk.magenta.datafordeler.cpr.data.road.RoadEntityManager;
import dk.magenta.datafordeler.cpr.records.road.RoadRecordQuery;
import dk.magenta.datafordeler.cpr.records.road.data.RoadEntity;
import dk.magenta.datafordeler.cpr.records.road.data.RoadMemoBitemporalRecord;
import dk.magenta.datafordeler.cpr.records.road.data.RoadNameBitemporalRecord;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RoadTest extends TestBase {

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

    @Override
    protected ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    @Before
    @After
    public void cleanup() {
        QueryManager.clearCaches();
    }

    private void loadRoad(ImportMetadata importMetadata) throws DataFordelerException, IOException {
        InputStream testData = RoadTest.class.getResourceAsStream("/roaddata.txt");
        roadEntityManager.parseData(testData, importMetadata);
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
            Assert.assertEquals(1, entity.getName().size());
            Assert.assertEquals(3, entity.getMemo().size());
            Assert.assertEquals(2, entity.getPostcode().size());
            Assert.assertEquals(0, entity.getCity().size());

            RoadNameBitemporalRecord roadName = entity.getName().iterator().next();

            Assert.assertTrue(Equality.equal(OffsetDateTime.parse("1900-01-01T12:00+01:00"), roadName.getEffectFrom()));
            Assert.assertTrue(Equality.equal(OffsetDateTime.parse("2006-12-22T12:00:00+01:00"), roadName.getRegistrationFrom()));

            List<RoadMemoBitemporalRecord> memoIterator = entity.getMemo().stream()
                    .sorted(Comparator.comparing(RoadMemoBitemporalRecord::getNoteLine)).collect(Collectors.toList());

            Assert.assertEquals("HUSNR.1 - BØRNEINSTITUTION -", memoIterator.get(0).getNoteLine());
            Assert.assertEquals("HUSNR.2 - EGEDAL -", memoIterator.get(1).getNoteLine());
            Assert.assertEquals("HUSNR.3 - KIRKE -", memoIterator.get(2).getNoteLine());

            String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entity);

            JSONAssert.assertEquals("{\"navn\":[{\"vejnavn\":\"Aalborggade\"}]}", jsonResponse, JSONCompareMode.LENIENT);


        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            transaction.rollback();
            session.close();
        }
    }
}

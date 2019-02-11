package dk.magenta.datafordeler.cpr;

import com.fasterxml.jackson.databind.JsonNode;
import dk.magenta.datafordeler.core.Application;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.fapi.ParameterMap;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.core.util.Equality;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RoadTest extends TestBase {

    @Autowired
    private RoadEntityManager roadEntityManager;

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
        Session session = this.getSessionManager().getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        ImportMetadata importMetadata = new ImportMetadata();
        importMetadata.setSession(session);
        importMetadata.setTransactionInProgress(true);
        try {
            loadRoad(importMetadata);
            List<RoadEntity> entities = QueryManager.getAllEntities(session, RoadEntity.class);
            JsonNode firstImport = this.getObjectMapper().valueToTree(entities);

            loadRoad(importMetadata);
            entities = QueryManager.getAllEntities(session, RoadEntity.class);
            JsonNode secondImport = this.getObjectMapper().valueToTree(entities);
            assertJsonEquality(firstImport, secondImport, true, true);
        } finally {
            transaction.rollback();
            session.close();
        }
    }

    @Test
    public void testParseRoad() throws IOException, DataFordelerException {
        Session session = this.getSessionManager().getSessionFactory().openSession();
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

            String jsonResponse = this.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(entity);

            JSONAssert.assertEquals("{\"navn\":[{\"vejnavn\":\"Aalborggade\"}]}", jsonResponse, JSONCompareMode.LENIENT);


        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            transaction.rollback();
            session.close();
        }
    }


    @Test
    public void testRoadAccess() throws Exception {

        UUID expectedUuid = UUID.nameUUIDFromBytes(("road:"+955+":"+1).getBytes());


        whitelistLocalhost();
        ImportMetadata importMetadata = new ImportMetadata();
        Session session = this.getSessionManager().getSessionFactory().openSession();
        importMetadata.setSession(session);
        Transaction transaction = session.beginTransaction();
        importMetadata.setTransactionInProgress(true);
        loadRoad(importMetadata);
        transaction.commit();
        session.close();

        TestUserDetails testUserDetails = new TestUserDetails();

        ParameterMap searchParameters = new ParameterMap();
        searchParameters.add("vejnavn", "TestVej");
        ResponseEntity<String> response = restSearch(searchParameters, "road");
        Assert.assertEquals(403, response.getStatusCode().value());

        testUserDetails.giveAccess(CprRolesDefinition.READ_CPR_ROLE);
        this.applyAccess(testUserDetails);

        response = restSearch(searchParameters, "road");
        Assert.assertEquals(200, response.getStatusCode().value());
        JsonNode jsonBody = this.getObjectMapper().readTree(response.getBody());
        JsonNode results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(expectedUuid.toString(), results.get(0).get("uuid").asText());

        testUserDetails.giveAccess(
                this.getPlugin().getAreaRestrictionDefinition().getAreaRestrictionTypeByName(
                        CprAreaRestrictionDefinition.RESTRICTIONTYPE_KOMMUNEKODER
                ).getRestriction(
                        CprAreaRestrictionDefinition.RESTRICTION_KOMMUNE_SERMERSOOQ
                )
        );
        this.applyAccess(testUserDetails);

        response = restSearch(searchParameters, "road");
        Assert.assertEquals(200, response.getStatusCode().value());
        jsonBody = this.getObjectMapper().readTree(response.getBody());
        results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(0, results.size());

        testUserDetails.giveAccess(
                this.getPlugin().getAreaRestrictionDefinition().getAreaRestrictionTypeByName(
                        CprAreaRestrictionDefinition.RESTRICTIONTYPE_KOMMUNEKODER
                ).getRestriction(
                        CprAreaRestrictionDefinition.RESTRICTION_KOMMUNE_KUJALLEQ
                )
        );
        this.applyAccess(testUserDetails);

        response = restSearch(searchParameters, "road");
        Assert.assertEquals(200, response.getStatusCode().value());
        jsonBody = this.getObjectMapper().readTree(response.getBody());
        results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(expectedUuid.toString(), results.get(0).get("uuid").asText());

    }

    @Test
    public void testRoadRecordTime() throws Exception {
        whitelistLocalhost();
        OffsetDateTime now = OffsetDateTime.now();
        ImportMetadata importMetadata = new ImportMetadata();
        Session session = this.getSessionManager().getSessionFactory().openSession();
        importMetadata.setSession(session);
        Transaction transaction = session.beginTransaction();
        importMetadata.setTransactionInProgress(true);
        loadRoad(importMetadata);
        transaction.commit();
        session.close();

        TestUserDetails testUserDetails = new TestUserDetails();
        testUserDetails.giveAccess(CprRolesDefinition.READ_CPR_ROLE);
        this.applyAccess(testUserDetails);

        ParameterMap searchParameters = new ParameterMap();
        searchParameters.add("registreringFra", now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        searchParameters.add("recordAfter", now.plusSeconds(5).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        ResponseEntity<String> response = restSearch(searchParameters, "road");
        Assert.assertEquals(200, response.getStatusCode().value());
        JsonNode jsonBody = this.getObjectMapper().readTree(response.getBody());
        JsonNode results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(0, results.size());

        searchParameters = new ParameterMap();
        searchParameters.add("registreringFra", now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        searchParameters.add("recordAfter", now.minusDays(1).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        response = restSearch(searchParameters, "road");
        Assert.assertEquals(200, response.getStatusCode().value());
        jsonBody = this.getObjectMapper().readTree(response.getBody());
        results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(9, results.size());
    }

}

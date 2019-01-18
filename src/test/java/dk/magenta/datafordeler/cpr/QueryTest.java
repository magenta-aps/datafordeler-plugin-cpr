package dk.magenta.datafordeler.cpr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.Application;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.fapi.FapiService;
import dk.magenta.datafordeler.core.fapi.ParameterMap;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.core.user.DafoUserManager;
import dk.magenta.datafordeler.cpr.data.person.PersonEntityManager;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceEntityManager;
import dk.magenta.datafordeler.cpr.data.road.RoadEntityManager;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QueryTest {

    @Autowired
    private CprPlugin plugin;

    @SpyBean
    private DafoUserManager dafoUserManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonEntityManager personEntityManager;

    @Autowired
    private RoadEntityManager roadEntityManager;

    @Autowired
    private ResidenceEntityManager residenceEntityManager;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SessionManager sessionManager;

    @After
    public void cleanup() {
        QueryManager.clearCaches();
    }

    public void loadPerson(ImportMetadata importMetadata) throws Exception {
        InputStream testData = QueryTest.class.getResourceAsStream("/persondata.txt");
        personEntityManager.parseData(testData, importMetadata);
        testData.close();
    }

    public void loadResidence(ImportMetadata importMetadata) throws Exception {
        InputStream testData = QueryTest.class.getResourceAsStream("/roaddata.txt");
        residenceEntityManager.parseData(testData, importMetadata);
        testData.close();
    }

    public void loadRoad(ImportMetadata importMetadata) throws Exception {
        InputStream testData = QueryTest.class.getResourceAsStream("/roaddata.txt");
        roadEntityManager.parseData(testData, importMetadata);
        testData.close();
    }

    private void applyAccess(TestUserDetails testUserDetails) {
        when(dafoUserManager.getFallbackUser()).thenReturn(testUserDetails);
    }
    private void whitelistLocalhost() {
        when(dafoUserManager.getIpWhitelist()).thenReturn(Collections.singleton("127.0.0.1"));
    }

    private ResponseEntity<String> restSearch(ParameterMap parameters, String type) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<String>("", headers);
        return this.restTemplate.exchange("/cpr/"+type+"/1/rest/search?" + parameters.asUrlParams(), HttpMethod.GET, httpEntity, String.class);
    }

    private ResponseEntity<String> uuidSearch(String id, String type) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<String>("", headers);
        return this.restTemplate.exchange("/cpr/"+type+"/1/rest/" + id, HttpMethod.GET, httpEntity, String.class);
    }


    @Test
    public void testPersonAccess() throws Exception {
        whitelistLocalhost();
        ImportMetadata importMetadata = new ImportMetadata();
        Session session = sessionManager.getSessionFactory().openSession();
        importMetadata.setSession(session);
        Transaction transaction = session.beginTransaction();
        importMetadata.setTransactionInProgress(true);
        loadPerson(importMetadata);
        transaction.commit();
        session.close();

        TestUserDetails testUserDetails = new TestUserDetails();

        ParameterMap searchParameters = new ParameterMap();
        searchParameters.add("fornavn", "Tester");
        ResponseEntity<String> response = restSearch(searchParameters, "person");
        Assert.assertEquals(403, response.getStatusCode().value());

        testUserDetails.giveAccess(CprRolesDefinition.READ_CPR_ROLE);
        this.applyAccess(testUserDetails);

        response = restSearch(searchParameters, "person");
        Assert.assertEquals(200, response.getStatusCode().value());
        JsonNode jsonBody = objectMapper.readTree(response.getBody());
        JsonNode results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("4ccc3b64-1779-38f2-a96c-458e541a010d", results.get(0).get("uuid").asText());


        testUserDetails.giveAccess(
                plugin.getAreaRestrictionDefinition().getAreaRestrictionTypeByName(
                        CprAreaRestrictionDefinition.RESTRICTIONTYPE_KOMMUNEKODER
                ).getRestriction(
                        CprAreaRestrictionDefinition.RESTRICTION_KOMMUNE_SERMERSOOQ
                )
        );
        this.applyAccess(testUserDetails);

        response = restSearch(searchParameters, "person");
        Assert.assertEquals(200, response.getStatusCode().value());
        jsonBody = objectMapper.readTree(response.getBody());
        results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(0, results.size());

        response = uuidSearch("4ccc3b64-1779-38f2-a96c-458e541a010d", "person");
        Assert.assertEquals(200, response.getStatusCode().value());
        jsonBody = objectMapper.readTree(response.getBody());
        results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(0, results.size());

        searchParameters.add("kommunekode", "95*");
        response = restSearch(searchParameters, "person");
        Assert.assertEquals(200, response.getStatusCode().value());
        jsonBody = objectMapper.readTree(response.getBody());
        results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(0, results.size());

        testUserDetails.giveAccess(
                plugin.getAreaRestrictionDefinition().getAreaRestrictionTypeByName(
                        CprAreaRestrictionDefinition.RESTRICTIONTYPE_KOMMUNEKODER
                ).getRestriction(
                        CprAreaRestrictionDefinition.RESTRICTION_KOMMUNE_QAASUITSUP
                )
        );
        this.applyAccess(testUserDetails);

        response = restSearch(searchParameters, "person");
        Assert.assertEquals(200, response.getStatusCode().value());
        jsonBody = objectMapper.readTree(response.getBody());
        results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("4ccc3b64-1779-38f2-a96c-458e541a010d", results.get(0).get("uuid").asText());

        response = uuidSearch("4ccc3b64-1779-38f2-a96c-458e541a010d", "person");
        Assert.assertEquals(200, response.getStatusCode().value());
        jsonBody = objectMapper.readTree(response.getBody());
        results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("4ccc3b64-1779-38f2-a96c-458e541a010d", results.get(0).get("uuid").asText());
    }

    @Test
    public void testPersonRecordTime() throws Exception {
        whitelistLocalhost();
        OffsetDateTime now = OffsetDateTime.now();
        ImportMetadata importMetadata = new ImportMetadata();
        Session session = sessionManager.getSessionFactory().openSession();
        importMetadata.setSession(session);
        Transaction transaction = session.beginTransaction();
        importMetadata.setTransactionInProgress(true);
        loadPerson(importMetadata);
        transaction.commit();
        session.close();

        TestUserDetails testUserDetails = new TestUserDetails();
        testUserDetails.giveAccess(CprRolesDefinition.READ_CPR_ROLE);
        this.applyAccess(testUserDetails);

        ParameterMap searchParameters = new ParameterMap();
        searchParameters.add("registreringFra", now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        searchParameters.add("recordAfter", now.plusSeconds(5).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        ResponseEntity<String> response = restSearch(searchParameters, "person");
        Assert.assertEquals(200, response.getStatusCode().value());
        JsonNode jsonBody = objectMapper.readTree(response.getBody());
        JsonNode results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());

        //System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(results));
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(4, results.get(0).size());

        searchParameters = new ParameterMap();
        searchParameters.add("registreringFra", now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        searchParameters.add("recordAfter", now.minusDays(1).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        searchParameters.add("fornavn", "Tester");
        searchParameters.add("fmt", "drv");

        response = restSearch(searchParameters, "person");
        Assert.assertEquals(200, response.getStatusCode().value());
        jsonBody = objectMapper.readTree(response.getBody());
        results = jsonBody.get("results");
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(results));
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(1, results.size());
    }


    @Test
    public void testResidenceAccess() throws Exception {
        whitelistLocalhost();
        ImportMetadata importMetadata = new ImportMetadata();
        Session session = sessionManager.getSessionFactory().openSession();
        importMetadata.setSession(session);
        Transaction transaction = session.beginTransaction();
        importMetadata.setTransactionInProgress(true);
        loadResidence(importMetadata);
        transaction.commit();
        session.close();

        TestUserDetails testUserDetails = new TestUserDetails();

        ParameterMap searchParameters = new ParameterMap();
        searchParameters.add("vejkode", "001");
        searchParameters.add("husnummer", "1");
        ResponseEntity<String> response = restSearch(searchParameters, "residence");
        Assert.assertEquals(403, response.getStatusCode().value());

        testUserDetails.giveAccess(CprRolesDefinition.READ_CPR_ROLE);
        this.applyAccess(testUserDetails);

        response = restSearch(searchParameters, "residence");
        Assert.assertEquals(200, response.getStatusCode().value());
        JsonNode jsonBody = objectMapper.readTree(response.getBody());
        JsonNode results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("1d4631ad-c49e-3c28-9de9-325be326b17a", results.get(0).get("uuid").asText());

        testUserDetails.giveAccess(
                plugin.getAreaRestrictionDefinition().getAreaRestrictionTypeByName(
                        CprAreaRestrictionDefinition.RESTRICTIONTYPE_KOMMUNEKODER
                ).getRestriction(
                        CprAreaRestrictionDefinition.RESTRICTION_KOMMUNE_SERMERSOOQ
                )
        );
        this.applyAccess(testUserDetails);

        response = restSearch(searchParameters, "residence");
        Assert.assertEquals(200, response.getStatusCode().value());
        jsonBody = objectMapper.readTree(response.getBody());
        results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(0, results.size());

        testUserDetails.giveAccess(
                plugin.getAreaRestrictionDefinition().getAreaRestrictionTypeByName(
                        CprAreaRestrictionDefinition.RESTRICTIONTYPE_KOMMUNEKODER
                ).getRestriction(
                        CprAreaRestrictionDefinition.RESTRICTION_KOMMUNE_KUJALLEQ
                )
        );
        this.applyAccess(testUserDetails);

        response = restSearch(searchParameters, "residence");
        Assert.assertEquals(200, response.getStatusCode().value());
        jsonBody = objectMapper.readTree(response.getBody());
        results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("1d4631ad-c49e-3c28-9de9-325be326b17a", results.get(0).get("uuid").asText());
    }


    @Test
    public void testResidenceRecordTime() throws Exception {
        whitelistLocalhost();
        OffsetDateTime now = OffsetDateTime.now();
        ImportMetadata importMetadata = new ImportMetadata();
        Session session = sessionManager.getSessionFactory().openSession();
        importMetadata.setSession(session);
        Transaction transaction = session.beginTransaction();
        importMetadata.setTransactionInProgress(true);
        loadResidence(importMetadata);
        transaction.commit();
        session.close();

        TestUserDetails testUserDetails = new TestUserDetails();
        testUserDetails.giveAccess(CprRolesDefinition.READ_CPR_ROLE);
        this.applyAccess(testUserDetails);

        ParameterMap searchParameters = new ParameterMap();
        searchParameters.add("registreringFra", now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        searchParameters.add("recordAfter", now.plusSeconds(5).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        ResponseEntity<String> response = restSearch(searchParameters, "residence");
        Assert.assertEquals(200, response.getStatusCode().value());
        JsonNode jsonBody = objectMapper.readTree(response.getBody());
        JsonNode results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(0, results.size());

        searchParameters = new ParameterMap();
        searchParameters.add("registreringFra", now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        searchParameters.add("recordAfter", now.minusDays(1).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        response = restSearch(searchParameters, "residence");
        Assert.assertEquals(200, response.getStatusCode().value());
        jsonBody = objectMapper.readTree(response.getBody());
        results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(2, results.size());
    }


    @Test
    public void testRoadAccess() throws Exception {
        whitelistLocalhost();
        ImportMetadata importMetadata = new ImportMetadata();
        Session session = sessionManager.getSessionFactory().openSession();
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
        JsonNode jsonBody = objectMapper.readTree(response.getBody());
        JsonNode results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("d318815f-1959-3b37-b173-b99b88935c82", results.get(0).get("uuid").asText());

        testUserDetails.giveAccess(
                plugin.getAreaRestrictionDefinition().getAreaRestrictionTypeByName(
                        CprAreaRestrictionDefinition.RESTRICTIONTYPE_KOMMUNEKODER
                ).getRestriction(
                        CprAreaRestrictionDefinition.RESTRICTION_KOMMUNE_SERMERSOOQ
                )
        );
        this.applyAccess(testUserDetails);

        response = restSearch(searchParameters, "road");
        Assert.assertEquals(200, response.getStatusCode().value());
        jsonBody = objectMapper.readTree(response.getBody());
        results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(0, results.size());

        testUserDetails.giveAccess(
                plugin.getAreaRestrictionDefinition().getAreaRestrictionTypeByName(
                        CprAreaRestrictionDefinition.RESTRICTIONTYPE_KOMMUNEKODER
                ).getRestriction(
                        CprAreaRestrictionDefinition.RESTRICTION_KOMMUNE_KUJALLEQ
                )
        );
        this.applyAccess(testUserDetails);

        response = restSearch(searchParameters, "road");
        Assert.assertEquals(200, response.getStatusCode().value());
        jsonBody = objectMapper.readTree(response.getBody());
        results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("d318815f-1959-3b37-b173-b99b88935c82", results.get(0).get("uuid").asText());

    }

    @Test
    public void testRoadRecordTime() throws Exception {
        whitelistLocalhost();
        OffsetDateTime now = OffsetDateTime.now();
        ImportMetadata importMetadata = new ImportMetadata();
        Session session = sessionManager.getSessionFactory().openSession();
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
        JsonNode jsonBody = objectMapper.readTree(response.getBody());
        JsonNode results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(0, results.size());

        searchParameters = new ParameterMap();
        searchParameters.add("registreringFra", now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        searchParameters.add("recordAfter", now.minusDays(1).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        response = restSearch(searchParameters, "road");
        Assert.assertEquals(200, response.getStatusCode().value());
        jsonBody = objectMapper.readTree(response.getBody());
        results = jsonBody.get("results");
        Assert.assertTrue(results.isArray());
        Assert.assertEquals(9, results.size());
    }


}

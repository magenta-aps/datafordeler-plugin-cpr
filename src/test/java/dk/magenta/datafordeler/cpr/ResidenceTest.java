package dk.magenta.datafordeler.cpr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.cpr.data.residence.*;
import dk.magenta.datafordeler.cpr.data.residence.data.ResidenceBaseData;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ResidenceTest extends TestBase {

    @Autowired
    private SessionManager sessionManager;

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


    private void loadResidence(ImportMetadata importMetadata) throws DataFordelerException, IOException {
        InputStream testData = ResidenceTest.class.getResourceAsStream("/roaddata.txt");
        residenceEntityManager.parseData(testData, importMetadata);
        testData.close();
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

}

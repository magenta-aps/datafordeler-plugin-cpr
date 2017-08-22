package dk.magenta.datafordeler.cpr;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.data.person.*;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceEntity;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceEntityManager;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceQuery;
import dk.magenta.datafordeler.cpr.data.road.*;
import dk.magenta.datafordeler.cpr.data.road.data.RoadMemoData;
import dk.magenta.datafordeler.cpr.data.road.data.RoadPostcodeData;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Created by lars on 14-06-17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ParseTest {

    @Autowired
    private QueryManager queryManager;

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

    @Test
    public void testParsePerson() throws IOException, ParseException {
        Session session = null;
        try {
            InputStream testData = ParseTest.class.getResourceAsStream("/persondata.txt");
            personEntityManager.parseRegistration(testData);

            PersonQuery query = new PersonQuery();
            //query.setCprNumber("121008217");
            query.setFirstName("Tester");
            session = sessionManager.getSessionFactory().openSession();

            try {
                List<PersonEntity> entities = queryManager.getAllEntities(session, query, PersonEntity.class);
                System.out.println(objectMapper.writeValueAsString(entities));
            } catch (DataFordelerException e) {
                e.printStackTrace();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Test
    public void testParseRoad() throws IOException, DataFordelerException {
        Session session = null;
        try {
            InputStream testData = ParseTest.class.getResourceAsStream("/roaddata.txt");
            long start = Instant.now().toEpochMilli();
            roadEntityManager.parseRegistration(testData);
            System.out.println("Parsed road data in "+ (Instant.now().toEpochMilli() - start) + " ms");
            session = sessionManager.getSessionFactory().openSession();

            RoadQuery query = new RoadQuery();
            query.setMunicipalityCode("0730");
            query.setCode("0004");

            List<RoadEntity> entities = queryManager.getAllEntities(session, query, RoadEntity.class);
            Assert.assertEquals(1, entities.size());
            RoadEntity roadEntity = entities.get(0);
            Assert.assertEquals(CprPlugin.getDomain(), roadEntity.getDomain());
            Assert.assertEquals(730, roadEntity.getMunicipalityCode());
            Assert.assertEquals(4, roadEntity.getRoadCode());
            Assert.assertEquals(2, roadEntity.getRegistrations().size());
            RoadRegistration registration1 = roadEntity.getRegistrations().get(0);
            Assert.assertEquals(0, registration1.getSequenceNumber());
            Assert.assertEquals(OffsetDateTime.parse("2006-12-22T12:00:00+01:00"), registration1.getRegistrationFrom());
            Assert.assertEquals(OffsetDateTime.parse("2008-05-30T09:11:00+02:00"), registration1.getRegistrationTo());
            Assert.assertEquals(2, registration1.getEffects().size());
            RoadEffect effect11 = registration1.getEffects().get(0);
            Assert.assertEquals(OffsetDateTime.parse("1900-01-01T12:00:00+01:00"), effect11.getEffectFrom());
            Assert.assertNull(effect11.getEffectTo());
            Assert.assertEquals("Aalborggade", effect11.getData().get("addressingName"));
            Assert.assertEquals("Aalborggade", effect11.getData().get("name"));
            Assert.assertFalse(effect11.isUncertainFrom());
            Assert.assertFalse(effect11.isUncertainTo());

            RoadEffect effect12 = registration1.getEffects().get(1);
            Assert.assertEquals(OffsetDateTime.parse("1996-03-12T07:42:00+01:00"), effect12.getEffectFrom());
            Assert.assertNull(effect12.getEffectTo());
            List<RoadMemoData> memo = (List<RoadMemoData>) effect12.getData().get("memo");
            Assert.assertEquals(1, memo.get(0).getMemoNumber());
            Assert.assertEquals("HUSNR.1 - BØRNEINSTITUTION -", memo.get(0).getMemoText());
            Assert.assertEquals(2, memo.get(1).getMemoNumber());
            Assert.assertEquals("HUSNR.2 - EGEDAL -", memo.get(1).getMemoText());
            Assert.assertEquals(3, memo.get(2).getMemoNumber());
            Assert.assertEquals("HUSNR.3 - KIRKE -", memo.get(2).getMemoText());

            RoadRegistration registration2 = roadEntity.getRegistrations().get(1);
            Assert.assertEquals(1, registration2.getSequenceNumber());
            Assert.assertEquals(OffsetDateTime.parse("2008-05-30T09:11:00+02:00"), registration2.getRegistrationFrom());
            Assert.assertNull(registration2.getRegistrationTo());
            Assert.assertEquals(1, registration2.getEffects().size());
            RoadEffect effect21 = registration2.getEffects().get(0);
            Assert.assertNull(effect21.getEffectFrom());
            Assert.assertNull(effect21.getEffectTo());
            Assert.assertFalse(effect21.isUncertainFrom());
            Assert.assertFalse(effect21.isUncertainTo());
            List<RoadPostcodeData> post = (List<RoadPostcodeData>) effect21.getData().get("postcode");
            Assert.assertEquals(2, post.size());
            Assert.assertEquals("001", post.get(0).getHouseNumberFrom());
            Assert.assertEquals("999", post.get(0).getHouseNumberTo());
            Assert.assertFalse(post.get(0).isEven());
            Assert.assertEquals(8940, post.get(0).getPostCode().getCode());
            Assert.assertEquals("Randers SV", post.get(0).getPostCode().getText());
            Assert.assertEquals("002", post.get(1).getHouseNumberFrom());
            Assert.assertEquals("998", post.get(1).getHouseNumberTo());
            Assert.assertTrue(post.get(1).isEven());
            Assert.assertEquals(8940, post.get(1).getPostCode().getCode());
            Assert.assertEquals("Randers SV", post.get(1).getPostCode().getText());
            Assert.assertFalse(effect21.isUncertainFrom());
            Assert.assertFalse(effect21.isUncertainTo());

        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Test
    public void testParseResidence() throws IOException, ParseException {
        Session session = null;
        try {
            InputStream testData = ParseTest.class.getResourceAsStream("/roaddata.txt");
            residenceEntityManager.parseRegistration(testData);

            ResidenceQuery query = new ResidenceQuery();
            //query.setMunicipalityCode("0730");
            //query.setCode("0012");
            //query.setMunicipalityCode();
            /*session = sessionManager.getSessionFactory().openSession();

            try {
                List<RoadEntity> entities = queryManager.getAllEntities(session, query, RoadEntity.class);
                System.out.println(objectMapper.writeValueAsString(entities));
            } catch (DataFordelerException e) {
                e.printStackTrace();
            }*/
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

}

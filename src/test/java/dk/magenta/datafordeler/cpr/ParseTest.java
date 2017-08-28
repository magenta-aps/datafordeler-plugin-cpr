package dk.magenta.datafordeler.cpr;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.util.Equality;
import dk.magenta.datafordeler.cpr.data.CprEntityManager;
import dk.magenta.datafordeler.cpr.data.person.*;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceEntity;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceEntityManager;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceQuery;
import dk.magenta.datafordeler.cpr.data.road.*;
import dk.magenta.datafordeler.cpr.data.road.data.RoadMemoData;
import dk.magenta.datafordeler.cpr.data.road.data.RoadPostcodeData;
import dk.magenta.datafordeler.cpr.records.Bitemporality;
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
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entities));
            Assert.assertEquals(1, entities.size());
            RoadEntity roadEntity = entities.get(0);
            Assert.assertEquals(CprPlugin.getDomain(), roadEntity.getDomain());
            Assert.assertEquals(730, roadEntity.getMunicipalityCode());
            Assert.assertEquals(4, roadEntity.getRoadCode());
            Assert.assertEquals(2, roadEntity.getRegistrations().size());
            RoadRegistration registration1 = roadEntity.getRegistrations().get(0);
            Assert.assertEquals(0, registration1.getSequenceNumber());
            Assert.assertTrue(Equality.equal(OffsetDateTime.parse("2006-12-22T12:00:00+01:00"), registration1.getRegistrationFrom()));
            Assert.assertTrue(Equality.equal(OffsetDateTime.parse("2008-05-30T09:11:00+02:00"), registration1.getRegistrationTo()));
            Assert.assertEquals(2, registration1.getEffects().size());
            RoadEffect effect11 = registration1.getEffects().get(0);
            Assert.assertTrue(Equality.equal(OffsetDateTime.parse("1900-01-01T12:00:00+01:00"), effect11.getEffectFrom()));
            Assert.assertNull(effect11.getEffectTo());
            Assert.assertEquals("Aalborggade", effect11.getData().get("addressingName"));
            Assert.assertEquals("Aalborggade", effect11.getData().get("name"));
            Assert.assertFalse(effect11.isUncertainFrom());
            Assert.assertFalse(effect11.isUncertainTo());

            RoadEffect effect12 = registration1.getEffects().get(1);
            Assert.assertTrue(Equality.equal(OffsetDateTime.parse("1996-03-12T07:42:00+01:00"), effect12.getEffectFrom()));
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
            Assert.assertTrue(Equality.equal(OffsetDateTime.parse("2008-05-30T09:11:00+02:00"), registration2.getRegistrationFrom()));
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

    @Test
    public void testFindRegistrations() {
        OffsetDateTime open = null;
        OffsetDateTime time1 = OffsetDateTime.parse("2001-01-01T00:00:00Z");
        OffsetDateTime time2 = OffsetDateTime.parse("2002-02-02T00:00:00Z");
        OffsetDateTime time3 = OffsetDateTime.parse("2003-03-03T00:00:00Z");
        OffsetDateTime time4 = OffsetDateTime.parse("2004-04-04T00:00:00Z");

        // Find/create registrations on empty entity, with specific range
        RoadEntity entity1 = new RoadEntity();
        List<RoadRegistration> result1 = entity1.findRegistrations(time1, time4);
        Assert.assertEquals(1, result1.size());
        Assert.assertTrue(Equality.equal(time1, result1.get(0).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(time4, result1.get(0).getRegistrationTo()));

        // Find/create registrations on empty entity, with open start
        RoadEntity entity2 = new RoadEntity();
        List<RoadRegistration> result2 = entity2.findRegistrations(open, time3);
        Assert.assertEquals(1, result2.size());
        Assert.assertTrue(Equality.equal(open, result2.get(0).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(time3, result2.get(0).getRegistrationTo()));

        // Find/create registrations on empty entity, with open end
        RoadEntity entity3 = new RoadEntity();
        List<RoadRegistration> result3 = entity3.findRegistrations(time2, open);
        Assert.assertEquals(1, result3.size());
        Assert.assertTrue(Equality.equal(time2, result3.get(0).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(open, result3.get(0).getRegistrationTo()));

        // Find/create registrations on empty entity, with open start and end
        RoadEntity entity4 = new RoadEntity();
        List<RoadRegistration> result4 = entity4.findRegistrations(open, open);
        Assert.assertEquals(1, result4.size());
        Assert.assertTrue(Equality.equal(open, result4.get(0).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(open, result4.get(0).getRegistrationTo()));

        // Find/create registrations on entity with one registration, with matching range
        RoadEntity entity5 = new RoadEntity();
        RoadRegistration registration5 = entity5.createRegistration();
        registration5.setRegistrationFrom(time1);
        registration5.setRegistrationTo(time4);
        List<RoadRegistration> result5 = entity5.findRegistrations(time1, time4);
        Assert.assertEquals(1, result5.size());
        Assert.assertTrue(Equality.equal(time1, result5.get(0).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(time4, result5.get(0).getRegistrationTo()));

        // Find/create registrations on entity with one registration, with off-range
        RoadEntity entity6 = new RoadEntity();
        RoadRegistration registration6 = entity6.createRegistration();
        registration6.setRegistrationFrom(time2);
        registration6.setRegistrationTo(time4);
        List<RoadRegistration> result6 = entity6.findRegistrations(time1, time3);
        Assert.assertEquals(2, result6.size());
        Assert.assertTrue(Equality.equal(time1, result6.get(0).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(time2, result6.get(0).getRegistrationTo()));
        Assert.assertTrue(Equality.equal(time2, result6.get(1).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(time3, result6.get(1).getRegistrationTo()));

        // Find/create registrations on entity with one registration, with range off at the end
        RoadEntity entity7 = new RoadEntity();
        RoadRegistration registration7 = entity7.createRegistration();
        registration7.setRegistrationFrom(time1);
        registration7.setRegistrationTo(time3);
        List<RoadRegistration> result7 = entity7.findRegistrations(time1, time4);
        Assert.assertEquals(2, result7.size());
        Assert.assertTrue(Equality.equal(time1, result7.get(0).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(time3, result7.get(0).getRegistrationTo()));
        Assert.assertTrue(Equality.equal(time3, result7.get(1).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(time4, result7.get(1).getRegistrationTo()));

        // Find/create registrations on entity with two registrations, with matching range
        RoadEntity entity8 = new RoadEntity();
        RoadRegistration registration8a = entity8.createRegistration();
        registration8a.setRegistrationFrom(time1);
        registration8a.setRegistrationTo(time2);
        RoadRegistration registration8b = entity8.createRegistration();
        registration8b.setRegistrationFrom(time2);
        registration8b.setRegistrationTo(time3);
        List<RoadRegistration> result8 = entity8.findRegistrations(time1, time3);
        Assert.assertEquals(2, result8.size());
        Assert.assertTrue(Equality.equal(time1, result8.get(0).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(time2, result8.get(0).getRegistrationTo()));
        Assert.assertTrue(Equality.equal(time2, result8.get(1).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(time3, result8.get(1).getRegistrationTo()));

        // Find/create registrations on entity with two registrations, with non-matching range
        RoadEntity entity9 = new RoadEntity();
        RoadRegistration registration9a = entity9.createRegistration();
        registration9a.setRegistrationFrom(open);
        registration9a.setRegistrationTo(time2);
        RoadRegistration registration9b = entity9.createRegistration();
        registration9b.setRegistrationFrom(time2);
        registration9b.setRegistrationTo(time4);
        List<RoadRegistration> result9 = entity9.findRegistrations(time1, time3);
        Assert.assertEquals(2, result9.size());
        Assert.assertTrue(Equality.equal(time1, result9.get(0).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(time2, result9.get(0).getRegistrationTo()));
        Assert.assertTrue(Equality.equal(time2, result9.get(1).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(time3, result9.get(1).getRegistrationTo()));

        // Find/create registrations on entity with two registrations (non-aligned), with non-matching range
        RoadEntity entity10 = new RoadEntity();
        RoadRegistration registration10a = entity10.createRegistration();
        registration10a.setRegistrationFrom(open);
        registration10a.setRegistrationTo(time2);
        RoadRegistration registration10b = entity10.createRegistration();
        registration10b.setRegistrationFrom(time3);
        registration10b.setRegistrationTo(open);
        List<RoadRegistration> result10 = entity10.findRegistrations(time1, time4);
        Assert.assertEquals(3, result10.size());
        Assert.assertTrue(Equality.equal(time1, result10.get(0).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(time2, result10.get(0).getRegistrationTo()));
        Assert.assertTrue(Equality.equal(time2, result10.get(1).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(time3, result10.get(1).getRegistrationTo()));
        Assert.assertTrue(Equality.equal(time3, result10.get(2).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(time4, result10.get(2).getRegistrationTo()));


        // Find/create registrations on entity with two registrations (non-aligned), with open range
        RoadEntity entity11 = new RoadEntity();
        RoadRegistration registration11a = entity11.createRegistration();
        registration11a.setRegistrationFrom(time1);
        registration11a.setRegistrationTo(time2);
        RoadRegistration registration11b = entity11.createRegistration();
        registration11b.setRegistrationFrom(time3);
        registration11b.setRegistrationTo(time4);
        List<RoadRegistration> result11 = entity11.findRegistrations(open, open);
        Assert.assertEquals(5, result11.size());
        Assert.assertEquals(5, entity11.getRegistrations().size());
        Assert.assertTrue(Equality.equal(open, result11.get(0).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(time1, result11.get(0).getRegistrationTo()));
        Assert.assertTrue(Equality.equal(time1, result11.get(1).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(time2, result11.get(1).getRegistrationTo()));
        Assert.assertTrue(Equality.equal(time2, result11.get(2).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(time3, result11.get(2).getRegistrationTo()));
        Assert.assertTrue(Equality.equal(time3, result11.get(3).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(time4, result11.get(3).getRegistrationTo()));
        Assert.assertTrue(Equality.equal(time4, result11.get(4).getRegistrationFrom()));
        Assert.assertTrue(Equality.equal(open, result11.get(4).getRegistrationTo()));

        List<RoadRegistration> result11a = entity11.findRegistrations(open, open);
        Assert.assertEquals(5, result11a.size());
        Assert.assertEquals(5, entity11.getRegistrations().size());
    }

}

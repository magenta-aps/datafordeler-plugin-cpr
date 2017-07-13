package dk.magenta.datafordeler.cpr;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.fapi.OutputWrapper;
import dk.magenta.datafordeler.cpr.data.person.*;
import dk.magenta.datafordeler.cpr.data.person.PersonOutputWrapper;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceEntityManager;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceQuery;
import dk.magenta.datafordeler.cpr.data.road.RoadEntity;
import dk.magenta.datafordeler.cpr.data.road.RoadEntityManager;
import dk.magenta.datafordeler.cpr.data.road.RoadQuery;
import org.hibernate.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;
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

    private OutputWrapper<PersonEntity> personOutputWrapper = new PersonOutputWrapper();

    @Test
    public void testParsePerson() throws IOException, ParseException {
        Session session = null;
        try {
            InputStream testData = ParseTest.class.getResourceAsStream("/persondata.txt");
            personEntityManager.parseRegistration(testData);

            PersonQuery query = new PersonQuery();
            //query.setPersonnummer("121008217");
            query.setFornavn("Tester");
            session = sessionManager.getSessionFactory().openSession();

            try {
                List<PersonEntity> entities = queryManager.getAllEntities(session, query, PersonEntity.class);
                personOutputWrapper.wrapResults(entities);
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
    public void testParseRoad() throws IOException, ParseException {
        Session session = null;
        try {
            InputStream testData = ParseTest.class.getResourceAsStream("/roaddata.txt");
            roadEntityManager.parseRegistration(testData);

            RoadQuery query = new RoadQuery();
            //query.setCprKommunekode("0730");
            //query.setPostnummer("0012");
            query.setNavn("Aalborggade");
            session = sessionManager.getSessionFactory().openSession();

            try {
                List<RoadEntity> entities = queryManager.getAllEntities(session, query, RoadEntity.class);
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
    public void testParseResidence() throws IOException, ParseException {
        Session session = null;
        try {
            InputStream testData = ParseTest.class.getResourceAsStream("/roaddata.txt");
            residenceEntityManager.parseRegistration(testData);

            ResidenceQuery query = new ResidenceQuery();
            //query.setCprKommunekode("0730");
            //query.setPostnummer("0012");
            //query.setCprKommunekode();
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

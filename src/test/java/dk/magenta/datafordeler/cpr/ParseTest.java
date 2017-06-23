package dk.magenta.datafordeler.cpr;

import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.cpr.data.person.*;
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

    @Test
    public void testParse() throws IOException, ParseException {
        Session session = null;
        try {
            InputStream testData = ParseTest.class.getResourceAsStream("/cprdata.txt");
            personEntityManager.parseRegistration(testData);

            PersonQuery query = new PersonQuery();
            //query.setCprNumber("121008217");
            query.setFirstName("Tester");
            session = sessionManager.getSessionFactory().openSession();

            try {
                List<PersonEntity> entities = queryManager.getAllEntities(session, query, PersonEntity.class);
                System.out.println(entities);
            } catch (DataFordelerException e) {
                e.printStackTrace();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

}

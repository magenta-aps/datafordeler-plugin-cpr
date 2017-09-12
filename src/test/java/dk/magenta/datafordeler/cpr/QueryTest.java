package dk.magenta.datafordeler.cpr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.magenta.datafordeler.core.Application;
import dk.magenta.datafordeler.core.Engine;
import dk.magenta.datafordeler.core.Pull;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.plugin.Plugin;
import dk.magenta.datafordeler.core.role.SystemRole;
import dk.magenta.datafordeler.core.user.DafoUserManager;
import dk.magenta.datafordeler.core.user.UserProfile;
import dk.magenta.datafordeler.cpr.configuration.CprConfiguration;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import dk.magenta.datafordeler.cpr.data.person.PersonEntity;
import dk.magenta.datafordeler.cpr.data.person.PersonEntityManager;
import dk.magenta.datafordeler.cpr.data.person.PersonOutputWrapper;
import dk.magenta.datafordeler.cpr.data.person.PersonQuery;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceEntityManager;
import dk.magenta.datafordeler.cpr.data.road.RoadEntityManager;
import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QueryTest {

    @Autowired
    private CprPlugin plugin;

    @Autowired
    private Engine engine;

    @Autowired
    private QueryManager queryManager;

    @Autowired
    private SessionManager sessionManager;

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

    private PersonOutputWrapper personOutputWrapper = new PersonOutputWrapper();

    @Test
    public void pull() throws Exception {
        this.setupFTP();
        Pull pull = new Pull(engine, plugin);
        pull.run();
    }

    @Test
    public void loadPerson() throws Exception {
        InputStream testData = ParseTest.class.getResourceAsStream("/persondata.txt");
        personEntityManager.parseRegistration(testData);
        testData.close();
    }

    @Test
    public void testQueryPerson() throws Exception {
        Session session = null;
        try {
            loadPerson();

            PersonQuery query = new PersonQuery();
            query.setEfternavn("Testersen");
            session = sessionManager.getSessionFactory().openSession();

            List<PersonEntity> entities = queryManager.getAllEntities(session, query, PersonEntity.class);
            long start = Instant.now().toEpochMilli();
            List<Object> wrapped = personOutputWrapper.wrapResults(entities);
            System.out.println(Instant.now().toEpochMilli() - start + "ms");

            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(wrapped));

            //Assert.assertEquals(1, wrapped.size());
            //Assert.assertTrue(wrapped.get(0) instanceof ObjectNode);
            //ObjectNode objectNode = (ObjectNode) wrapped.get(0);


        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private void setupFTP() throws Exception {
        int port = 2101;
        CprConfiguration configuration = ((CprConfigurationManager) plugin.getConfigurationManager()).getConfiguration();
        configuration.setPersonRegisterAddress("ftps://localhost:" + port);
        String username = configuration.getPersonRegisterFtpUsername();
        String password = configuration.getPersonRegisterFtpPassword();
        InputStream contents = FullTest.class.getResourceAsStream("/persondata.txt");
        File tempFile = File.createTempFile("cprdata", "txt");
        tempFile.createNewFile();
        FileUtils.copyInputStreamToFile(contents, tempFile);
        FtpService ftp = new FtpService();
        ftp.startServer(username, password, port, Collections.singletonList(tempFile));
    }

    private void giveAccess(SystemRole... rolesDefinitions) {
        ArrayList<String> roleNames = new ArrayList<>();
        for (SystemRole role : rolesDefinitions) {
            roleNames.add(role.getRoleName());
        }
        UserProfile testUserProfile = new UserProfile("TestProfile", roleNames);

        TestUserDetails testUserDetails = new TestUserDetails();
        testUserDetails.addUserProfile(testUserProfile);
        when(dafoUserManager.getFallbackUser()).thenReturn(testUserDetails);
    }
}

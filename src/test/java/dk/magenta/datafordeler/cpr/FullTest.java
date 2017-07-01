package dk.magenta.datafordeler.cpr;

import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.io.Event;
import dk.magenta.datafordeler.core.util.ItemInputStream;
import dk.magenta.datafordeler.cpr.configuration.CprConfiguration;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import dk.magenta.datafordeler.cpr.data.person.PersonEntity;
import dk.magenta.datafordeler.cpr.data.person.PersonEntityManager;
import dk.magenta.datafordeler.cpr.data.person.PersonQuery;
import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by lars on 20-06-17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class FullTest {

    @Autowired
    private CprPlugin plugin;

    @Autowired
    private PersonEntityManager entityManager;

    @Autowired
    private QueryManager queryManager;

    @Autowired
    private SessionManager sessionManager;

    @Test
    public void test() throws Exception {
        /*File tempFile = null;
        FtpService ftp = new FtpService();
        try {
            int port = 2101;
            CprConfiguration configuration = ((CprConfigurationManager) plugin.getConfigurationManager()).getConfiguration();
            configuration.setRegisterAddress("ftp://localhost:" + port);
            String username = configuration.getFtpUsername();
            String password = configuration.getFtpPassword();
            InputStream contents = FullTest.class.getResourceAsStream("/persondata.txt");
            tempFile = File.createTempFile("cprdata", "txt");
            tempFile.createNewFile();
            FileUtils.copyInputStreamToFile(contents, tempFile);

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            final File tempFileClosure = tempFile;
            Future<Boolean> future = executorService.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    ftp.startServer(username, password, port, Collections.singletonList(tempFileClosure));
                    URI dataLocation = new URI("ftp://localhost:"+port+"/"+tempFileClosure.getName());

                    ItemInputStream<Event> eventStream = plugin.getRegisterManager().pullEvents(dataLocation);
                    if (eventStream != null) {
                        Event event;
                        while ((event = eventStream.next()) != null) {
                            entityManager.parseRegistration(event.getObjektData());
                        }
                        eventStream.close();

                        Session session = null;
                        try {
                            session = sessionManager.getSessionFactory().openSession();
                            PersonQuery query = new PersonQuery();
                            query.setFirstName("Tester");
                            List<PersonEntity> entities = queryManager.getAllEntities(session, query, PersonEntity.class);
                            Assert.assertEquals(1, entities.size());
                        } catch (DataFordelerException e) {
                            e.printStackTrace();
                        } finally {
                            if (session != null) {
                                session.close();
                            }
                        }
                    }
                    return true;
                }
            });
            future.get(20, TimeUnit.SECONDS);
            executorService.shutdownNow();
        } finally {
            ftp.stopServer();
            if (tempFile != null) {
                tempFile.delete();
            }
        }*/
    }

}

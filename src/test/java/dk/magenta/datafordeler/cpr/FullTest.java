package dk.magenta.datafordeler.cpr;

import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.io.PluginSourceData;
import dk.magenta.datafordeler.core.util.ItemInputStream;
import dk.magenta.datafordeler.cpr.configuration.CprConfiguration;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import dk.magenta.datafordeler.cpr.data.person.PersonEntity;
import dk.magenta.datafordeler.cpr.data.person.PersonEntityManager;
import dk.magenta.datafordeler.cpr.data.person.PersonQuery;
import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Ignore;
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
    private PersonEntityManager personEntityManager;

    @Autowired
    private QueryManager queryManager;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private CprRegisterManager registerManager;

    @Test
    public void test() throws Exception {
        File tempFile = null;
        FtpService ftp = new FtpService();
        Session session = null;
        try {
            int port = 21001;
            CprConfiguration configuration = ((CprConfigurationManager) plugin.getConfigurationManager()).getConfiguration();
            configuration.setPersonRegisterAddress("ftps://localhost:" + port);
            String username = configuration.getPersonRegisterFtpUsername();
            String password = configuration.getPersonRegisterFtpPassword();
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
                    System.out.println("server started on port "+port);
                    URI dataLocation = new URI("ftps://localhost:"+port+"/"+tempFileClosure.getName());

                    ItemInputStream<? extends PluginSourceData> eventStream = plugin.getRegisterManager().pullEvents(dataLocation, personEntityManager);
                    System.out.println("pulled");
                    if (eventStream != null) {
                        PluginSourceData data;
                        while ((data = eventStream.next()) != null) {
                            personEntityManager.parseRegistration(data.getData());
                        }
                        eventStream.close();
                        System.out.println("parsed & stored");

                        Session session = null;
                        try {
                            session = sessionManager.getSessionFactory().openSession();
                            PersonQuery query = new PersonQuery();
                            query.setFornavn("Tester");
                            List<PersonEntity> entities = queryManager.getAllEntities(session, query, PersonEntity.class);
                            Assert.assertEquals(1, entities.size());
                            System.out.println("stored");
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
            if (session != null) {
                session.close();
            }
        }
    }

}

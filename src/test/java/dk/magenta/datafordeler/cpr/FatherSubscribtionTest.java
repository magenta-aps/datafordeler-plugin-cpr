package dk.magenta.datafordeler.cpr;


import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.Application;
import dk.magenta.datafordeler.core.Engine;
import dk.magenta.datafordeler.core.Pull;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.plugin.FtpCommunicator;
import dk.magenta.datafordeler.cpr.configuration.CprConfiguration;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import dk.magenta.datafordeler.cpr.data.CprRecordEntityManager;
import dk.magenta.datafordeler.cpr.data.person.*;
import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FatherSubscribtionTest {

    @Autowired
    private CprPlugin plugin;

    @Autowired
    private Engine engine;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
    private CprConfigurationManager cprConfigurationManager;

    @SpyBean
    private CprRegisterManager cprRegisterManager;

    @SpyBean
    private PersonEntityManager personEntityManager;

    @After
    public void cleanup() {
        QueryManager.clearCaches();
    }

    private static SSLSocketFactory getTrustAllSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustManager = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };
        SSLContext sslContext = null;

        sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustManager, new SecureRandom());

        return sslContext.getSocketFactory();
    }


    @Test
    public void testParentSubscription() throws Exception {

        CprConfiguration configuration = ((CprConfigurationManager) plugin.getConfigurationManager()).getConfiguration();
        when(cprConfigurationManager.getConfiguration()).thenReturn(configuration);
        when(personEntityManager.isSetupSubscriptionEnabled()).thenReturn(true);
        when(personEntityManager.getCustomerId()).thenReturn(1234);
        when(personEntityManager.getJobId()).thenReturn(123456);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return null;
            }
        }).when(personEntityManager).getLastUpdated(any(Session.class));


        File localSubFolder = File.createTempFile("foo", "bar");
        localSubFolder.delete();
        localSubFolder.mkdirs();
        when(personEntityManager.getLocalSubscriptionFolder()).thenReturn(localSubFolder.getAbsolutePath());

        CprRegisterManager registerManager = (CprRegisterManager) plugin.getRegisterManager();
        registerManager.setProxyString(null);

        doAnswer(new Answer<FtpCommunicator>() {
            @Override
            public FtpCommunicator answer(InvocationOnMock invocation) throws Throwable {
                FtpCommunicator ftpCommunicator = (FtpCommunicator) invocation.callRealMethod();
                ftpCommunicator.setSslSocketFactory(FatherSubscribtionTest.getTrustAllSSLSocketFactory());
                return ftpCommunicator;
            }
        }).when(cprRegisterManager).getFtpCommunicator(any(URI.class), any(CprRecordEntityManager.class));


        String username = "test";
        String password = "test";


        int personPort = 2101;
        InputStream personContents = this.getClass().getResourceAsStream("/personWithParentRelation.txt");
        File personFile = File.createTempFile("persondata", "txt");
        personFile.createNewFile();
        FileUtils.copyInputStreamToFile(personContents, personFile);
        personContents.close();

        FtpService personFtp = new FtpService();
        personFtp.startServer(username, password, personPort, Collections.singletonList(personFile));

        configuration.setPersonRegisterType(CprConfiguration.RegisterType.REMOTE_FTP);
        configuration.setPersonRegisterFtpAddress("ftps://localhost:" + personPort);
        configuration.setPersonRegisterFtpUsername(username);
        configuration.setPersonRegisterFtpPassword(password);
        configuration.setPersonRegisterDataCharset(CprConfiguration.Charset.UTF_8);

        configuration.setRoadRegisterType(CprConfiguration.RegisterType.DISABLED);
        configuration.setResidenceRegisterType(CprConfiguration.RegisterType.DISABLED);


        Pull pull = new Pull(engine, plugin);
        pull.run();


        personFtp.stopServer();
        personFile.delete();

        personContents = this.getClass().getResourceAsStream("/personWithParentRelation.txt");
        personFile = File.createTempFile("persondata2", "txt");
        personFile.createNewFile();
        FileUtils.copyInputStreamToFile(personContents, personFile);
        personContents.close();

        personFtp = new FtpService();
        personFtp.startServer(username, password, personPort, Collections.singletonList(personFile));

        pull.run();

        personFtp.stopServer();
        personFile.delete();

        Session session = sessionManager.getSessionFactory().openSession();
        try {
            List<PersonSubscription> subscriptions = QueryManager.getAllItems(session, PersonSubscription.class);
            //There is 5 fathers in the test-file
            //One should not be added to subscribtion becrause the child is more than 18 years old.
            //One person should not be added becrause the father allready exists as a person.
            Assert.assertEquals(3, subscriptions.size());
            File[] subFiles = localSubFolder.listFiles();
            Assert.assertEquals(1, subFiles.length);
        } finally {
            session.close();
            localSubFolder.delete();
        }
    }


}

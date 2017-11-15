package dk.magenta.datafordeler.cpr;

import dk.magenta.datafordeler.core.Application;
import dk.magenta.datafordeler.core.Engine;
import dk.magenta.datafordeler.core.Pull;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.RecordCollection;
import dk.magenta.datafordeler.core.database.RecordData;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.plugin.FtpCommunicator;
import dk.magenta.datafordeler.cpr.configuration.CprConfiguration;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import dk.magenta.datafordeler.cpr.data.CprEntityManager;
import dk.magenta.datafordeler.cpr.data.person.PersonEntity;
import dk.magenta.datafordeler.cpr.data.person.PersonQuery;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceEntity;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceQuery;
import dk.magenta.datafordeler.cpr.data.road.RoadEntity;
import dk.magenta.datafordeler.cpr.data.road.RoadQuery;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PullTest {

    @Autowired
    private CprPlugin plugin;

    @Autowired
    private Engine engine;

    @Autowired
    private SessionManager sessionManager;

    @SpyBean
    private CprConfigurationManager cprConfigurationManager;

    @SpyBean
    private CprRegisterManager cprRegisterManager;

    @After
    public void cleanup() {
        QueryManager.clearCaches();
    }

    private static SSLSocketFactory getTrustAllSSLSocketFactory() {
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
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManager, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sslContext.getSocketFactory();
    }

    @Test
    public void pull() throws Exception {

        CprConfiguration configuration = ((CprConfigurationManager) plugin.getConfigurationManager()).getConfiguration();
        when(cprConfigurationManager.getConfiguration()).thenReturn(configuration);

        CprRegisterManager registerManager = (CprRegisterManager) plugin.getRegisterManager();
        registerManager.setProxyString(null);

        doAnswer(new Answer<FtpCommunicator>() {
            @Override
            public FtpCommunicator answer(InvocationOnMock invocation) throws Throwable {
                FtpCommunicator ftpCommunicator = (FtpCommunicator) invocation.callRealMethod();
                ftpCommunicator.setSslSocketFactory(PullTest.getTrustAllSSLSocketFactory());
                return ftpCommunicator;
            }
        }).when(cprRegisterManager).getFtpCommunicator(any(URI.class), any(CprEntityManager.class));


        String username = "test";
        String password = "test";


        InputStream personContents = this.getClass().getResourceAsStream("/persondata.txt");
        File personFile = File.createTempFile("persondata", "txt");
        personFile.createNewFile();
        FileUtils.copyInputStreamToFile(personContents, personFile);
        personContents.close();

        FtpService personFtp = new FtpService();
        int personPort = 2101;
        personFtp.startServer(username, password, personPort, Collections.singletonList(personFile));

        configuration.setPersonRegisterType(CprConfiguration.RegisterType.REMOTE_FTP);
        configuration.setPersonRegisterFtpAddress("ftps://localhost:" + personPort);
        configuration.setPersonRegisterFtpUsername(username);
        configuration.setPersonRegisterFtpPassword(password);
        configuration.setPersonRegisterDataCharset(CprConfiguration.Charset.UTF_8);



        InputStream roadContents = this.getClass().getResourceAsStream("/roaddata.txt");
        File roadFile = File.createTempFile("roaddata", "txt");
        roadFile.createNewFile();
        FileUtils.copyInputStreamToFile(roadContents, roadFile);
        roadContents.close();

        FtpService roadFtp = new FtpService();
        int roadPort = 2102;
        roadFtp.startServer(username, password, roadPort, Collections.singletonList(roadFile));

        configuration.setRoadRegisterType(CprConfiguration.RegisterType.REMOTE_FTP);
        configuration.setRoadRegisterFtpAddress("ftps://localhost:" + roadPort);
        configuration.setRoadRegisterFtpUsername(username);
        configuration.setRoadRegisterFtpPassword(password);
        configuration.setRoadRegisterDataCharset(CprConfiguration.Charset.UTF_8);



        InputStream residenceContents = this.getClass().getResourceAsStream("/roaddata.txt");
        File residenceFile = File.createTempFile("residencedata", "txt");
        residenceFile.createNewFile();
        FileUtils.copyInputStreamToFile(residenceContents, residenceFile);
        residenceContents.close();

        FtpService residenceFtp = new FtpService();
        int residencePort = 2103;
        residenceFtp.startServer(username, password, residencePort, Collections.singletonList(residenceFile));

        configuration.setResidenceRegisterType(CprConfiguration.RegisterType.REMOTE_FTP);
        configuration.setResidenceRegisterFtpAddress("ftps://localhost:" + residencePort);
        configuration.setResidenceRegisterFtpUsername(username);
        configuration.setResidenceRegisterFtpPassword(password);
        configuration.setResidenceRegisterDataCharset(CprConfiguration.Charset.UTF_8);




        Pull pull = new Pull(engine, plugin);
        pull.run();



        personFtp.stopServer();
        personFile.delete();

        roadFtp.stopServer();
        roadFile.delete();

        residenceFtp.stopServer();
        residenceFile.delete();



        Session session = sessionManager.getSessionFactory().openSession();
        try {

            PersonQuery personQuery = new PersonQuery();
            personQuery.setFornavn("Tester");
            List<PersonEntity> personEntities = QueryManager.getAllEntities(session, personQuery, PersonEntity.class);
            Assert.assertEquals(1, personEntities.size());
            Assert.assertEquals(PersonEntity.generateUUID("0101001234"), personEntities.get(0).getUUID());

            RoadQuery roadQuery = new RoadQuery();
            roadQuery.addKommunekode(730);
            roadQuery.setVejkode(4);
            List<RoadEntity> roadEntities = QueryManager.getAllEntities(session, roadQuery, RoadEntity.class);
            Assert.assertEquals(1, roadEntities.size());
            Assert.assertEquals(RoadEntity.generateUUID(730, 4), roadEntities.get(0).getUUID());

            ResidenceQuery residenceQuery = new ResidenceQuery();
            residenceQuery.addKommunekode(360);
            residenceQuery.setVejkode(206);
            residenceQuery.setHusnummer("44E");
            List<ResidenceEntity> residenceEntities = QueryManager.getAllEntities(session, residenceQuery, ResidenceEntity.class);
            Assert.assertEquals(1, residenceEntities.size());
            Assert.assertEquals(ResidenceEntity.generateUUID(360, 206, "44E", "", ""), residenceEntities.get(0).getUUID());

            RecordCollection firstRecordCollection = residenceEntities.get(0).getRegistrations().get(0).getEffects().get(0).getDataItems().get(0).getRecordSet();
            Assert.assertEquals(1, firstRecordCollection.getRecords().size());
            RecordData firstRecordData = firstRecordCollection.getRecords().iterator().next();
            Assert.assertEquals("00203600206044E      200612221200 199109231200000000000000Provstelunden", firstRecordData.getSourceData());

        } finally {
            session.close();
        }

    }

}

package dk.magenta.datafordeler.cpr;

import dk.magenta.datafordeler.core.Application;
import dk.magenta.datafordeler.core.Engine;
import dk.magenta.datafordeler.core.Pull;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.RecordCollection;
import dk.magenta.datafordeler.core.database.RecordData;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.cpr.configuration.CprConfiguration;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import dk.magenta.datafordeler.cpr.data.person.PersonEntity;
import dk.magenta.datafordeler.cpr.data.person.PersonQuery;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceEntity;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceQuery;
import dk.magenta.datafordeler.cpr.data.road.RoadEntity;
import dk.magenta.datafordeler.cpr.data.road.RoadQuery;
import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

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

    @Autowired
    private QueryManager queryManager;

    private FtpService ftp;
    private File tempFile;

    private void setupFTP() throws Exception {
        int port = 2101;
        CprConfiguration configuration = ((CprConfigurationManager) plugin.getConfigurationManager()).getConfiguration();
        configuration.setPersonRegisterAddress("ftps://localhost:" + port);
        String username = configuration.getPersonRegisterFtpUsername();
        String password = configuration.getPersonRegisterFtpPassword();
        InputStream contents = this.getClass().getResourceAsStream("/persondata.txt");
        tempFile = File.createTempFile("cprdata", "txt");
        tempFile.createNewFile();
        FileUtils.copyInputStreamToFile(contents, tempFile);
        ftp = new FtpService();
        ftp.startServer(username, password, port, Collections.singletonList(tempFile));
    }

    private void stopFTP() {
        ftp.stopServer();
        tempFile.delete();
    }

    @Test
    public void pull() throws Exception {
        this.setupFTP();
        Pull pull = new Pull(engine, plugin);
        pull.run();
        this.stopFTP();

        Session session = sessionManager.getSessionFactory().openSession();
        try {

            PersonQuery personQuery = new PersonQuery();
            personQuery.setFornavn("Tester");
            List<PersonEntity> personEntities = queryManager.getAllEntities(session, personQuery, PersonEntity.class);
            Assert.assertEquals(1, personEntities.size());
            Assert.assertEquals(PersonEntity.generateUUID("0101001234"), personEntities.get(0).getUUID());

            RoadQuery roadQuery = new RoadQuery();
            roadQuery.addKommunekode(730);
            roadQuery.setVejkode(4);
            List<RoadEntity> roadEntities = queryManager.getAllEntities(session, roadQuery, RoadEntity.class);
            Assert.assertEquals(1, roadEntities.size());
            Assert.assertEquals(RoadEntity.generateUUID(730, 4), roadEntities.get(0).getUUID());

            ResidenceQuery residenceQuery = new ResidenceQuery();
            residenceQuery.addKommunekode(360);
            residenceQuery.setVejkode(206);
            residenceQuery.setHusnummer("44E");
            List<ResidenceEntity> residenceEntities = queryManager.getAllEntities(session, residenceQuery, ResidenceEntity.class);
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

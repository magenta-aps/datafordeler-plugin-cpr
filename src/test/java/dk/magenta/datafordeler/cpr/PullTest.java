package dk.magenta.datafordeler.cpr;

import dk.magenta.datafordeler.core.Application;
import dk.magenta.datafordeler.core.Engine;
import dk.magenta.datafordeler.core.Pull;
import dk.magenta.datafordeler.cpr.configuration.CprConfiguration;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PullTest {

    @Autowired
    private CprPlugin plugin;

    @Autowired
    private Engine engine;

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

    @Test
    public void pull() throws Exception {
        this.setupFTP();
        Pull pull = new Pull(engine, plugin);
        pull.run();
    }

}

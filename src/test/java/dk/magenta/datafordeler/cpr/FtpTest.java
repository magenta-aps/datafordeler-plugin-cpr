package dk.magenta.datafordeler.cpr;

import dk.magenta.datafordeler.core.io.Event;
import dk.magenta.datafordeler.core.util.ItemInputStream;
import dk.magenta.datafordeler.cpr.configuration.CprConfiguration;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by lars on 20-06-17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class FtpTest {

    @Autowired
    CprPlugin plugin;

    @Test
    public void testDownload() throws Exception {
        /*FtpService ftp = new FtpService();
        File tempFile = null;
        try {
            int port = 2101;
            CprConfiguration configuration = ((CprConfigurationManager) plugin.getConfigurationManager()).getConfiguration();
            configuration.setRegisterAddress("ftp://localhost:" + port);
            String username = configuration.getFtpUsername();
            String password = configuration.getFtpPassword();
            InputStream contents = FtpTest.class.getResourceAsStream("/persondata.txt");
            tempFile = File.createTempFile("cprdata", ".txt");
            tempFile.createNewFile();
            FileUtils.copyInputStreamToFile(contents, tempFile);

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            final File tempFileClosure = tempFile;
            Future<Boolean> future = executorService.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    ftp.startServer(username, password, port, Collections.singletonList(tempFileClosure));
                    URI dataLocation = new URI("ftp://localhost:"+port+"/");
                    System.out.println(dataLocation);

                    ItemInputStream<Event> eventStream = plugin.getRegisterManager().pullEvents(dataLocation);
                    if (eventStream != null) {
                        Event event;
                        while ((event = eventStream.next()) != null) {
                            System.out.println(event.toString());
                        }
                        eventStream.close();
                    }
                    return true;
                }
            });
            future.get(10, TimeUnit.SECONDS);
            executorService.shutdownNow();
        } finally {
            ftp.stopServer();
            if (tempFile != null) {
                tempFile.delete();
            }
        }*/
    }

}

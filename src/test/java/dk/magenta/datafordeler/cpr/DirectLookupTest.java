package dk.magenta.datafordeler.cpr;

import dk.magenta.datafordeler.core.Application;
import dk.magenta.datafordeler.cpr.configuration.CprConfiguration;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class DirectLookupTest {

    @Autowired
    private CprPlugin plugin;

    @SpyBean
    private CprConfigurationManager configurationManager;

    @Autowired
    private CprDirectLookup directLookup;

    @Test
    @Ignore
    public void testLookup() throws Exception {

        CprConfiguration configuration = ((CprConfigurationManager) plugin.getConfigurationManager()).getConfiguration();
        when(configurationManager.getConfiguration()).thenReturn(configuration);
        configuration.setDirectHost("direkte-demo.cpr.dk");
        configuration.setDirectUsername("some user");
        configuration.setDirectPasswordPasswordEncryptionFile(
                new File("local/cpr/keyfile.json")
        );
        configuration.setDirectPassword("nice try");
        configuration.setDirectCustomerNumber(0);

        String response = directLookup.lookup("0707614285");

        System.out.println(response);



    }
}

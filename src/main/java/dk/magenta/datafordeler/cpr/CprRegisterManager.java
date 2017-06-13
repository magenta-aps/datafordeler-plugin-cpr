package dk.magenta.datafordeler.cpr;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.plugin.Communicator;
import dk.magenta.datafordeler.core.plugin.HttpCommunicator;
import dk.magenta.datafordeler.core.plugin.Plugin;
import dk.magenta.datafordeler.core.plugin.RegisterManager;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by lars on 16-05-17.
 */
@Component
public class CprRegisterManager extends RegisterManager {

    private HttpCommunicator commonFetcher;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CprConfigurationManager configurationManager;

    @Autowired
    private CprPlugin plugin;

    private Logger log = LogManager.getLogger("CprRegisterManager");

    public CprRegisterManager() {
        this.commonFetcher = new HttpCommunicator();
    }

    @PostConstruct
    public void init() {
        try {
            this.baseEndpoint = new URI(this.configurationManager.getConfiguration().getRegisterAddress());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Logger getLog() {
        return this.log;
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    private URI baseEndpoint;

    @Override
    public URI getBaseEndpoint() {
        return this.baseEndpoint;
    }



    @Override
    protected Communicator getEventFetcher() {
        return this.commonFetcher;
    }

    @Override
    protected ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    @Override
    protected URI getEventInterface() {
        return expandBaseURI(this.getBaseEndpoint(), "/getNewEvents");
    }

    @Override
    protected Communicator getChecksumFetcher() {
        return this.commonFetcher;
    }

    @Override
    public URI getListChecksumInterface(String schema, OffsetDateTime from) {
        ListHashMap<String, String> parameters = new ListHashMap<>();
        if (schema != null) {
            parameters.add("objectType", schema);
        }
        if (from != null) {
            parameters.add("timestamp", from.format(DateTimeFormatter.ISO_DATE_TIME));
        }
        return expandBaseURI(this.getBaseEndpoint(), "/listChecksums", RegisterManager.joinQueryString(parameters), null);
    }

    public String getPullCronSchedule() {
        System.out.println(this);
        System.out.println(this.configurationManager);
        System.out.println(this.configurationManager.getConfiguration());
        System.out.println(this.configurationManager.getConfiguration().getPullCronSchedule());
        return this.configurationManager.getConfiguration().getPullCronSchedule();
    }

}

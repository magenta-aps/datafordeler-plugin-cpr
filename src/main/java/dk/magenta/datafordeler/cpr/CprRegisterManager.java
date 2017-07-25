package dk.magenta.datafordeler.cpr;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.exception.DataStreamException;
import dk.magenta.datafordeler.core.exception.HttpStatusException;
import dk.magenta.datafordeler.core.exception.WrongSubclassException;
import dk.magenta.datafordeler.core.io.Event;
import dk.magenta.datafordeler.core.io.PluginSourceData;
import dk.magenta.datafordeler.core.plugin.*;
import dk.magenta.datafordeler.core.util.CloseDetectInputStream;
import dk.magenta.datafordeler.core.util.ItemInputStream;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.configuration.CprConfiguration;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import dk.magenta.datafordeler.cpr.data.CprEntityManager;
import dk.magenta.datafordeler.cpr.data.person.PersonEntity;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceEntity;
import dk.magenta.datafordeler.cpr.data.road.RoadEntity;
import dk.magenta.datafordeler.cpr.synchronization.CprSourceData;
import dk.magenta.datafordeler.cpr.synchronization.LocalCopyFtpCommunicator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by lars on 16-05-17.
 */
@Component
public class CprRegisterManager extends RegisterManager {

    private LocalCopyFtpCommunicator commonFetcher;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CprConfigurationManager configurationManager;

    @Autowired
    private CprPlugin plugin;

    private Logger log = LogManager.getLogger("CprRegisterManager");

    @Value("${dafo.cpr.proxy-url:}")
    private String proxyString;

    @Value("${dafo.cpr.local-copy-folder:}")
    private String localCopyFolder;


    public CprRegisterManager() {

    }

    @PostConstruct
    public void init() throws IOException {
        CprConfiguration configuration = this.configurationManager.getConfiguration();
        if (this.localCopyFolder == null || this.localCopyFolder.isEmpty()) {
            this.localCopyFolder = "cache";
        }
        this.commonFetcher = new LocalCopyFtpCommunicator(
            configuration.getFtpUsername(),
            configuration.getFtpPassword(),
            configuration.getFtps(),
            this.proxyString,
            this.localCopyFolder
        );
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
        try {
            return new URI(this.configurationManager.getConfiguration().getRegisterAddress());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
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
    protected URI getEventInterface(EntityManager entityManager) {
        return expandBaseURI(this.getBaseEndpoint(), "/ud/");
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
        return this.configurationManager.getConfiguration().getPullCronSchedule();
    }




    @Override
    public ItemInputStream<? extends PluginSourceData> pullEvents(URI eventInterface, EntityManager entityManager) throws DataFordelerException {
        if (!(entityManager instanceof CprEntityManager)) {
            throw new WrongSubclassException(CprEntityManager.class, entityManager);
        }
        LocalCopyFtpCommunicator ftpFetcher = (LocalCopyFtpCommunicator) this.getEventFetcher();
        InputStream responseBody = null;
        try {
            responseBody = ftpFetcher.fetch(eventInterface);
        } catch (HttpStatusException | DataStreamException e) {
            this.log.error(e);
        }
        return this.parseEventResponse(responseBody, entityManager);
    }

    @Override
    protected ItemInputStream<? extends PluginSourceData> parseEventResponse(InputStream inputStream, EntityManager entityManager) throws DataFordelerException {
        if (!(entityManager instanceof CprEntityManager)) {
            throw new WrongSubclassException(CprEntityManager.class, entityManager);
        }
        return this.parseEventResponse(inputStream, entityManager.getSchema());
    }

    private ItemInputStream<CprSourceData> parseEventResponse(final InputStream responseBody, final String schema) throws DataFordelerException {
        final int linesPerEvent = 100;
        PipedInputStream inputStream = new PipedInputStream();

        try {
            final PipedOutputStream outputStream = new PipedOutputStream(inputStream);

            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(responseBody, Charset.forName("iso-8859-1")));

                            int eventCount = 0;
                            try {
                                String line;

                                // One line per event
                                int lineCount = 0;
                                ArrayList<String> lines = new ArrayList<>();
                                while ((line = responseReader.readLine()) != null) {
                                    lines.add(line);
                                    System.out.println("Package "+eventCount+", line "+lineCount);
                                    lineCount++;
                                    if (lineCount >= linesPerEvent) {
                                        objectOutputStream.writeObject(CprRegisterManager.this.wrap(lines, schema));
                                        lines.clear();
                                        lineCount = 0;
                                        eventCount++;
                                    }
                                }
                                if (lineCount > 0) {
                                    objectOutputStream.writeObject(CprRegisterManager.this.wrap(lines, schema));
                                    eventCount++;
                                }
                                CprRegisterManager.this.log.info("Packed "+eventCount+" data objects");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        try {
                            responseReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    try {
                        objectOutputStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            t.start();

            return new ItemInputStream<>(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private CprSourceData wrap(List<String> lines, String schema) {
        StringJoiner s = new StringJoiner("\n");
        for (String line : lines) {
            s.add(line);
        }
        return new CprSourceData(schema, s.toString());
    }

}

package dk.magenta.datafordeler.cpr;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.exception.DataStreamException;
import dk.magenta.datafordeler.core.exception.WrongSubclassException;
import dk.magenta.datafordeler.core.io.PluginSourceData;
import dk.magenta.datafordeler.core.plugin.Communicator;
import dk.magenta.datafordeler.core.plugin.EntityManager;
import dk.magenta.datafordeler.core.plugin.Plugin;
import dk.magenta.datafordeler.core.plugin.RegisterManager;
import dk.magenta.datafordeler.core.util.ItemInputStream;
import dk.magenta.datafordeler.cpr.configuration.CprConfiguration;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import dk.magenta.datafordeler.cpr.data.CprEntityManager;
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
import java.nio.charset.Charset;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by lars on 16-05-17.
 */
@Component
public class CprRegisterManager extends RegisterManager {

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

    /**
    * RegisterManager initialization; set up configuration and source fetcher.
    * We store fetched data in a local cache, so create a random folder for that.
    */
    @PostConstruct
    public void init() throws IOException {
        CprConfiguration configuration = this.configurationManager.getConfiguration();
        if (this.localCopyFolder == null || this.localCopyFolder.isEmpty()) {
            File temp = File.createTempFile("datafordeler-cache","");
            temp.delete();
            temp.mkdir();
            this.localCopyFolder = temp.getAbsolutePath();
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

    @Override
    public URI getBaseEndpoint() {
        return null;
    }



    @Override
    protected Communicator getEventFetcher() {
        return null;
    }

    @Override
    protected ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    @Override
    public URI getEventInterface(EntityManager entityManager) throws DataFordelerException {
        if (entityManager instanceof CprEntityManager) {
            CprConfiguration configuration = this.configurationManager.getConfiguration();
            return configuration.getRegisterURI((CprEntityManager) entityManager);
        }
        return null;
    }

    @Override
    protected Communicator getChecksumFetcher() {
        return null;
    }

    @Override
    public URI getListChecksumInterface(String schema, OffsetDateTime from) {
        return null;
    }

    public String getPullCronSchedule() {
        // TODO: make entitymanager specific
        return this.configurationManager.getConfiguration().getPersonRegisterPullCronSchedule();
    }



    /**
    * Pull data from the data source denoted by eventInterface, using the 
    * mechanism appropriate for the source.
    * For CPR, this is done using a LocalCopyFtpCommunicator, where we fetch all 
    * files in a remote folder (known to be text files).
    * We then package chunks of lines into Events, and feed them into a stream for 
    * returning.
    */
    @Override
    protected ItemInputStream<? extends PluginSourceData> pullEvents(URI eventInterface, EntityManager entityManager) throws DataFordelerException {
        if (!(entityManager instanceof CprEntityManager)) {
            throw new WrongSubclassException(CprEntityManager.class, entityManager);
        }
        CprEntityManager cprEntityManager = (CprEntityManager) entityManager;
        InputStream responseBody = null;
        String scheme = eventInterface.getScheme();
        switch (scheme) {
            case "file":
                try {
                    responseBody = new FileInputStream(new File(eventInterface));
                } catch (FileNotFoundException e) {
                    this.log.error(e);
                    throw new DataStreamException(e);
                }
                break;

            case "ftp":
            case "ftps":
                CprConfiguration configuration = this.configurationManager.getConfiguration();
                try {
                    LocalCopyFtpCommunicator ftpFetcher = new LocalCopyFtpCommunicator(
                            configuration.getRegisterFtpUsername(cprEntityManager),
                            configuration.getRegisterFtpPassword(cprEntityManager),
                            "ftps".equals(scheme),
                            this.proxyString,
                            this.localCopyFolder
                    );
                    responseBody = ftpFetcher.fetch(eventInterface);
                } catch (IOException e) {
                    this.log.error(e);
                    throw new DataStreamException(e);
                }
                break;
        }
        if (responseBody == null) {
            throw new DataStreamException("No data received from source " + eventInterface.toString());
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
            final int dataIdBase = responseBody.hashCode();

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
                            lineCount++;
                            if (lineCount >= linesPerEvent) {
                                objectOutputStream.writeObject(CprRegisterManager.this.wrap(lines, schema, dataIdBase, eventCount));
                                lines.clear();
                                lineCount = 0;
                                eventCount++;
                            }
                        }
                        if (lineCount > 0) {
                            objectOutputStream.writeObject(CprRegisterManager.this.wrap(lines, schema, dataIdBase, eventCount));
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

    private CprSourceData wrap(List<String> lines, String schema, int base, int index) {
        StringJoiner s = new StringJoiner("\n");
        for (String line : lines) {
            s.add(line);
        }
        return new CprSourceData(schema, s.toString(), base + ":" + index);
    }

}

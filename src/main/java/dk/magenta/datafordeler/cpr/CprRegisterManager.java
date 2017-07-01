package dk.magenta.datafordeler.cpr;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.exception.DataStreamException;
import dk.magenta.datafordeler.core.exception.HttpStatusException;
import dk.magenta.datafordeler.core.io.Event;
import dk.magenta.datafordeler.core.plugin.*;
import dk.magenta.datafordeler.core.util.CloseDetectInputStream;
import dk.magenta.datafordeler.core.util.ItemInputStream;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.configuration.CprConfiguration;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import dk.magenta.datafordeler.cpr.data.person.PersonEntity;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceEntity;
import dk.magenta.datafordeler.cpr.data.road.RoadEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by lars on 16-05-17.
 */
@Component
public class CprRegisterManager extends RegisterManager {

    private FtpCommunicator commonFetcher;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CprConfigurationManager configurationManager;

    @Autowired
    private CprPlugin plugin;

    private Logger log = LogManager.getLogger("CprRegisterManager");

    public CprRegisterManager() {

    }

    @PostConstruct
    public void init() {

        CprConfiguration configuration = this.configurationManager.getConfiguration();
        this.commonFetcher = new FtpCommunicator(configuration.getFtpUsername(), configuration.getFtpPassword(), configuration.getFtps());
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
    protected URI getEventInterface() {
        return expandBaseURI(this.getBaseEndpoint(), "/cprdata.txt");
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

    public ItemInputStream<Event> pullEvents(URI eventInterface) throws DataFordelerException {
        FtpCommunicator eventCommunicator = (FtpCommunicator) this.getEventFetcher();
        CprConfiguration configuration = configurationManager.getConfiguration();

        PipedInputStream inputStream = new PipedInputStream();
        final PipedOutputStream outputStream;
        try {
            outputStream = new PipedOutputStream(inputStream);
            System.out.println("outputStream: "+outputStream);
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            System.out.println("objectOutputStream: "+objectOutputStream);

            final List<EntityManager> entityManagers = new ArrayList<>(this.entityManagers);
            final URI baseEndpoint = this.baseEndpoint;

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    for (EntityManager entityManager : entityManagers) {
                        InputStream responseBody = null;
                        String schema = entityManager.getSchema();

                        try {
                            switch (schema) {
                                case PersonEntity.schema:
                                    responseBody = eventCommunicator.fetch(
                                            CprRegisterManager.this.getEventInterface()
                                    );
                                    break;
                                case RoadEntity.schema:
                                    try {
                                        responseBody = new FileInputStream(new File(configuration.getCprRoadDataLocation()));
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case ResidenceEntity.schema:
                                    try {
                                        responseBody = new FileInputStream(new File(configuration.getCprResidenceDataLocation()));
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                            }
                        } catch (HttpStatusException e1) {
                            e1.printStackTrace();
                        } catch (DataStreamException e1) {
                            e1.printStackTrace();
                        }
                        if (responseBody != null) {
                            System.out.println("got a response body");

                            final BufferedReader responseReader = new BufferedReader(new InputStreamReader(responseBody));
                            System.out.println("inputStream: " + inputStream);
                            System.out.println("dataStream: " + responseReader);


                            int count = 0;
                            try {
                                String line;

                                // One line per event
                                while ((line = responseReader.readLine()) != null) {
                                    System.out.println("got a response line");
                                    objectOutputStream.writeObject(CprRegisterManager.this.wrap(Collections.singletonList(line), schema));
                                    count++;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {

                                System.out.println("Wrote " + count + " events");

                                try {
                                    responseReader.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    try {
                        objectOutputStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            t.start();

            //System.out.println("There are "+entityManagerParseStreams.size()+" streams");
            return new ItemInputStream<>(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Event wrap(List<String> lines, String schema) {
        Event event = new Event();
        event.setEventID(UUID.randomUUID().toString());
        event.setBeskedVersion("1.0");
        StringJoiner s = new StringJoiner("\n");
        for (String line : lines) {
            s.add(line);
        }
        event.setDataskema(schema);
        event.setObjektData(s.toString());
        return event;
    }



}

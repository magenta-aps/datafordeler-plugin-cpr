package dk.magenta.datafordeler.cpr.direct;

import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.cpr.configuration.CprConfiguration;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import dk.magenta.datafordeler.cpr.records.Mapping;
import dk.magenta.datafordeler.cpr.records.person.PersonRecord;
import dk.magenta.datafordeler.cpr.records.person.data.PersonCoreDataRecord;
import dk.magenta.datafordeler.cpr.records.person.data.PersonStatusDataRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

@Component
public class CprDirectLookup {

    @Autowired
    private CprConfigurationManager configurationManager;

    private String authToken;

    private SSLSocketFactory socketFactory;

    private static final int ERR_TOKEN_EXPIRED = 7;

    private Logger log = LogManager.getLogger(CprDirectLookup.class.getCanonicalName());

    @PostConstruct
    public void init() {
        this.socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    }

    private CprConfiguration getConfiguration() {
        return configurationManager.getConfiguration();
    }

    private boolean login() throws IOException, GeneralSecurityException {
        CprConfiguration configuration = this.getConfiguration();

        String transactionCode = configuration.getDirectTransactionCode();
        String customerNumber = String.format("%04d", configuration.getDirectCustomerNumber());
        String username = String.format("%-8.8s", configuration.getDirectUsername());
        String password = String.format("%-8.8s", configuration.getDirectPassword());

        log.info("Logging in to CPR Direkte with customerNumber "+customerNumber);

        String LOGON_SUBSCRIPTION_TYPE = "9"; // constant in LOGON record
        String LOGON_DATA_TYPE = "0"; // constant in LOGON record

        // build logon request - a mix of constants and user-specified information
        String requestBody = String.format("%-35.35s",
                String.format("%s,%s%s%s%s%s",
                        transactionCode,
                        customerNumber,
                        LOGON_SUBSCRIPTION_TYPE,
                        LOGON_DATA_TYPE,
                        username,
                        password
                )); // record should always be 35 bytes

        String response = this.request(requestBody);

        // error code in response starts at position 22 in response
        int errorCode = Integer.parseInt(response.substring(22, 24));

        if (errorCode != 0) {
            log.info("Login failed with error code: " + errorCode + ", errorText: "+response);
            return false;
        }

        // parse out token, used for authentication on subsequent requests
        this.authToken = response.substring(6, 14);

        return true;
    }

    public String lookup(String pnr) throws Exception {

        if (this.authToken == null) {
            if (!this.login()) {
                throw new Exception("Login failed");
            }
        }

        CprConfiguration configuration = this.getConfiguration(); // TODO: Cache configuration?

        for (int attempt = 0; attempt<3; attempt++) {


            String dataType = "06"; // "00" can also be used if no DATA section is required

            // build lookup request
            String request = String.format("%-39.39s",
                    configuration.getDirectTransactionCode() +
                            "," +
                            String.format("%04d", configuration.getDirectCustomerNumber()) +
                            dataType +
                            this.authToken +
                            configuration.getDirectUsername() +
                            "00" +
                            pnr
            ); // must be 39 bytes

            String response = this.request(request);

            int errorCode = this.parseErrorCode(response);
            if (errorCode == 0) {
                // All ok, return response
                return response;
            } else if (errorCode == ERR_TOKEN_EXPIRED) {
                // Login and try again
                if (!this.login()) {
                    // If login failed, bail
                    return null;
                }
            } else {
                // Some other error, bail
                return null;
            }
        }
        return null;
    }

    private SSLSocket getSocket() throws IOException {
        CprConfiguration configuration = this.getConfiguration();
        return (SSLSocket) this.socketFactory.createSocket(
                configuration.getDirectHost(),
                configuration.getDirectPort()
        );
    }

    private String request(String request) throws IOException {
        SSLSocket client = this.getSocket();
        OutputStream os = client.getOutputStream();
        os.write(request.getBytes());
        os.flush();

        InputStream is = client.getInputStream();

        byte[] response = new byte[4096];
        int totalResponseLength = 0;
        int responseLength;

        while ((responseLength = is.read(response)) > -1) {
            totalResponseLength += responseLength;
        }

        client.close(); // CPR closes socket, so do we

        byte[] baCompleteResponse = new byte[totalResponseLength];
        System.arraycopy(response, 0, baCompleteResponse, 0, totalResponseLength);

        String responseString = new String(baCompleteResponse, Charset.forName("ISO-8859-1"));
        System.out.println("Response: " + responseString);

        return responseString;
    }

    private int parseErrorCode(String response) {
        int startErrorResponse = 22; // start of error code in response body
        int startDataSection = 28; // start of DATA section in response

        int errorCode = Integer.parseInt(
                response.substring(startErrorResponse, startErrorResponse + 2)
        );

        if (errorCode != 0) {
            // extended reason code available after standard error code
            int reasonCode = Integer.parseInt(response.substring(startErrorResponse + 2, startErrorResponse + 6));
            String reasonText = response.substring(startDataSection).trim();
            log.info("Error reasonCode: "+reasonCode+", reasonText: "+reasonText);
        }

        return errorCode;
    }

    public void parseResponse(String response) throws Exception {
        // dataType determines what records we received, and allows us to parse appropriately
        int dataType = Integer.parseInt(response.substring(5, 6));

        switch (dataType) {
            case 0:
                // only header record is present, see output of response above
                System.out.println("Only header record present. End.");
                break;
            case 6:
                /* one or more data records are present, so we need to determine which based on
                   record numbers. Can also be hardcoded based on which records you have agreed
                   to receive from CPR system */
                ListHashMap<RecordType, String> records = this.getAvailableRecords(response);

                if (records.size() == 0) {
                    System.out.println("No records found in response.");
                    return;
                }

                System.out.println("records: "+records);

                int startOfRecord;

                for (RecordType recordType : records.keySet()) {
                    for (String line : records.get(recordType)) {
                        switch (recordType) {
                            case PERSON_INFO:
                                PersonRecord personRecord = new PersonRecord(line, personRecordMapping);

                                System.out.println("personRecord: "+personRecord);
                                /*
                                * Create record instances
                                * Create entity
                                * from instances, obtain data records
                                * add to entity while ignoring historical data
                                * */

                        }
                    }
                }

                break;
            default:
                System.err.println("Unrecognized record data type: " + dataType);
        }
    }

    private static Mapping personRecordMapping = new Mapping();
    static {
        personRecordMapping.add("pnrgaeld", 14, 10);
        personRecordMapping.add("status", 24, 2);
        personRecordMapping.add("statushaenstart", 26, 12);
        personRecordMapping.add("statusdto_umrk", 38, 1);
        personRecordMapping.add("koen", 39, 1);
        personRecordMapping.add("foed_dt", 40, 10);
        personRecordMapping.add("foed_dt_umrk", 50, 1);
        personRecordMapping.add("start_dt-person", 51, 10);
        personRecordMapping.add("start_dt_umrk-person", 61, 1);
        personRecordMapping.add("slut_dt-person", 62, 10);
        personRecordMapping.add("slut_dt_umrk-person", 72, 1);
        personRecordMapping.add("stilling", 73, 34);
    }

    private ListHashMap<RecordType, String> getAvailableRecords(String response) throws Exception {
        ListHashMap<RecordType, String> records = new ListHashMap<>();
        int start = 28; // start of DATA section in response

        // programmatically find out which records are included in the response, and where they begin
        while (start < response.length()) {
            // record type is always 3 characters in length
            String recordType = response.substring(start, start + 3);
            boolean foundRecord = false;

            for (RecordType rec : RecordType.values()) {
                if (recordType.equals(rec.getValue())) {
                    int end = start + rec.getLength();
                    records.add(rec, response.substring(start, end));
                    start = end;
                    foundRecord = true;
                    break;
                }
            }

            if (!foundRecord) {
                start += recordType.length(); // avoid infinite loops
            }
        }
        return records;
    }

    private enum RecordType {
        START("000", 35),
        PERSON_INFO("001", 106),
        CURRENT_ADDR("002", 306), // assume type 'A' record
        PLAIN_ADDR("003", 249),
        PROTECTION("004", 37),
        FOREIGN_ADDR("005", 200),
        CONTACT_ADDR("006", 203),

        DISAPPEARANCE("007", 26),
        NAME("008", 193),
        BIRTHPLACE("009",37),
        CITIZENSHIP("010", 30),
        CHURCH("011", 25),
        CIVILSTATUS("012",95),
        SEPARATION("013", 36),
        CHILDREN("014", 23),
        PARENTS("015", 147),
        CUSTODY("016", 58),
        UNMANAGE("017", 272),
        MUNICIPAL_RELATION("018", 60),
        CREDIT_WARNING("050", 29), // NB: credit warning record first in production 1/1/2017
        UNMANAGE_EXTRA("052", 287),
        END("999", 21);

        private String value;
        private int recordLength;

        /**
         * Constructor for RecordType enums.
         *
         * @param value        The record number as defined in PNR specification.
         * @param recordLength The length of the record as defined in PNR specification.
         */
        RecordType(String value, int recordLength) {
            this.value = value;
            this.recordLength = recordLength;
        }

        /**
         * Returns the record number for this enum.
         *
         * @return Returns the record number of this enum as a String value.
         */
        public String getValue() {
            return this.value;
        }

        /**
         * Returns the length of this record type. as an integer.
         *
         * @return Integer value containing length of this record type.
         */
        public int getLength() {
            return this.recordLength;
        }
    }

    private static String substr(String s, int start, int length) {
        return s.substring(start, start + length);
    }

    private static int substrInt(String s, int start, int length) {
        return Integer.parseInt(substr(s, start, length));
    }


}

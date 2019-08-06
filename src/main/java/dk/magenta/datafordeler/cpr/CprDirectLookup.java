package dk.magenta.datafordeler.cpr;

import dk.magenta.datafordeler.cpr.configuration.CprConfiguration;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

@Component
public class CprDirectLookup {

    @Autowired
    private CprConfigurationManager configurationManager;

    private String authToken;

    private SSLSocketFactory socketFactory;

    private static final int ERR_TOKEN_EXPIRED = 7;

    private Logger log = LogManager.getLogger(CprDirectLookup.class.getCanonicalName());


    private CprConfiguration getConfiguration() {
        CprConfiguration configuration = configurationManager.getConfiguration();
        configuration.setDirectPasswordPasswordEncryptionFile(new File("local/cpr/keyfile.json"));
        return configuration;
    }

    private boolean login() throws IOException {
        CprConfiguration configuration = this.getConfiguration();

        String transactionCode = configuration.getDirectTransactionCode();
        String customerNumber = String.format("%4d", configuration.getDirectCustomerNumber());
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
                            configuration.getDirectCustomerNumber() +
                            dataType +
                            this.authToken +
                            configuration.getDirectUsername() +
                            "00" +
                            pnr
            ); // must be 39 bytes
            System.out.println("Sending request:\n" + request);

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

        System.out.println("Reading response from CPR:");

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
        System.out.println("Error number: " + errorCode);

        if (errorCode != 0) {
            // extended reason code available after standard error code
            System.out.println("Reason code: " + Integer.parseInt(
                    response.substring(startErrorResponse + 2, startErrorResponse + 6)
            ));

            String errorText;
            if (!(errorText = response.substring(startDataSection, response.length()).trim()).equals("")) {
                System.out.println("Error message: " + errorText);
                // Log text
            }
        }

        return errorCode;
    }

}

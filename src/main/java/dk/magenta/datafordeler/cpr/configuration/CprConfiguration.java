package dk.magenta.datafordeler.cpr.configuration;

import dk.magenta.datafordeler.core.configuration.Configuration;
import dk.magenta.datafordeler.core.exception.ConfigurationException;
import dk.magenta.datafordeler.cpr.CprPlugin;
import dk.magenta.datafordeler.cpr.data.CprEntityManager;
import dk.magenta.datafordeler.cpr.data.person.PersonEntityManager;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceEntityManager;
import dk.magenta.datafordeler.cpr.data.road.RoadEntityManager;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by lars on 16-05-17.
 */
@javax.persistence.Entity
@Table(name="cpr_config")
public class CprConfiguration implements Configuration {

    public enum Charset {
        US_ASCII,
        ISO_8859_1,
        UTF_8,
        UTF_16BE,
        UTF_16LE,
        UTF_16
    }

    public enum RegisterType {
        LOCAL_FILE,
        REMOTE_FTP
    }

    @Id
    @Column(name = "id")
    private final String plugin = CprPlugin.class.getName();




    // Midnight every january 1st
    @Column
    private String personRegisterPullCronSchedule = "0 0 0 1 1 ?";

    @Column
    private RegisterType personRegisterType = RegisterType.LOCAL_FILE;

    @Column
    private String personRegisterFtpAddress = "ftps://localhost:2101/ud/";

    @Column
    private String personRegisterFtpUsername = "username";

    @Column
    private String personRegisterFtpPassword = "password";

    @Column
    private String personRegisterLocalFile = "data/cprpersondata.txt";

    @Column
    private Charset personRegisterDataCharset = Charset.ISO_8859_1;



    @Column
    private String roadRegisterPullCronSchedule = null;

    @Column
    private RegisterType roadRegisterType = RegisterType.LOCAL_FILE;

    @Column
    private String roadRegisterFtpAddress = null;

    @Column
    private String roadRegisterFtpUsername = null;

    @Column
    private String roadRegisterFtpPassword = null;

    @Column
    private String roadRegisterLocalFile = "data/cprroaddata_truncated.txt";

    @Column
    private Charset roadRegisterDataCharset = Charset.ISO_8859_1;



    @Column
    private String residenceRegisterPullCronSchedule = null;

    @Column
    private RegisterType residenceRegisterType = RegisterType.LOCAL_FILE;

    @Column
    private String residenceRegisterFtpAddress = null;

    @Column
    private String residenceRegisterFtpUsername = null;

    @Column
    private String residenceRegisterFtpPassword = null;

    @Column
    private String residenceRegisterLocalFile = "data/cprroaddata_truncated.txt";

    @Column
    private Charset residenceRegisterDataCharset = Charset.ISO_8859_1;






    public String getPersonRegisterPullCronSchedule() {
        return this.personRegisterPullCronSchedule;
    }

    public RegisterType getPersonRegisterType() {
        return this.personRegisterType;
    }

    public String getPersonRegisterFtpAddress() {
        return this.personRegisterFtpAddress;
    }

    public String getPersonRegisterFtpUsername() {
        return this.personRegisterFtpUsername;
    }

    public String getPersonRegisterFtpPassword() {
        return this.personRegisterFtpPassword;
    }

    public String getPersonRegisterLocalFile() {
        return this.personRegisterLocalFile;
    }

    public String getPersonRegisterCharset() {
        return this.formatCharset(this.personRegisterDataCharset);
    }

    public URI getPersonRegisterURI() throws ConfigurationException {
        return this.formatURI(this.personRegisterType, this.personRegisterLocalFile, this.personRegisterFtpAddress);
    }








    public String getRoadRegisterPullCronSchedule() {
        return this.roadRegisterPullCronSchedule;
    }

    public RegisterType getRoadRegisterType() {
        return this.roadRegisterType;
    }

    public String getRoadRegisterFtpAddress() {
        return this.roadRegisterFtpAddress;
    }

    public String getRoadRegisterFtpUsername() {
        return this.roadRegisterFtpUsername;
    }

    public String getRoadRegisterFtpPassword() {
        return this.roadRegisterFtpPassword;
    }

    public String getRoadRegisterLocalFile() {
        return this.roadRegisterLocalFile;
    }

    public String getRoadRegisterCharset() {
        return this.formatCharset(this.roadRegisterDataCharset);
    }

    public URI getRoadRegisterURI() throws ConfigurationException {
        return this.formatURI(this.roadRegisterType, this.roadRegisterLocalFile, this.roadRegisterFtpAddress);
    }





    public String getResidenceRegisterPullCronSchedule() {
        return this.residenceRegisterPullCronSchedule;
    }

    public RegisterType getResidenceRegisterType() {
        return this.residenceRegisterType;
    }

    public String getResidenceRegisterFtpAddress() {
        return this.residenceRegisterFtpAddress;
    }

    public String getResidenceRegisterFtpUsername() {
        return this.residenceRegisterFtpUsername;
    }

    public String getResidenceRegisterFtpPassword() {
        return this.residenceRegisterFtpPassword;
    }

    public String getResidenceRegisterLocalFile() {
        return this.residenceRegisterLocalFile;
    }

    public String getResidenceRegisterCharset() {
        return this.formatCharset(this.residenceRegisterDataCharset);
    }

    public URI getResidenceRegisterURI() throws ConfigurationException {
        return this.formatURI(this.residenceRegisterType, this.residenceRegisterLocalFile, this.residenceRegisterFtpAddress);
    }





    public String getRegisterPullCronSchedule(CprEntityManager entityManager) {
        if (entityManager instanceof PersonEntityManager) {
            return this.getPersonRegisterPullCronSchedule();
        }
        if (entityManager instanceof RoadEntityManager) {
            return this.getRoadRegisterPullCronSchedule();
        }
        if (entityManager instanceof ResidenceEntityManager) {
            return this.getResidenceRegisterPullCronSchedule();
        }
        return null;
    }

    public RegisterType getRegisterType(CprEntityManager entityManager) {
        if (entityManager instanceof PersonEntityManager) {
            return this.getPersonRegisterType();
        }
        if (entityManager instanceof RoadEntityManager) {
            return this.getRoadRegisterType();
        }
        if (entityManager instanceof ResidenceEntityManager) {
            return this.getResidenceRegisterType();
        }
        return null;
    }

    public String getRegisterFtpAddress(CprEntityManager entityManager) {
        if (entityManager instanceof PersonEntityManager) {
            return this.getPersonRegisterFtpAddress();
        }
        if (entityManager instanceof RoadEntityManager) {
            return this.getRoadRegisterFtpAddress();
        }
        if (entityManager instanceof ResidenceEntityManager) {
            return this.getResidenceRegisterFtpAddress();
        }
        return null;
    }

    public String getRegisterFtpUsername(CprEntityManager entityManager) {
        if (entityManager instanceof PersonEntityManager) {
            return this.getPersonRegisterFtpUsername();
        }
        if (entityManager instanceof RoadEntityManager) {
            return this.getRoadRegisterFtpUsername();
        }
        if (entityManager instanceof ResidenceEntityManager) {
            return this.getResidenceRegisterFtpUsername();
        }
        return null;
    }

    public String getRegisterFtpPassword(CprEntityManager entityManager) {
        if (entityManager instanceof PersonEntityManager) {
            return this.getPersonRegisterFtpPassword();
        }
        if (entityManager instanceof RoadEntityManager) {
            return this.getRoadRegisterFtpPassword();
        }
        if (entityManager instanceof ResidenceEntityManager) {
            return this.getResidenceRegisterFtpPassword();
        }
        return null;
    }

    public String getRegisterLocalFile(CprEntityManager entityManager) {
        if (entityManager instanceof PersonEntityManager) {
            return this.getPersonRegisterLocalFile();
        }
        if (entityManager instanceof RoadEntityManager) {
            return this.getRoadRegisterLocalFile();
        }
        if (entityManager instanceof ResidenceEntityManager) {
            return this.getResidenceRegisterLocalFile();
        }
        return null;
    }

    public String getRegisterCharset(CprEntityManager entityManager) {
        if (entityManager instanceof PersonEntityManager) {
            return this.getPersonRegisterCharset();
        }
        if (entityManager instanceof RoadEntityManager) {
            return this.getRoadRegisterCharset();
        }
        if (entityManager instanceof ResidenceEntityManager) {
            return this.getResidenceRegisterCharset();
        }
        return null;
    }

    public URI getRegisterURI(CprEntityManager entityManager) throws ConfigurationException {
        if (entityManager instanceof PersonEntityManager) {
            return this.getPersonRegisterURI();
        }
        if (entityManager instanceof RoadEntityManager) {
            return this.getRoadRegisterURI();
        }
        if (entityManager instanceof ResidenceEntityManager) {
            return this.getResidenceRegisterURI();
        }
        return null;
    }



    private String formatCharset(Charset charset) {
        if (charset != null) {
            return charset.name().replaceAll("_", "-");
        }
        return null;
    }

    private URI formatURI(RegisterType registerType, String localFile, String ftpAddress) throws ConfigurationException {
        if (registerType == RegisterType.LOCAL_FILE) {
            File file = new File(localFile);
            if (!file.exists()) {
                throw new ConfigurationException("Configured file not found: "+localFile);
            }
            return file.toURI();
        } else if (registerType == RegisterType.REMOTE_FTP) {
            try {
                return new URI(ftpAddress);
            } catch (URISyntaxException e) {
                throw new ConfigurationException("Invalid FTP address configured: "+ftpAddress);
            }
        }
        return null;
    }

    
    /**
     * For testing - test methods will set this, but not save the entity to DB
     * @param registerAddress
     */
    public void setPersonRegisterAddress(String registerAddress) {
        this.personRegisterFtpAddress = registerAddress;
    }
}

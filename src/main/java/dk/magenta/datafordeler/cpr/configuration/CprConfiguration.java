package dk.magenta.datafordeler.cpr.configuration;

import dk.magenta.datafordeler.core.configuration.Configuration;
import dk.magenta.datafordeler.cpr.CprPlugin;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by lars on 16-05-17.
 */
@javax.persistence.Entity
@Table(name="cpr_config")
public class CprConfiguration implements Configuration {

    @Id
    @Column(name = "id")
    private final String plugin = CprPlugin.class.getName();

    @Column
    private String pullCronSchedule = "0 0 * * * ?";

    @Column
    private String registerAddress = "ftp://localhost:2101";

    @Column
    private String ftpUsername = "username";

    @Column
    private String ftpPassword = "password";

    @Column
    private boolean ftps = true;


    // Temporary: set this to the location of the cpr file on the server
    @Column
    private String cprRoadDataLocation = "";

    // Temporary: set this to the location of the cpr file on the server
    @Column
    private String cprResidenceDataLocation = "";

    public String getPullCronSchedule() {
        return this.pullCronSchedule;
    }

    public String getRegisterAddress() {
        return this.registerAddress;
    }

    public String getFtpUsername() {
        return this.ftpUsername;
    }

    public String getFtpPassword() {
        return this.ftpPassword;
    }

    public boolean getFtps() {
        return this.ftps;
    }

    public String getCprRoadDataLocation() {
        return this.cprRoadDataLocation;
    }

    public String getCprResidenceDataLocation() {
        return this.cprResidenceDataLocation;
    }

    /**
     * For testing - test methods will set this, but not save the entity to DB
     * @param registerAddress
     */
    public void setRegisterAddress(String registerAddress) {
        System.out.println("setting registerAddress: " + registerAddress);
        this.registerAddress = registerAddress;
    }
}

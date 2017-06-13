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
    private String registerAddress = "http://localhost:8000";

    public String getPullCronSchedule() {
        return this.pullCronSchedule;
    }

    public String getRegisterAddress() {
        return this.registerAddress;
    }
}

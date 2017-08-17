package dk.magenta.datafordeler.cpr;

import dk.magenta.datafordeler.core.configuration.ConfigurationManager;
import dk.magenta.datafordeler.core.plugin.Plugin;
import dk.magenta.datafordeler.core.plugin.RegisterManager;
import dk.magenta.datafordeler.core.plugin.RolesDefinition;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import dk.magenta.datafordeler.cpr.data.person.PersonEntityManager;
import dk.magenta.datafordeler.cpr.data.residence.ResidenceEntityManager;
import dk.magenta.datafordeler.cpr.data.road.RoadEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by lars on 16-05-17.
 */
@Component
public class CprPlugin extends Plugin {

    @Autowired
    private CprConfigurationManager configurationManager;

    @Autowired
    private CprRegisterManager registerManager;

    @Autowired
    private PersonEntityManager personEntityManager;

    @Autowired
    private ResidenceEntityManager residenceEntityManager;

    @Autowired
    private RoadEntityManager roadEntityManager;

    private CprRolesDefinition rolesDefinition = new CprRolesDefinition();

    @PostConstruct
    public void init() {
        this.registerManager.addEntityManager(this.personEntityManager);
        this.registerManager.addEntityManager(this.residenceEntityManager);
        this.registerManager.addEntityManager(this.roadEntityManager);
    }

    @Override
    public long getVersion() {
        return 1;
    }

    @Override
    public String getName() {
        return "cpr";
    }

    public static String getDomain() {
        return "cpr";
    }

    @Override
    public RegisterManager getRegisterManager() {
        return this.registerManager;
    }

    @Override
    public ConfigurationManager getConfigurationManager() {
        return this.configurationManager;
    }

    @Override
    public RolesDefinition getRolesDefinition() {
        return this.rolesDefinition;
    }
}

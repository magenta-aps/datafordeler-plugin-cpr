package dk.magenta.datafordeler.cpr;

import dk.magenta.datafordeler.core.configuration.ConfigurationManager;
import dk.magenta.datafordeler.core.plugin.Plugin;
import dk.magenta.datafordeler.core.plugin.RegisterManager;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import dk.magenta.datafordeler.cpr.data.person.PersonEntityManager;
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

    @PostConstruct
    public void init() {
        this.registerManager.addEntityManager(this.personEntityManager);
    }

    @Override
    public String getName() {
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

}

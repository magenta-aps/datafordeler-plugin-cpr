package dk.magenta.datafordeler.cpr.configuration;

import dk.magenta.datafordeler.core.configuration.ConfigurationManager;
import dk.magenta.datafordeler.core.database.ConfigurationSessionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;

@Component
public class CprConfigurationManager extends ConfigurationManager<CprConfiguration> {

    @Autowired
    private ConfigurationSessionManager configurationSessionManager;

    @Value("${cpr.encryption.keyfile:local/cpr/keyfile.json}")
    private String encryptionKeyFileName;

    @Value("${cpr.encryption.keyfile:local/cpr/encryptedpassword.bin}")
    private String encryptedPassword;

    private Logger log = LogManager.getLogger("CprConfigurationManager");

    @PostConstruct
    public void init() {
        // Very important to call init() on ConfigurationManager, or the config will not be loaded
        super.init();
    }

    @Override
    protected Class<CprConfiguration> getConfigurationClass() {
        return CprConfiguration.class;
    }

    @Override
    protected CprConfiguration createConfiguration() {
        return new CprConfiguration();
    }

    @Override
    protected ConfigurationSessionManager getSessionManager() {
        return this.configurationSessionManager;
    }

    @Override
    protected Logger getLog() {
        return this.log;
    }


    @Override
    public CprConfiguration getConfiguration() {
        CprConfiguration configuration = super.getConfiguration();
        File encryptionFile = new File(this.encryptionKeyFileName);
        configuration.setPersonRegisterPasswordEncryptionFile(encryptionFile);
        configuration.setRoadRegisterPasswordEncryptionFile(encryptionFile);
        configuration.setResidenceRegisterPasswordEncryptionFile(encryptionFile);
        configuration.setDirectPasswordPasswordEncryptionFile(encryptionFile);
        return configuration;
    }

    public void setDirectPassword(String password) throws GeneralSecurityException, IOException {
        // Updates the encrypted password in the database
        Session session = this.getSessionManager().getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            CprConfiguration cprConfiguration = session.createQuery("select c from " + CprConfiguration.class.getCanonicalName() + " c", CprConfiguration.class).getSingleResult();
            cprConfiguration.setDirectPasswordPasswordEncryptionFile(new File(this.encryptionKeyFileName));
            cprConfiguration.setDirectPassword(password);
            CprConfiguration configuration = super.getConfiguration();
            transaction.commit();
            Files.write(new File(encryptedPassword).toPath(), configuration.getEncryptedDirectPassword());
            session.saveOrUpdate(cprConfiguration);
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @PostConstruct
    public void printDirectPassword() throws GeneralSecurityException, IOException {
        try {
            CprConfiguration configuration = super.getConfiguration();
            File encryptedPasswordFile = new File(encryptedPassword);
            if(encryptedPasswordFile.getParentFile().isDirectory()) {
                Files.write(new File(encryptedPassword).toPath(), configuration.getEncryptedDirectPassword());
            }
        } catch(Exception ioe) {
            log.error("Exception", ioe);
        }
    }
}

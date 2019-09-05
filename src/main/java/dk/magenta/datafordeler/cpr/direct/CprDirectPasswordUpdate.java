package dk.magenta.datafordeler.cpr.direct;

import dk.magenta.datafordeler.core.AbstractTask;
import dk.magenta.datafordeler.core.Engine;
import dk.magenta.datafordeler.core.Pull;
import dk.magenta.datafordeler.core.command.Worker;
import dk.magenta.datafordeler.core.plugin.RegisterManager;
import dk.magenta.datafordeler.cpr.configuration.CprConfigurationManager;
import org.quartz.JobDataMap;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

public class CprDirectPasswordUpdate extends Worker implements Runnable {


    public static class Task extends AbstractTask<CprDirectPasswordUpdate> {
        public static final String DATA_CONFIGURATIONMANAGER = "configurationManager";

        @Override
        protected CprDirectPasswordUpdate createWorker(JobDataMap dataMap) {
            CprConfigurationManager configurationManager = (CprConfigurationManager) dataMap.get(DATA_CONFIGURATIONMANAGER);
            return new CprDirectPasswordUpdate(configurationManager);
        }
    }


    private CprConfigurationManager configurationManager;
    private SecureRandom random = new SecureRandom();

    public CprDirectPasswordUpdate(CprConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    private static final String ALPHA_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHA_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMERIC = "0123456789";
    private static final String SPECIAL_CHARS = "~`!@#$%^*()_-+=,./{}[];:";

    public String generatePassword(int len) {
        String[] buckets = new String[]{ALPHA_LOWER, ALPHA_UPPER, NUMERIC, SPECIAL_CHARS};
        char[] password = new char[len];
        for (int i = 0; i < len; i++) {
            // Round-robin the buckets, and pick a random char from the current one
            String bucket = buckets[i % buckets.length];
            int index = random.nextInt(bucket.length());
            password[i] = bucket.charAt(index);
        }
        for (int i = 0; i < password.length; i++) {
            // Randomly shuffle two chars in the array
            int randomIndex = random.nextInt(password.length);
            char temp = password[i];
            password[i] = password[randomIndex];
            password[randomIndex] = temp;
        }
        return new String(password);
    }

    @Override
    public void run() {
        try {
            String oldPassword = this.configurationManager.getConfiguration().getDirectPassword();
            String newPassword = this.generatePassword(8);
            // Update remote pw

            // If success, update local pw
            //this.configurationManager.setDirectPassword(newPassword);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}

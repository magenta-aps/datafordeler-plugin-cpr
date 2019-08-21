package dk.magenta.datafordeler.cpr.synchronization;

import dk.magenta.datafordeler.cpr.data.person.PersonEntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.TimerTask;

public class SubscribtionTimerTask extends TimerTask {
    private Logger log = LogManager.getLogger(SubscribtionTimerTask.class);
    private PersonEntityManager personManager;

    public SubscribtionTimerTask(PersonEntityManager personManager) {
        this.personManager = personManager;
    }

    @Override
    public void run() {
        try {
            this.personManager.createSubscriptionFile();
        } catch (Exception e) {
            log.error("Failed to upload subscribtions", e);
        }
    }
}
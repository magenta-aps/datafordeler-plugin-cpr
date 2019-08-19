package dk.magenta.datafordeler.cpr.synchronization;

import dk.magenta.datafordeler.cpr.data.person.PersonEntityManager;

import java.util.TimerTask;


public class SubscribtionTimerTask extends TimerTask {

    @Override
    public void run() {
        PersonEntityManager entitiManager = new PersonEntityManager();
        entitiManager.createSubscriptionFile();
    }

}
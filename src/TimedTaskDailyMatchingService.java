import java.rmi.RemoteException;
import java.util.TimerTask;

public class TimedTaskDailyMatchingService extends TimerTask {

    MatchingService ms;

    public TimedTaskDailyMatchingService(MatchingServiceImpl m) {
        ms = m;
    }

    public void run() {
        try {
            ms.forwardUnacknowledgedTokens();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}




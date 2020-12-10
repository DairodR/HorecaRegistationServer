import java.rmi.RemoteException;
import java.util.TimerTask;

public class TimedTaskDailyMixingProxy extends TimerTask {

    MixingProxyImpl mp;

    public TimedTaskDailyMixingProxy(MixingProxyImpl m) {
        mp = m;
    }

    public void run() {
        try {
           mp.clearData();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}


import java.rmi.RemoteException;
import java.util.TimerTask;

public class TimedTaskDaily extends TimerTask {

    MixingProxyImpl mp;

    public TimedTaskDaily(MixingProxyImpl m) {
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


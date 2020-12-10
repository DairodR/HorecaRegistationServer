import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;

public class MixingProxyServer {
    private void startServer() {
        try {
            // create on port 1098
            System.setProperty("java.rmi.server.hostname","localhost");
            Registry registry = LocateRegistry.createRegistry(1098);
            // create a new service named MixingProxy
            MixingProxyImpl mp =  new MixingProxyImpl();
            registry.rebind("MixingProxy", mp);

            LocalTime midnight = LocalTime.MIDNIGHT;
            LocalDate today = LocalDate.now();
            LocalDateTime localDateTime = LocalDateTime.of(today,midnight);
            Timer timer = new Timer();
            timer.schedule(new TimedTaskDailyMixingProxy(mp), Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()), 86400000);

            mp.connectToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("system is ready");
    }

    public static void main(String[] args) {
        MixingProxyServer server  = new MixingProxyServer();
        server.startServer();

    }
}
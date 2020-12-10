import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;

public class MatchingServiceServer {
    private void startServer() {
        try {
            // create on port 1097
            System.setProperty("java.rmi.server.hostname","localhost");
            Registry registry = LocateRegistry.createRegistry(1097);
            // create a new service named MatchingService
            MatchingServiceImpl ms = new MatchingServiceImpl();
            registry.rebind("MatchingService", ms);

            LocalTime midnight = LocalTime.MIDNIGHT;
            LocalDate today = LocalDate.now();
            LocalDateTime localDateTime = LocalDateTime.of(today,midnight);
            Timer timer = new Timer();
            timer.schedule(new TimedTaskDailyMatchingService(ms), Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()), 86400000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("system is ready");
    }

    public static void main(String[] args) {
        MatchingServiceServer server  = new MatchingServiceServer();
        server.startServer();
    }
}
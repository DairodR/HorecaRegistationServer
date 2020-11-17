import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MatchingServiceServer {
    private void startServer() {
        try {
            // create on port 1099
            System.setProperty("java.rmi.server.hostname","192.168.1.7");
            Registry registry = LocateRegistry.createRegistry(1099);
            // create a new service named MatchingService
            registry.rebind("MatchingService", new MatchingServiceImpl());
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
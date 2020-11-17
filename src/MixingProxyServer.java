import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MixingProxyServer {
    private void startServer() {
        try {
            // create on port 1099
            System.setProperty("java.rmi.server.hostname","192.168.1.7");
            Registry registry = LocateRegistry.createRegistry(1099);
            // create a new service named MixingProxy
            registry.rebind("MixingProxy", new MixingProxyImpl());
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
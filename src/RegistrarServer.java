import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RegistrarServer {
    private void startServer() {
        try {
            // create on port 1099
            System.setProperty("java.rmi.server.hostname","localhost");
            Registry registry = LocateRegistry.createRegistry(1099);
            // create a new service named Registrar
            registry.rebind("Registrar", new RegistrarImpl());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("system is ready");
    }

    public static void main(String[] args) {
        RegistrarServer server  = new RegistrarServer();
        server.startServer();
    }
}

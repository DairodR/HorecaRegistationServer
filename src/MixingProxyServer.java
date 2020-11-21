import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MixingProxyServer {
    private void startServer() {
        try {
            // create on port 1099
            System.setProperty("java.rmi.server.hostname","localhost");
            Registry registry = LocateRegistry.createRegistry(1098);
            // create a new service named MixingProxy
            MixingProxyImpl mp =  new MixingProxyImpl();
            registry.rebind("MixingProxy", mp);
            /*DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormatter .parse("2020-11-20 00:00:01");
            Timer timer = new Timer();
            timer.schedule(new TimedTaskDaily(mp),date, 86400000 );*/
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
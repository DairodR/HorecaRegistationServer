import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CateringFacilityInterface extends Remote {

    void connectToServer() throws RemoteException;

}

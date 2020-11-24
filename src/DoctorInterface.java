import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;

public interface DoctorInterface extends Remote {

    void connectToServer() throws RemoteException;

    PublicKey getPublicKey() throws RemoteException;

    int getId() throws RemoteException;
}

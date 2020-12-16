import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.List;

public interface MixingProxy extends Remote {
    void connectToServer() throws RemoteException;

    byte[] registerVisit(String capsule) throws RemoteException;

    void continueVisit(String capsule) throws RemoteException;

    void acknowledge(List<String> token) throws RemoteException;

    void submitCapsules() throws RemoteException;

    PublicKey getPublicKey() throws RemoteException;

    void clearData()throws RemoteException;

    void flushCapsules() throws RemoteException;
}

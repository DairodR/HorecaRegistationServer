import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;

public interface MixingProxy extends Remote {
    void connectToServer() throws RemoteException;

    byte[] registerVisit(String capsule) throws RemoteException;

    void continueVisit(String capsule) throws RemoteException;

    void acknowledge() throws RemoteException;

    void submitCapsules() throws RemoteException;

    void submitAcknowledgements() throws RemoteException;

    PublicKey getPublicKey() throws RemoteException;

    void clearData()throws RemoteException;
}

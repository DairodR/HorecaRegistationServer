import java.rmi.RemoteException;

public interface MixingProxy {
    void registerVisit() throws RemoteException;

    void acknowledge() throws RemoteException;

    void submitCapsules() throws RemoteException;

    void submitAcknowledgements() throws RemoteException;
}

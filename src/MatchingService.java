import java.rmi.RemoteException;

public interface MatchingService {
    void requestInfectedLogs() throws RemoteException;

    void forwardLogs() throws RemoteException;

    void submitCapsules() throws RemoteException;

    void submitAcknowledgements() throws RemoteException;

    void forwardUnacknowledgedLogs() throws RemoteException;
}

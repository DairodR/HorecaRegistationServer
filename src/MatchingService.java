import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface MatchingService extends Remote {
    void connect(MixingProxy mp) throws RemoteException;

    void requestInfectedLogs() throws RemoteException;

    void forwardLogs() throws RemoteException;

    void submitCapsules(List<String> capsules) throws RemoteException;

    void submitAcknowledgements() throws RemoteException;

    void forwardUnacknowledgedLogs() throws RemoteException;
}

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface MatchingService extends Remote {
    void connectToServer() throws RemoteException;

    void connect(MixingProxy mp) throws RemoteException;

    void connect(DoctorInterface d) throws RemoteException;

    void requestInfectedLogs() throws RemoteException;

    void forwardLogs(int id, List<String> unsignedLogs, List<byte[]> signedLogs) throws RemoteException;

    void submitCapsules(List<String> capsules) throws RemoteException;

    void submitAcknowledgements() throws RemoteException;

    void forwardUnacknowledgedLogs() throws RemoteException;
}

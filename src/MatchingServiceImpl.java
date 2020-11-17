import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class MatchingServiceImpl extends UnicastRemoteObject implements MatchingService{
    protected MatchingServiceImpl() throws RemoteException {
    }

    protected MatchingServiceImpl(int port) throws RemoteException {
        super(port);
    }

    protected MatchingServiceImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public void requestInfectedLogs() throws RemoteException {

    }

    @Override
    public void forwardLogs() throws RemoteException {

    }

    @Override
    public void submitCapsules() throws RemoteException {

    }

    @Override
    public void submitAcknowledgements() throws RemoteException {

    }

    @Override
    public void forwardUnacknowledgedLogs() throws RemoteException {

    }
}

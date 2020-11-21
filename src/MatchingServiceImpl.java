import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MatchingServiceImpl extends UnicastRemoteObject implements MatchingService{
    PublicKey mixingServiceKey=null;
    Set<String> capsules;

    protected MatchingServiceImpl() throws RemoteException {
        capsules = new HashSet<>();
    }

    protected MatchingServiceImpl(int port) throws RemoteException {
        super(port);
    }

    protected MatchingServiceImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public void connect(MixingProxy mp) throws RemoteException {
       mixingServiceKey = mp.getPublicKey();
    }

    @Override
    public void requestInfectedLogs() throws RemoteException {

    }

    @Override
    public void forwardLogs() throws RemoteException {

    }

    @Override
    public void submitCapsules(List<String> c) throws RemoteException {
        capsules.addAll(c);
    }

    @Override
    public void submitAcknowledgements() throws RemoteException {

    }

    @Override
    public void forwardUnacknowledgedLogs() throws RemoteException {

    }
}

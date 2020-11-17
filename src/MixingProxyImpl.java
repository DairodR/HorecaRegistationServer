import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class MixingProxyImpl extends UnicastRemoteObject implements MixingProxy{
    protected MixingProxyImpl() throws RemoteException {
    }

    protected MixingProxyImpl(int port) throws RemoteException {
        super(port);
    }

    protected MixingProxyImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }


    @Override
    public void registerVisit() throws RemoteException {

    }

    @Override
    public void acknowledge() throws RemoteException {

    }

    @Override
    public void submitCapsules() throws RemoteException {

    }

    @Override
    public void submitAcknowledgements() throws RemoteException {

    }
}

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class RegistrarImpl extends UnicastRemoteObject implements Registrar {

    protected RegistrarImpl() throws RemoteException {
    }

    protected RegistrarImpl(int port) throws RemoteException {
        super(port);
    }

    protected RegistrarImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public void enrollFacility() throws RemoteException {

    }

    @Override
    public void enrollUsers() throws RemoteException {

    }

    @Override
    public void retrieveToken() throws RemoteException {

    }
}

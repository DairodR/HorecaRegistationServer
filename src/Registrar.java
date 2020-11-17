import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Registrar extends Remote{
    void connect(CateringFacilityInterface cf) throws RemoteException;

    void connect(UserInterface ui) throws RemoteException;

    void enrollFacility() throws RemoteException;

    void enrollUsers() throws RemoteException;

    void retrieveToken() throws RemoteException;


}

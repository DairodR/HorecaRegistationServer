import java.rmi.Remote;
import java.rmi.RemoteException;


public interface UserInterface extends  Remote{

    void connectToServer() throws RemoteException;

}

import javax.crypto.SecretKey;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.List;

public interface Registrar extends Remote{
    void connect(CateringFacilityInterface cf) throws RemoteException;

    void connect(UserInterface ui) throws RemoteException;

    String enrollFacility(String cf) throws RemoteException;

    SecretKey getDailyKey(String cf, String s) throws RemoteException;

    String getDailyPseudonym(String location, SecretKey sCFDay) throws RemoteException;

    PublicKey enrollUsers(int gsm) throws RemoteException;

    List<byte[]> retrieveToken() throws RemoteException;
}

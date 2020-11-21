import javax.crypto.SecretKey;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.List;

public interface Registrar extends Remote{
    void connect(CateringFacilityInterface cf) throws RemoteException;

    void connect(UserInterface ui) throws RemoteException;

    PublicKey connect(MixingProxy mp) throws RemoteException;

    SecretKey enrollFacility(String cf) throws RemoteException;

    SecretKey getDailyKey(String cf, SecretKey s) throws RemoteException;

    String getDailyPseudonym(String location, SecretKey sCFDay) throws RemoteException;

    PublicKey enrollUsers(int gsm) throws RemoteException;

    List<String> retrieveToken() throws RemoteException;
}

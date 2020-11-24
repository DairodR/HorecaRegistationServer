import javax.crypto.SecretKey;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface Registrar extends Remote{
    void connect(CateringFacilityInterface cf) throws RemoteException;

    void connect(UserInterface ui) throws RemoteException;

    PublicKey connect(MixingProxy mp) throws RemoteException;

    PublicKey connect(MatchingService ms) throws RemoteException;

    SecretKey enrollFacility(String cf) throws RemoteException;

    PublicKey enrollUsers(int gsm) throws RemoteException;

    List<String> retrieveToken() throws RemoteException;

    SecretKey getDailyKey(String cf, SecretKey s) throws RemoteException;

    String getDailyPseudonym(String location, SecretKey sCFDayi) throws RemoteException;

    void setDailyNym(int rand, String dailyNym) throws RemoteException;

    Map<Integer,String> getAllNyms(String date) throws RemoteException;
}

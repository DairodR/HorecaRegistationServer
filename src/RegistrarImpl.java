import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.security.*;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class RegistrarImpl extends UnicastRemoteObject implements Registrar {
    List<CateringFacilityInterface> cateringFacilities;


    protected RegistrarImpl() throws RemoteException {
        cateringFacilities = new ArrayList<>();
    }

    protected RegistrarImpl(int port) throws RemoteException {
        super(port);
    }

    protected RegistrarImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public void connect(CateringFacilityInterface cf) throws RemoteException {
        cateringFacilities.add(cf);
    }

    @Override
    public void connect(UserInterface ui) throws RemoteException {

    }

    @Override
    public SecretKey enrollFacility(String cf) throws RemoteException {
        byte[] decodedKey = Base64.getDecoder().decode(cf);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return secretKey;
    }

    @Override
    public SecretKey getDailyKey(String cf, SecretKey s) throws RemoteException {
        long timeMilli= LocalDate.now().toEpochDay();
        byte[] salt = new byte[8];
        for (int i = 7; i >= 0; i--) {
            salt[i] = (byte)(timeMilli & 0xFF);
            timeMilli >>= 8;
        }

        SecretKeyFactory kf = null;
        MessageDigest md = null;
        try {
            kf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        KeySpec specs = new PBEKeySpec(cf.toCharArray(), salt, 1024);
        SecretKey key = null;
        try {
            key = kf.generateSecret(specs);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        md.update(key.getEncoded());
        byte[] digest = md.digest(s.getEncoded());
        SecretKey mergedSecretKey = new SecretKeySpec(digest, "AES");
        return mergedSecretKey;
    }

    @Override
    public String getDailyPseudonym(String location, SecretKey sCFDay) throws RemoteException{
        long timeMilli= LocalDate.now().toEpochDay();
        byte[] day = new byte[8];
        for (int i = 7; i >= 0; i--) {
            day[i] = (byte)(timeMilli & 0xFF);
            timeMilli >>= 8;
        }

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(location.getBytes());
        md.update(sCFDay.getEncoded());
        byte[] digest = md.digest(day);

        return Base64.getEncoder().encodeToString(digest);
    }

    @Override
    public void enrollUsers() throws RemoteException {

    }

    @Override
    public void retrieveToken() throws RemoteException {

    }
}

import java.io.UnsupportedEncodingException;
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
    List<Integer> users;

    KeyPair pair;


    protected RegistrarImpl() throws RemoteException {
        cateringFacilities = new ArrayList<>();
        KeyPairGenerator keyGen=null;
        SecureRandom random=null;
        try {
            keyGen = KeyPairGenerator.getInstance("DSA","SUN");
            random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(1024, random);
            pair = keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
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
        System.out.println("Enrolling facility");
        System.out.println("TEST?");
        byte[] decodedKey = Base64.getDecoder().decode(cf);
        System.out.println("PIJPEN");
        Cipher cipher = null;
        String encoded = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            encoded = Base64.getEncoder().encodeToString(cipher.doFinal(cf.getBytes("UTF-8")));
            System.out.println("KOEKE");
            // System.out.println( Base64.getMimeEncoder().encodeToString( secretKey.getEncoded()));
            //            System.out.println(Base64.getMimeEncoder().encodeToString( decodedKey));

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | UnsupportedEncodingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }


        return encoded;
    }

    @Override
    public SecretKey getDailyKey(String cf, SecretKey s) throws RemoteException {
        System.out.println("Getting daily key");
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

        KeySpec specs = new PBEKeySpec(cf.toCharArray(), salt, 1024,128);
        SecretKey key = null;
        try {
            key = kf.generateSecret(specs);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        md.update(key.getEncoded());
        byte[] digest = md.digest(s.getEncoded());
        SecretKey mergedSecretKey = new SecretKeySpec(digest, "AES");
        System.out.println(mergedSecretKey.toString());
        return mergedSecretKey;
    }

    @Override
    public String getDailyPseudonym(String location, SecretKey sCFDay) throws RemoteException{
        System.out.println("Getting daily pseudonym");
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
        System.out.println(Base64.getEncoder().encodeToString(digest));
        return Base64.getEncoder().encodeToString(digest);
    }

    @Override
    public PublicKey enrollUsers(int gsm) throws RemoteException {
        System.out.println("Enrolling user:"+gsm);
        users.add(gsm);
        return pair.getPublic();
    }

    @Override
    public List<byte[]> retrieveToken() throws RemoteException {
        System.out.println("Retrieving tokens");
        List<byte[]> tokens=new ArrayList<>();
        for(int j=0;j<48;j++) {
            SecureRandom random = null;
            Signature dsa = null;
            long timeMilli = LocalDate.now().toEpochDay();
            byte[] day = new byte[8];
            for (int i = 7; i >= 0; i--) {
                day[i] = (byte) (timeMilli & 0xFF);
                timeMilli >>= 8;
            }
            try {
                random = SecureRandom.getInstance("SHA1PRNG", "SUN");
                dsa = Signature.getInstance("SHA1withDSA", "SUN");

                dsa.initSign(pair.getPrivate());

                byte[] bytes = new byte[20];
                random.nextBytes(bytes);
                dsa.update(bytes);

                dsa.update(day);

                byte[] realSig = dsa.sign();
                tokens.add(realSig);
            } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
                e.printStackTrace();
            }
        }
        return tokens;
    }
}

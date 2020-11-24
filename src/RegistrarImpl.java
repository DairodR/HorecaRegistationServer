import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class RegistrarImpl extends UnicastRemoteObject implements Registrar {
    List<CateringFacilityInterface> cateringFacilities;
    List<Integer> users;

    KeyPair pair;

    Map<String, Integer> allDailyPseudonyms = new HashMap<>();


    protected RegistrarImpl() throws RemoteException {
        cateringFacilities = new ArrayList<>();
        users = new ArrayList<>();
        KeyPairGenerator keyGen=null;
        SecureRandom random=null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            pair = keyGen.generateKeyPair();
            System.out.println("pair:"+pair);
            System.out.println("publickey:"+Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()));
            System.out.println("privatekey:"+Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded()));

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
    public PublicKey connect(MixingProxy mp) throws RemoteException {
        return pair.getPublic();
    }

    @Override
    public PublicKey connect(MatchingService ms) throws RemoteException {
        return pair.getPublic();
    }

    @Override
    public SecretKey enrollFacility(String cf) throws RemoteException {
        System.out.println("Enrolling facility");
        SecretKey secretKey= null;
        try {
            SecureRandom r = new SecureRandom();
            byte[] salt = new byte[8];
            r.nextBytes(salt);
            char[] password = cf.toCharArray();
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
             secretKey = factory.generateSecret(spec);
            System.out.println( Base64.getMimeEncoder().encodeToString( secretKey.getEncoded()));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return secretKey;
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
        System.out.println(Base64.getEncoder().encodeToString(mergedSecretKey.getEncoded()));
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
    public void setDailyNym(int rand, String dailyNym) throws RemoteException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = LocalDateTime.now().format(dtf);

        StringBuilder sb = new StringBuilder();
        sb.append(date);
        sb.append(",");
        sb.append(dailyNym);
        allDailyPseudonyms.put(sb.toString(),rand);
    }

    @Override
    public Map<Integer, String> getAllNyms(String date) throws RemoteException {
        Map<Integer,String> dailyNyms = new HashMap<>();

        for(Map.Entry<String, Integer> entry : allDailyPseudonyms.entrySet()){
            if(entry.getKey().startsWith(date))dailyNyms.put(entry.getValue(), entry.getKey().split(",")[1]);
        }
        System.out.println("returning all daily pseudonyms: "+dailyNyms.size());
        return dailyNyms;
    }

    @Override
    public PublicKey enrollUsers(int gsm) throws RemoteException {
        System.out.println("Enrolling user:"+gsm);
        System.out.println(users.size());
        users.add(gsm);
        System.out.println("pair:"+pair);
        System.out.println("publickey:"+Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()));
        return pair.getPublic();
    }

    @Override
    public List<String> retrieveToken() throws RemoteException {
        System.out.println("Retrieving tokens");
        List<String> tokens=new ArrayList<>();
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
                dsa = Signature.getInstance("SHA1WithRSA");

                dsa.initSign(pair.getPrivate());

                byte[] bytes = new byte[20];
                random.nextBytes(bytes);

                dsa.update(bytes);

                dsa.update(day);

                byte[] realSig = dsa.sign();

                String data = Base64.getEncoder().encodeToString(bytes) + ";" + Base64.getEncoder().encodeToString(realSig);

                tokens.add(data);
            } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
                e.printStackTrace();
            }
        }
        return tokens;
    }
}

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.time.LocalDate;
import java.util.*;

public class MixingProxyImpl extends UnicastRemoteObject implements MixingProxy{
    List<String> capsules;
    PublicKey registrarKey = null;
    PublicKey matchingServiceKey = null;

    Registry myRegistry;
    Registrar registrar;

    Registry myRegistryMatching;
    MatchingService matchingService;

    KeyPair pair = null;

    protected MixingProxyImpl() throws RemoteException {
        capsules = new ArrayList<>();
        KeyPairGenerator keyGen=null;
        SecureRandom random=null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            pair = keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    protected MixingProxyImpl(int port) throws RemoteException {
        super(port);
    }

    protected MixingProxyImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public void connectToServer() throws RemoteException {
        try {
            // fire to localhostport 1099
            myRegistry = LocateRegistry.getRegistry("localhost", 1099);
            // search for CounterService
            registrar = (Registrar) myRegistry.lookup("Registrar");
            myRegistryMatching = LocateRegistry.getRegistry("localhost", 1097);
            matchingService = (MatchingService) myRegistryMatching.lookup("MatchingService");
            if (registrar != null) registrarKey = registrar.connect(this);
            if (matchingService != null) matchingService.connect(this);

            if(registrarKey!=null)System.out.println(Base64.getEncoder().encodeToString(registrarKey.getEncoded()));
            else System.out.println("registrarKey is null");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] registerVisit(String capsule) throws RemoteException {
        String token = capsule.split(",")[1];
        if(checkTokenValid(token)){
            System.out.println("Token is valid");
            capsules.add(capsule);
            Signature dsa = null;
            try {
                dsa = Signature.getInstance("SHA1WithRSA");

                dsa.initSign(pair.getPrivate());

                byte[] tekst = capsule.split(",")[2].getBytes();
                dsa.update(tekst);

                return dsa.sign();

            } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
                e.printStackTrace();
                return null;
            }
        }else{
            System.out.println("Token is not valid");
            return null;
        }
    }

    @Override
    public void continueVisit(String capsule) throws RemoteException {
        for(int i = 0; i< capsules.size();i++){
            String s = capsules.get(i);
            if(capsule.split(",")[1].equals(s.split(",")[1])){
                capsules.remove(s);
                capsules.add(s.concat(capsule.split(",")[0]));
            }
        }
    }

    @Override
    public void acknowledge() throws RemoteException {

    }

    @Override
    public void submitCapsules() throws RemoteException {
        List<String> random = new ArrayList<>(capsules);
        Collections.shuffle(random);
        matchingService.submitCapsules(random);
    }

    @Override
    public void submitAcknowledgements() throws RemoteException {

    }

    public boolean checkTokenValid(String token){
        Signature dsa = null;
        boolean used = false;
        boolean verified = false;
        for(String s : capsules){
            if(s.split(",")[1].equals(token))used = true;
        }

        long timeMilli = LocalDate.now().toEpochDay();
        byte[] day = new byte[8];
        for (int i = 7; i >= 0; i--) {
            day[i] = (byte) (timeMilli & 0xFF);
            timeMilli >>= 8;
        }

        try {
            System.out.println("signed data:" + token.split(";")[1] + token.split(";")[1].getBytes().length);
            System.out.println("unsigned data:" + token.split(";")[0] + token.split(";")[0].getBytes().length);
            dsa = Signature.getInstance("SHA1WithRSA");

            dsa.initVerify(registrarKey);

            dsa.update(Base64.getDecoder().decode(token.split(";")[0]));
            dsa.update(day);
            verified=dsa.verify(Base64.getDecoder().decode(token.split(";")[1]));

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        System.out.println(used +" "+ verified);
        return !used && verified;
    }
    public PublicKey getPublicKey(){
        return pair.getPublic();
    }

    public void clearData() throws RemoteException {
        submitCapsules();
        capsules.clear();
    }
}

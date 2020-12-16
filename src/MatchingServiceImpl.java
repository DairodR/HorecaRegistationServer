import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MatchingServiceImpl extends UnicastRemoteObject implements MatchingService {
    PublicKey mixingServiceKey = null;
    Set<String> capsules;

    Map<Integer, PublicKey> doctorKeys = new HashMap<>();

    Registry myRegistry;
    Registrar registrar;
    PublicKey registrarKey = null;

    List<String> critical;
    List<String> informedTokens;
    List<String> uninformedTokens;



    protected MatchingServiceImpl() throws RemoteException {
        capsules = new HashSet<>();
        critical = new ArrayList<>();
        uninformedTokens= new ArrayList<>();
        informedTokens = new ArrayList<>();

        connectToServer();
    }

    protected MatchingServiceImpl(int port) throws RemoteException {
        super(port);
    }

    protected MatchingServiceImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public void connectToServer() throws RemoteException {
        try {
            // fire to localhostport 1099
            myRegistry = LocateRegistry.getRegistry("localhost", 1099);
            // search for CounterService
            registrar = (Registrar) myRegistry.lookup("Registrar");
            if (registrar != null) registrarKey = registrar.connect(this);

            if (registrarKey != null) System.out.println(Base64.getEncoder().encodeToString(registrarKey.getEncoded()));
            else System.out.println("registrarKey is null");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect(MixingProxy mp) throws RemoteException {
        mixingServiceKey = mp.getPublicKey();
    }


    @Override
    public void connect(DoctorInterface d) throws RemoteException {
        doctorKeys.put(d.getId(), d.getPublicKey());
    }

    @Override
    public List<String> requestInfectedLogs() throws RemoteException {
        return critical;
    }

    @Override
    public void forwardLogs(int id, List<String> unsignedLogs, List<byte[]> signedLogs) throws RemoteException {
        Signature dsa = null;
        boolean allVerified = true;

        try {
            PublicKey key = doctorKeys.get(id);
            for (int i = 0; i < unsignedLogs.size(); i++) {
                dsa = Signature.getInstance("SHA1WithRSA");

                dsa.initVerify(key);

                dsa.update(unsignedLogs.get(i).getBytes());
                if (!dsa.verify(signedLogs.get(i))) allVerified = false;
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }

        System.out.println("hallo"+allVerified);
        if (allVerified) {
            boolean allValidated = true;
            for (int i = 0; i < unsignedLogs.size(); i++) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                LocalDateTime d = LocalDateTime.parse(unsignedLogs.get(i).split(",")[unsignedLogs.get(i).split(",").length - 1],dtf);
                String date = d.format(dtf).split(" ")[0];

                System.out.println(date);

                Map<Integer, String> dailyNyms = registrar.getAllNyms(date);

                MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance("SHA-256");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                int randomGetal = Integer.parseInt(unsignedLogs.get(i).split(",")[3]);

                System.out.println(randomGetal+":"+dailyNyms.get(randomGetal));

                md.update(BigInteger.valueOf(randomGetal).toByteArray());
                byte[] hash = md.digest(dailyNyms.get(randomGetal).getBytes());

                String generatedHash=Base64.getEncoder().encodeToString(hash);
                String givenHash = unsignedLogs.get(i).split(",")[2];

                System.out.println("generated hash: "+generatedHash);
                System.out.println("given hash: "+givenHash);

                if (!generatedHash.equals(givenHash)) allValidated = false;
            }
System.out.println("hallo2"+allValidated);
            if (allValidated) {
                System.out.println("all logs validated");
                for (int i = 0; i < unsignedLogs.size(); i++) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(unsignedLogs.get(i).split(",")[2]);
                    sb.append(",");
                    sb.append(unsignedLogs.get(i).split(",")[0]);
                    sb.append(",");
                    sb.append(unsignedLogs.get(i).split(",")[4]);

                    addCritical(sb.toString());
                    addInformedToken(unsignedLogs.get(i).split(",")[1].split(";")[1]);
                }
            }
        }
        addUninformedTokens();
        System.out.println("Critical");
        for(String s : critical){
            System.out.println(s);
        }
        System.out.println("informedTokens");
        for(String token : informedTokens){
            System.out.println(token);
        }
        System.out.println("uninformedTokens");
        for(String token : uninformedTokens){
            System.out.println(token);
        }
    }

    public void addUninformedTokens(){
        for(String s : critical){
            String hash = s.split(",")[0];
            System.out.println(capsules.size());
            for(String capsule:capsules){
                System.out.println(capsule);
                if(hash.equals(capsule.split(",")[2])){
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    LocalDateTime begin1 = LocalDateTime.parse(s.split(",")[1],dtf);
                    System.out.println("begin 1: "+begin1.toString());

                    LocalDateTime eind1 = LocalDateTime.parse(s.split(",")[2],dtf);
                    System.out.println("eind 1: "+eind1.toString());

                    List<LocalDateTime> capsuleAanwezig = new ArrayList<>();

                    LocalDateTime begin2 = LocalDateTime.parse(capsule.split(",")[0],dtf);
                    System.out.println("begin 2: "+begin2.toString());
                    capsuleAanwezig.add(begin2);

                    for(int i=3; i<capsule.split(",").length;i++){
                        LocalDateTime aanwezig = LocalDateTime.parse(capsule.split(",")[0],dtf);
                        System.out.println("nog steeds aanwezig: "+aanwezig.toString());
                        capsuleAanwezig.add(aanwezig);
                    }

                    for(LocalDateTime d :capsuleAanwezig) {
                        if (begin1.isBefore(d) && eind1.isAfter(d)){
                            addUninformedToken(capsule.split(",")[1].split(";")[1]);
                            break;
                        }
                    }
                }
            }
        }

    }

    @Override
    public void submitCapsules(List<String> c) throws RemoteException {
        capsules.addAll(c);
    }

    @Override
    public void submitAcknowledgements(List<String> tokens) throws RemoteException {
        for(String s : tokens){
            System.out.println("Acknowledgement received for token:");
            System.out.println(s);
            uninformedTokens.remove(s);
            addInformedToken(s);
        }

        System.out.println("informedTokens");
        for(String token : informedTokens){
            System.out.println(token);
        }
        System.out.println("uninformedTokens");
        for(String token : uninformedTokens){
            System.out.println(token);
        }
    }

    @Override
    public void forwardUnacknowledgedTokens() throws RemoteException {
        registrar.sendUnacknowledgedTokens(uninformedTokens);

    }
    public void addCritical(String s){
        if(!critical.contains(s)){
            critical.add(s);
        }
    }
    public void addInformedToken(String s){
        if (!informedTokens.contains(s)){
            informedTokens.add(s);
        }
    }
    public void addUninformedToken(String s){
        if (!uninformedTokens.contains(s)){
            uninformedTokens.add(s);
        }
    }

}

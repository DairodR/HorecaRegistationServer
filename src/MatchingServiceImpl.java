import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
    List<byte[]> informedTokens;
    List<byte[]> uninformedTokens;



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
    public void requestInfectedLogs() throws RemoteException {

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

        if (allVerified) {
            boolean allValidated = true;
            for (int i = 0; i < unsignedLogs.size(); i++) {
                DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                Date d = null;
                try {
                    d = dateFormatter.parse(unsignedLogs.get(i).split(",")[unsignedLogs.size() - 1]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String date = d.toString();
                Map<Integer, String> dailyNyms = registrar.getAllNyms(date);

                MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance("SHA-256");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                md.update(unsignedLogs.get(i).split(",")[2].getBytes());
                byte[] hash = md.digest(dailyNyms.get(Integer.parseInt(unsignedLogs.get(i).split(",")[3])).getBytes());

                if (!Arrays.equals(hash, unsignedLogs.get(i).split(",")[1].getBytes())) allValidated = false;
            }

            if (allValidated) {
                for (int i = 0; i < unsignedLogs.size(); i++) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(unsignedLogs.get(i).split(",")[2]);
                    sb.append(",");
                    sb.append(unsignedLogs.get(i).split(",")[0]);
                    sb.append(",");
                    sb.append(unsignedLogs.get(i).split(",")[4]);

                    critical.add(sb.toString());
                    informedTokens.add(unsignedLogs.get(i).split(",")[1].getBytes());
                }
            }
        }
        addUninformedTokens();
        System.out.println("Critical");
        for(String s : critical){
            System.out.println(s);
        }
        System.out.println("informedTokens");
        for(byte[] token : informedTokens){
            System.out.println(Base64.getEncoder().encodeToString(token));
        }
        System.out.println("uninformedTokens");
        for(byte[] token : uninformedTokens){
            System.out.println(Base64.getEncoder().encodeToString(token));
        }
    }

    public void addUninformedTokens(){
        for(String s : critical){
            String hash = s.split(",")[0];
            for(String capsule:capsules){
                if(hash.equals(capsule.split(",")[2])){
                    DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date begin1 = null;
                    Date eind1 = null;
                    Date begin2 = null;
                    Date eind2 = null;

                    try {
                        begin1 = dateFormatter.parse(s.split(",")[1]);
                        eind1 = dateFormatter.parse(s.split(",")[2]);
                        begin2 = dateFormatter.parse(capsule.split(",")[0]);
                        eind2 = dateFormatter.parse(capsule.split(",")[capsule.split(",").length-1]);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(begin1.before(eind2) && begin2.before(eind1))uninformedTokens.add(capsule.split(",")[1].getBytes());
                }
            }
        }

    }

    @Override
    public void submitCapsules(List<String> c) throws RemoteException {
        capsules.addAll(c);
    }

    @Override
    public void submitAcknowledgements() throws RemoteException {

    }

    @Override
    public void forwardUnacknowledgedLogs() throws RemoteException {

    }
}

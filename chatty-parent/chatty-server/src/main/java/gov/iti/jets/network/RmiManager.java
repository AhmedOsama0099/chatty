package gov.iti.jets.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiManager {
    private final static RmiManager RMI_MANAGER = new RmiManager();
    private Registry registry;

    private Logger logger = LoggerFactory.getLogger( RmiManager.class );

    private RmiManager() {
        try {
            this.registry = LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            try {
                this.registry = LocateRegistry.getRegistry(1099);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
            logger.info( "Registry was already alive. Obtained registry already running at port 1099" );
        }

        registerServices();
        addContactServices();
    }

    public static RmiManager getInstance() {
        return RMI_MANAGER;
    }

    private void registerServices() {
        try {
            registry.rebind("LoginService", new LoginServiceImpl());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private void addContactServices() {
        try {
            registry.rebind("AddContactService", new AddContactServiceImpl());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            registry.unbind("LoginService");
            registry.unbind("AddContactService");
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
//        try {
//            Runtime rt = Runtime.getRuntime();
//            Process proc = rt.exec("cmd /c netstat -ano | findstr 1099");
//            BufferedReader stdInput = new BufferedReader(new
//                    InputStreamReader(proc.getInputStream()));
//            String s;
//            if ((s = stdInput.readLine()) != null) {
//                int index = s.lastIndexOf(" ");
//                String sc = s.substring(index);
//                rt.exec("cmd /c Taskkill /PID" + sc + " /F");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}

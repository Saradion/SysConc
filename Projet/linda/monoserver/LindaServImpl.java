package linda.monoserver;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.server.TaskManager;
import linda.shm.CentralizedLinda;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

/**
 * Created by saradion on 08/01/17.
 */
public class LindaServImpl extends UnicastRemoteObject implements LindaServ {
    private Linda tupleSpace;

    private LindaServImpl() throws RemoteException {
        super();
        tupleSpace = new CentralizedLinda();
    }

    @Override
    public void write(Tuple t) throws RemoteException {
        tupleSpace.write(t);
    }

    @Override
    public Tuple take(Tuple template) throws RemoteException {
        return tupleSpace.take(template);
    }

    @Override
    public Tuple read(Tuple template) throws RemoteException {
        return tupleSpace.read(template);
    }

    @Override
    public Tuple tryTake(Tuple template) throws RemoteException {
        return tupleSpace.tryTake(template);
    }

    @Override
    public Tuple tryRead(Tuple template) throws RemoteException {
        return tupleSpace.tryRead(template);
    }

    @Override
    public Collection<Tuple> takeAll(Tuple template) throws RemoteException {
        return tupleSpace.takeAll(template);
    }

    @Override
    public Collection<Tuple> readAll(Tuple template) throws RemoteException {
        return tupleSpace.readAll(template);
    }

    @Override
    public void eventRegister(Linda.eventMode mode, Linda.eventTiming timing, Tuple template, Callback callback) throws RemoteException {
        tupleSpace.eventRegister(mode, timing, template, callback);
    }

    @Override
    public void addListener(Linda.eventMode mode, Linda.eventTiming timing, Tuple template, String listenerURI, String eventID) throws RemoteException {
        Callback notify = (Callback) t -> {
            try {
                TaskManager mgr = (TaskManager) Naming.lookup(listenerURI);
                mgr.notify(t, eventID);
            } catch (RemoteException | NotBoundException | MalformedURLException e) {
                e.printStackTrace();
            }
        };

        this.eventRegister(mode, timing, template, notify);
    }

    @Override
    public void debug(String prefix) throws RemoteException {
        tupleSpace.debug(prefix);
    }

    @Override
    public void reset() throws RemoteException {
        tupleSpace = new CentralizedLinda();
    }

    /**
     * Launches a Linda server on a port defined by the number passed as args[0].
     *
     * <p>
     * To launch a server using the command line :
     * > java LindaServImpl [portnumber]
     *
     * @param args the list of arguments.
     */
    public static void main(String[] args) {
        int port;
        long start = System.nanoTime();

        System.out.println("Starting Linda server...");
        if (args.length == 0) {
            System.out.println("Unable to start server : no port number specified.\n" +
                    "Usage : java LindaServImpl <portnumber>");
        }

        try {
            port = Integer.parseInt(args[0]);
            LocateRegistry.createRegistry(port);
            LindaServ serv = new LindaServImpl();
            Naming.rebind("//" + InetAddress.getLocalHost().getHostName() + ":" + port + "/lindaserv", serv);
            long timer = (System.nanoTime() - start) / 1000000;
            System.out.println("Server deployed on port " + port + ". Startup time : " + timer + " ms.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

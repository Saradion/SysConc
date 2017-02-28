package linda.server;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.monoserver.LindaServ;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Collection;

/**
 * Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it is connected to.
 */
public class LindaClient implements Linda {
    private String servURI, selfURI;
    private String clientID;
    private TaskManager mgr;
    private int eventNb = 1;

    /**
     * Initializes the Linda implementation.
     *
     * @param serverURI the URI of the server, e.g. "//localhost:4000/LindaServer".
     */
    public LindaClient(String serverURI, String name) {
        servURI = serverURI;
        clientID = name;

        try {
            selfURI = "//" + InetAddress.getLocalHost().getHostName() + ":" + 6000 + "/" + name;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            LocateRegistry.createRegistry(6000);
        } catch (RemoteException e) {
            System.out.println("Registry already existing");
        }

        try {
            mgr = new TaskManagerImpl(4);
            Naming.rebind(selfURI, mgr);
        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public LindaClient(String serverURI, String name, int port) {
        servURI = serverURI;
        clientID = name;

        try {
            selfURI = "//" + InetAddress.getLocalHost().getHostName() + ":" + port + "/" + name;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
            System.out.println("Found a previously existing registry on port " + port + ".");
        }

        try {
            mgr = new TaskManagerImpl(4);
            Naming.rebind(selfURI, mgr);
        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(Tuple t) {
        try {
            LindaServ tuplespace = (LindaServ) Naming.lookup(servURI);
            tuplespace.write(t);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Tuple take(Tuple template) {
        try {
            LindaServ tuplespace = (LindaServ) Naming.lookup(servURI);
            return tuplespace.take(template);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Tuple read(Tuple template) {
        try {
            LindaServ tuplespace = (LindaServ) Naming.lookup(servURI);
            return tuplespace.read(template);
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Tuple tryTake(Tuple template) {
        try {
            LindaServ tuplespace = (LindaServ) Naming.lookup(servURI);
            return tuplespace.tryTake(template);
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Tuple tryRead(Tuple template) {
        try {
            LindaServ tuplespace = (LindaServ) Naming.lookup(servURI);
            return tuplespace.tryRead(template);
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Collection<Tuple> takeAll(Tuple template) {
        try {
            LindaServ tuplespace = (LindaServ) Naming.lookup(servURI);
            return tuplespace.takeAll(template);
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Collection<Tuple> readAll(Tuple template) {
        try {
            LindaServ tuplespace = (LindaServ) Naming.lookup(servURI);
            return tuplespace.readAll(template);
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
        try {
            String eventID = clientID + eventNb;
            eventNb++;
            mgr.submit(callback, eventID);
            LindaServ tuplespace = (LindaServ) Naming.lookup(servURI);
            tuplespace.addListener(mode, timing, template, selfURI, eventID);
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void debug(String prefix) {
        try {
            LindaServ tuplespace = (LindaServ) Naming.lookup(servURI);
            tuplespace.debug(prefix);
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        try {
            LindaServ tuplespace = (LindaServ) Naming.lookup(servURI);
            tuplespace.reset();
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        try {
            Naming.unbind(selfURI);
            mgr.shutdown();
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
}

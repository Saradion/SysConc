package linda.monoserver;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

/**
 * A Linda tuplespace accessible remotely through RMI.
 *
 * This stuple
 * Created by saradion on 08/01/17.
 */
public interface LindaServ extends Remote {

    public void write(Tuple t) throws RemoteException;

    public Tuple take(Tuple template) throws RemoteException;

    public Tuple read(Tuple template) throws RemoteException;

    public Tuple tryTake(Tuple template) throws RemoteException;

    public Tuple tryRead(Tuple template) throws RemoteException;

    public Collection<Tuple> takeAll(Tuple template) throws RemoteException;

    public Collection<Tuple> readAll(Tuple template) throws RemoteException;

    public void eventRegister(Linda.eventMode mode, Linda.eventTiming timing, Tuple template, Callback callback) throws RemoteException;

    public void addListener(Linda.eventMode mode, Linda.eventTiming timing, Tuple template, String listenerURI, String eventID) throws RemoteException;

    public void debug(String prefix) throws RemoteException;

    public void reset() throws RemoteException;
}

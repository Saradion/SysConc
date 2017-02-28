package linda.server;

import linda.Callback;
import linda.Tuple;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * A manager used to submit Callbacks identified by a unique ID. The callbacks are then executed upon notification
 * of the manager through the notify() method.
 * <p>
 * Created by saradion on 09/01/17.
 */
public interface TaskManager extends Remote {
    /**
     * Registers a Callback to the manager under the identifier eventID.
     *
     * @param callback the callback to be registered.
     * @param eventID  the unique identifier of the event.
     * @throws RemoteException if the remote call fails.
     */
    public void submit(Callback callback, String eventID) throws RemoteException;

    /**
     * Notifies the manager that the callback identified by eventID has to be executed on target tuple.
     *
     * @param tuple   the target tuple on which to execute the callback.
     * @param eventID the ID of the callback to be executed.
     * @throws RemoteException if the remote call fails.
     */
    public void notify(Tuple tuple, String eventID) throws RemoteException;

    public void shutdown() throws RemoteException;
}

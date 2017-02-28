package linda.server;

import linda.Callback;
import linda.Tuple;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * An implementation of the TaskManager using an ScheduledThreadPoolExecutor
 * Created by saradion on 09/01/17.
 */
public class TaskManagerImpl extends UnicastRemoteObject implements TaskManager {
    private ExecutorService xs;
    private ConcurrentHashMap<String, Callback> callbackMap;

    public TaskManagerImpl(int nbThreads) throws RemoteException {
        xs = new ScheduledThreadPoolExecutor(nbThreads);
        callbackMap = new ConcurrentHashMap<>();
    }

    @Override
    public void submit(Callback callback, String eventID) throws RemoteException {
        callbackMap.put(eventID, callback);
    }

    @Override
    public void notify(Tuple tuple, String eventID) throws RemoteException {
        Callback cb = callbackMap.get(eventID);
        Task task = new Task(cb, tuple);
        xs.submit(task);
    }

    @Override
    public void shutdown() throws RemoteException {
        xs.shutdown();
    }
}

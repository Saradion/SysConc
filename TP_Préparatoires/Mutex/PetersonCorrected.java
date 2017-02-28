/**
 * An implementation of the Peterson algorithm for two concurrent threads.
 * <p>
 * Created by saradion on 02/10/16.
 */
public class PetersonCorrected {
    static int tour = 0;
    static boolean[] demande = {false, false};

    public static void main(String[] args) {
        Object shared_lock = new Object(); //initialization of the shared lock.
        Thread t1 = new Thread(new Proc2(shared_lock), "1");
        Thread t2 = new Thread(new Proc2(shared_lock), "0");
        t1.start();
        t2.start();
    }
}

class Proc2 implements Runnable {
    private int id, di;
    private final Object LOCK; // the lock associated with this object.

    Proc2(Object lock) {
        this.LOCK = lock; // initializes the object's explicit lock
    }

    @Override
    public void run() {
        id = Integer.parseInt(Thread.currentThread().getName());
        di = (id + 1) % 2;
        for (; ; ) {
            System.out.println("Thread " + id + " attend SC " + di);
            try {
                entrer(); // The thread tries to enter a critical section...
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Thread " + id + " en SC ");
            sortir(); // The thread exits the critical section
            System.out.println("Thread " + id + " hors SC ");
        }
    }

    /**
     *  Tries to enter a critical section of code.
     *
     * @throws InterruptedException
     */
    private void entrer() throws InterruptedException {
        PetersonCorrected.demande[id] = true;
        PetersonCorrected.tour = di;
        synchronized (LOCK) { // acquires ownership of LOCK's monitor
            while ((PetersonCorrected.tour != id) && (PetersonCorrected.demande[di])) {
                LOCK.wait(); // starts waiting on LOCK's monitor
            }
        }
    }

    /**
     * Exits a critical section of code
     */
    private void sortir() {
        PetersonCorrected.demande[id] = false;
        synchronized (LOCK) { // acquires ownership on LOCK's monitor
            LOCK.notifyAll(); // notify all threads waiting on LOCK's monitor
        }
    }
}

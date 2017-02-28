import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implements a naive mutual-exclusion algorithm for N threads.
 * <p>
 * Created by saradion on 02/10/16.
 */
public class NaiveAlgorithm {

    static AtomicBoolean isOccupied = new AtomicBoolean();

    public static void main(String[] args) throws InterruptedException {
        final CustomLock SHARED_LOCK = new CustomLock("SHARED_LOCK");
        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < Integer.parseInt(args[0]); i++) {
            threads.add(new NaiveThread(SHARED_LOCK));
            threads.get(i).start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Goodbye!");
    }
}

class NaiveThread extends Thread {
    private final CustomLock LOCK;

    NaiveThread(CustomLock new_lock) {
        LOCK = new_lock;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " : Starting.");
        for (int i = 0; i < 500; i++) {
            System.out.println(Thread.currentThread().getName() + " : Requesting access to CS.");
            try {
                requestAccess();
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            exit();
        }
    }

    private void requestAccess() throws InterruptedException {
        while (!(NaiveAlgorithm.isOccupied.compareAndSet(false, true))) {
            synchronized (LOCK) {
                LOCK.wait();
            }
        }
        System.out.println(Thread.currentThread().getName() + " : Access granted to CS.");
    }

    private void exit() {
        NaiveAlgorithm.isOccupied.set(false);
        synchronized (LOCK) {
            LOCK.notifyAll();
        }
    }
}

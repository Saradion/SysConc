import java.util.concurrent.Semaphore;

/**
 * Created by saradion on 28/09/16.
 */
public class Pool {
    private int size;
    private final int max_size;
    private Semaphore lock;

    public Pool(int obj) {
        lock = new Semaphore(1);
        size = 0;
        max_size = obj;
    }

    public void increment() throws InterruptedException {
        lock.acquire();
        size++;
        lock.release();
    }

    public int getObj() {
        return max_size;
    }

    public int getSize() {
        return size;
    }
}

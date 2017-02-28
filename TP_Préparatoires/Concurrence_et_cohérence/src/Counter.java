import java.util.concurrent.Semaphore;

/**
 * Created by saradion on 28/09/16.
 */
public class Counter {
    private long size;

    public Counter() {
        size = 0;
    }

    public void increment() {
        size++;
    }

    public long getSize() {
        return size;
    }
}

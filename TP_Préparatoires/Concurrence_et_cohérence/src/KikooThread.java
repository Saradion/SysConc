/**
 * Created by saradion on 28/09/16.
 */
public class KikooThread extends Thread {
    private Counter counter;
    private long nb_iter;

    public KikooThread(Counter counter, long nb_iter) {
        this.counter = counter;
        this.nb_iter = nb_iter;
    }

    @Override
    public void run() {
        for (int i = 0; i < nb_iter; i++) {
            counter.increment();
        }
    }
}

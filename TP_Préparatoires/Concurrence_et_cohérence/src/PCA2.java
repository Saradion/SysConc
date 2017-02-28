import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by saradion on 29/09/16.
 */
public class PCA2 {
    int dépot = 0;
    int retrait = 0;
    ConcurrentLinkedQueue<Integer> buffer = new ConcurrentLinkedQueue<>();
}

class Produce extends Thread {
    @Override
    public void run() {
        
    }
}

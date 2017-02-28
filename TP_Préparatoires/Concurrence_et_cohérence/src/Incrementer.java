/**
 * Created by saradion on 28/09/16.
 */
public class Incrementer extends Thread {
    private Pool pool;
    private int n;


    public Incrementer(int n, Pool pool) {
        this.n = n;
        this.pool = pool;
    }

    @Override
    public void run() {
        for (int i = 0; i < (this.pool.getObj()/n); i++) {
            try {
                pool.increment();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

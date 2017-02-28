package linda.server;

import linda.Callback;
import linda.Tuple;

/**
 * An object wraps a Callback and the target tuple on which said Callback call() method must be invoked.
 * <p>
 * Created by saradion on 10/01/17.
 */
class Task implements Runnable {
    private Callback cb;
    private Tuple target;

    Task(Callback cb, Tuple target) {
        this.cb = cb;
        this.target = target;
    }

    /**
     * Invokes the call method of the underlying Callback on the encapsulated Tuple.
     */
    @Override
    public void run() {
        cb.call(target);
    }
}

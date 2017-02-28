package linda.shm;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/** A multi-threaded version of the Linda kernel.
 *
 * This
 * Created by saradion on 11/01/17.
 */
public class MultiLinda implements Linda {
    private Linda kernel;
    private ExecutorService xs;

    public MultiLinda(int nbThreads) {
        this.kernel = new CentralizedLinda();
        this.xs = new ScheduledThreadPoolExecutor(nbThreads);
    }

    @Override
    public void write(Tuple t) {
        xs.submit(() -> kernel.write(t));
    }

    @Override
    public Tuple take(Tuple template) {
        Future<Tuple> taskRes = xs.submit(() -> kernel.take(template));
        return waitCompletion(taskRes);
    }

    @Override
    public Tuple read(Tuple template) {
        Future<Tuple> taskRes = xs.submit(() -> kernel.read(template));
        return waitCompletion(taskRes);
    }

    private <E> E waitCompletion(Future<E> taskRes) {
        E res = null;
        while (res == null) {
            try {
                res = taskRes.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return res;
    }

    @Override
    public Tuple tryTake(Tuple template) {
        Future<Tuple> taskRes = xs.submit(() -> kernel.tryTake(template));
        return waitCompletion(taskRes);
    }

    @Override
    public Tuple tryRead(Tuple template) {
        Future<Tuple> taskRes = xs.submit(() -> kernel.tryRead(template));
        return waitCompletion(taskRes);
    }

    @Override
    public Collection<Tuple> takeAll(Tuple template) {
        Future<Collection<Tuple>> taskRes = xs.submit(() -> kernel.takeAll(template));
        return waitCompletion(taskRes);
    }

    @Override
    public Collection<Tuple> readAll(Tuple template) {
        Future<Collection<Tuple>> taskRes = xs.submit(() -> kernel.readAll(template));
        return waitCompletion(taskRes);
    }

    @Override
    public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
        xs.submit(() -> kernel.eventRegister(mode, timing, template, callback));
    }

    @Override
    public void debug(String prefix) {
        xs.submit(() -> kernel.debug(prefix));
    }
}

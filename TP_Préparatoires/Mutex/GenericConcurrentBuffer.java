import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * An improvement on the ConcurrentBuffer class, adding generics.
 * <p>
 * Created by saradion on 03/10/16.
 */
public class GenericConcurrentBuffer<E> {
    private ArrayBlockingQueue<E> buffer;
    private ConcurrentLinkedQueue<Object> priority_queue;

    GenericConcurrentBuffer(int new_buffer_capa) {
        buffer = new ArrayBlockingQueue<>(new_buffer_capa);
        priority_queue = new ConcurrentLinkedQueue<>();
    }

    void drop_item(E item) throws InterruptedException {
        while (!buffer.offer(item)) {
            System.out.println(Thread.currentThread().getName() + ": Waiting to drop...");
            this.wait(5000);
        }
        Object next_in_queue = priority_queue.peek();
        if (next_in_queue != null) {
            synchronized (next_in_queue) {
                next_in_queue.notifyAll();
            }
        }
    }

    E retrieve_item() throws InterruptedException {
        E retrieved;

        final Object LOCK = new Object();
        priority_queue.add(LOCK);

        synchronized (LOCK) {
            while (priority_queue.peek() != LOCK || (buffer.peek() == null)) {
                LOCK.wait();
            }
        }

        retrieved = buffer.poll();

        priority_queue.poll();
        Object next_in_queue = priority_queue.peek();
        if (next_in_queue != null) {
            synchronized (next_in_queue) {
                next_in_queue.notifyAll();
            }
        }

        return retrieved;
    }
}

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by saradion on 03/10/16.
 */
public class TestGenericBuffer {
    static GenericConcurrentBuffer<Integer> buffer;

    public static void main(String[] args) throws InterruptedException {
        buffer = new GenericConcurrentBuffer<>(Integer.parseInt(args[3]));
        ArrayList<ProducerThread> producer_threads = new ArrayList<>();
        ArrayList<ConsumerThread> consumer_threads = new ArrayList<>();

        for (int i = 0; i < Integer.parseInt(args[0]); i++) {
            producer_threads.add(new ProducerThread());
        }

        for (int i = 0; i < Integer.parseInt(args[1]); i++) {
            consumer_threads.add(new ConsumerThread(Integer.parseInt(args[2])));
        }

        System.out.println("Consumer threads : " + consumer_threads);
        System.out.println("Producer threads : " + producer_threads);
        consumer_threads.forEach(Thread::start);
        producer_threads.forEach(Thread::start);

        for (Thread thread : consumer_threads) {
            thread.join();
        }

        System.out.println("Stopping production...");
        producer_threads.forEach(ProducerThread::stopProduction);
    }




}

class ProducerThread extends Thread {
    private Random seed;
    private AtomicBoolean is_producing;

    ProducerThread() {
        seed = new Random();
    }

    @Override
    public void run() {
        this.is_producing = new AtomicBoolean(true);
        while (this.is_producing.get()) {
            try {
                sleep(seed.nextInt(500));
                System.out.println(Thread.currentThread().getName() + " : Dropping an item.");
                TestGenericBuffer.buffer.drop_item(seed.nextInt(1000));
                System.out.println(Thread.currentThread().getName() + " : Done.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void stopProduction() {
        this.is_producing.set(false);
        System.out.println(Thread.currentThread().getName() + " : Stopping production.");
    }
}

class ConsumerThread extends Thread {
    private HashSet<Integer> pool;
    private int pool_capacity;

    ConsumerThread(int pool_capa) {
        pool = new HashSet<>();
        pool_capacity = pool_capa;
    }

    @Override
    public void run() {
        while (pool.size() != pool_capacity) {
            try {
                sleep(new Random().nextInt(2000));
                System.out.println(Thread.currentThread().getName() + " : Trying to retrieve an item...");
                int retrieved = TestGenericBuffer.buffer.retrieve_item();
                pool.add(retrieved);
                System.out.println(Thread.currentThread().getName() + " : Retrieved item " + retrieved +
                    ". Current pool : " + pool);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(Thread.currentThread().getName() + " : Goodbye!");
    }
}

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Implements a mutual-exclusion protocol for N threads using a FIFO structures for thread priority.
 * <p>
 * The protocol assigns a lock to each thread. Whenever a thread requires access to a critical section of code, it
 * places its assigned lock in a designated queue. It then calls wait() on its assigned lock, as long as said lock isn't
 * at the head of the queue.
 * A thread exiting a critical section of code first removes its assigned lock from the queue, then looks at the queue.
 * If the queue contains at least one lock, the exiting thread notifies the threads waiting on the lock at the head of
 * the queue.
 * <p>
 * This algorithm is supposed to be deadlock-free, starvation-free and to provide bounded waiting (since a thread waits
 * at most for N-1 other threads to gain access to a critical section before gaining access itself.
 * <p>
 * Created by saradion on 03/10/16.
 */
public class PriorityAlgorithm {

    static ConcurrentLinkedQueue<Object> priorityQueue; // The priority queue

    public static void main(String[] args) throws InterruptedException {
        ArrayList<Thread> threads = new ArrayList<>();
        priorityQueue = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < Integer.parseInt(args[0]); i++) {
            Thread new_thread = new PriorityThread(new CustomLock(Integer.toString(i)));
            threads.add(new_thread);
            new_thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Goodbye!");
    }
}

class PriorityThread extends Thread {
    private final CustomLock LOCK;

    PriorityThread(CustomLock new_lock) {
        LOCK = new_lock;
    }

    @Override
    public void run() {
        LOCK.setThread(Thread.currentThread());
        System.out.println(Thread.currentThread().getName() + " : Starting with lock " + LOCK);
        for (int i = 0; i < 500; i++) {
            try {
                sleep((new Random()).nextInt(4000) + 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                requestAccess();
                System.out.println();
                sleep((new Random()).nextInt(500) + 500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            exit();
        }
    }

    private void requestAccess() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " : Requesting access.");
        PriorityAlgorithm.priorityQueue.add(LOCK);
        while (PriorityAlgorithm.priorityQueue.peek() != LOCK) {
            synchronized (LOCK) {
                LOCK.wait();
            }
        }
        System.out.println(Thread.currentThread().getName() + " : Access granted..");
    }

    private void exit() {
        synchronized (PriorityAlgorithm.priorityQueue) {
            PriorityAlgorithm.priorityQueue.poll();
            System.out.println(Thread.currentThread().getName() + " : Exiting. Current queue : " + PriorityAlgorithm.priorityQueue);
            Object nextInQueue = PriorityAlgorithm.priorityQueue.peek();
            if (nextInQueue != null) {
                System.out.println(Thread.currentThread().getName() + " : Awakening thread with lock tagged : " + nextInQueue);
                synchronized (nextInQueue) {
                    nextInQueue.notifyAll();
                }
            }
        }
    }
}

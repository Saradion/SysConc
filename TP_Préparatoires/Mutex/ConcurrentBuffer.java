/**
 * A implementation of a simple integer buffer.
 * <p>
 * Created by saradion on 03/10/16.
 */
class ConcurrentBuffer {
    private int size;
    private int used_slots = 0;
    private int tail_pointer = 0;
    private int head_pointer = 0;
    private int trace = 0;
    private int[] buffer;

    ConcurrentBuffer(int t) {
        size = t;
        buffer = new int[size];
    }

    synchronized void déposer() throws InterruptedException {
            while (used_slots == size) {
                System.out.println(Thread.currentThread().getName() + " : Cannot produce - Buffer is full");
                this.wait();
            }
            buffer[tail_pointer] = trace++;
            tail_pointer = (tail_pointer + 1) % size;
            used_slots++;
            String msg = "P : " + (trace - 1);
            if (used_slots == size) msg = msg + " (PLEIN)";
            System.out.println(msg);
            this.notifyAll();
    }

    synchronized int retirer() throws InterruptedException {
            while (used_slots == 0) {
                System.out.println(Thread.currentThread().getName() + " : Cannot consume - Buffer is empty");
                this.wait();
            }
            int i;
            i = buffer[head_pointer];
            head_pointer = (head_pointer + 1) % size;
            used_slots--;

            String msg = "C : " + i;
            if (used_slots == 0) msg = msg + " (VIDE)";
            System.out.println(msg);
            this.notifyAll();
            return i;
    }

}

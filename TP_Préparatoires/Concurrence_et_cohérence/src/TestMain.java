import java.util.ArrayList;

/**
 * Created by saradion on 28/09/16.
 */
public class TestMain {
    public static void main(String[] args) {
        ArrayList<Counter> list = new ArrayList<>();
        ArrayList<Thread> threads = new ArrayList<>();

        double current = System.nanoTime();

        for(int i = 1; i <= Integer.parseInt(args[0]); i++) {
            Counter new_counter = new Counter();
            list.add(new_counter);
            System.out.println("Spawning thread N°" + i);
            KikooThread new_thread = new KikooThread(new_counter, Long.parseLong(args[1])/Long.parseLong(args[0]));
            threads.add(new_thread);
        }

        threads.stream().forEach(Thread::start);
        threads.stream().forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println("Summing every thread result...");
        long result = list.stream().mapToLong(Counter::getSize).sum();

        double elapsed = System.nanoTime() - current;

        System.out.println("Finished in " + elapsed + " seconds. End result : " + result + ". Goal : " + args[1] + ".");
    }
}

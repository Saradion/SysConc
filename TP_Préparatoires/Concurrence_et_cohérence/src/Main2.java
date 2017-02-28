import java.util.ArrayList;

/**
 * Created by saradion on 27/09/16.
 */
public class Main2 {
    public static void main(String[] args) throws InterruptedException {
        double current, elapsed;
        Pool pool = new Pool(1000000000);
        ArrayList<Thread> threads_t = new ArrayList<>();

        current = System.nanoTime();

        for (int i = 1; i <= Integer.parseInt(args[0]); i++) {
            System.out.println("Spawning thread N°" + i);
            Incrementer new_thread = new Incrementer(Integer.parseInt(args[0]), pool);
            threads_t.add(new_thread);
        }

        threads_t.stream().forEach(Thread::start);
        threads_t.stream().forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        elapsed = System.nanoTime() - current;

        System.out.println("Elapsed time : " + elapsed);
        System.out.println("Pool size : " + pool.getSize());
    }
}

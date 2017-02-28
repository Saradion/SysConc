package linda.applications;

import linda.shm.CentralizedLinda;
import linda.Linda;
import linda.Tuple;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** Quelques explications :
 *
 * L'algorithme déployée est une version 'bêtement' parallélisée du crible d'Érathosthène.
 * On stock des entiers dans l'espace de tuples, puis on parcourt à l'aide d'une boucle parallélisée ces entiers en
 * supprimant les entiers non-premiers.
 *
 * L'éxécution montrera que les temps ne sont pas du tout optimaux comparé à la version séquentielle. Il est probable
 * que l'algorithme implémenté soit trop lourd, peu adapté à la parallélisation ou à l'usage de Linda.
 * Created by saradion on 13/12/16.
 */
public class CalculPremiers {
    public static void main(String[] args) {
        if (args.length < 0 && args.length > 1) {
            System.out.println("Use : java CalculPremiers <integer>");
        } else {
            double start = System.nanoTime();
            sequential(Integer.parseInt(args[0]));
            double end = System.nanoTime();
            System.out.println("Sequential time : " + (end - start) / 1E6 + "ms");

            start = System.nanoTime();
            parallelLinda(Integer.parseInt(args[0]));
            end = System.nanoTime();
            System.out.println("Parallel time : " + (end - start) / 1E6 + "ms");
        }
    }

    private static void sequential(int k) {
        HashMap<Integer, Boolean> map = new HashMap<>();
        for (int i = 2; i <= k; i++) {
            map.put(i, true);
        }

        for (int i = 2; i <= k; i++) {
            for (int j = i + 1; j <= k; j++) {
                if (map.get(j) && (j % i == 0)) {
                    map.replace(j, false);
                }
            }
        }
        System.out.println();
    }

    private static void parallelLinda(int k) {
        Linda tserv = new CentralizedLinda();
        for (int i = 2; i <= k; i++) {
            tserv.write(new Tuple(i));
        }

        for (int i = 2; i <= k; i++) {
            ExecutorService exec = new ScheduledThreadPoolExecutor(4);
            Tuple res = tserv.tryRead(new Tuple(i));
            if (res != null) {
                System.out.println(res);
                for (int j = i*2; j <= k; j += i) {
                    int tmp = j;
                    exec.submit(() -> tserv.tryTake(new Tuple(tmp)));
                }
            }
            exec.shutdown();
            try {
                exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println();
    }
}

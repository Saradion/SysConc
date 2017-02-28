// Time-stamp: <08 déc 2009 08:30 queinnec@enseeiht.fr>

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class PhiloSem implements StrategiePhilo {

    /*****************************************************************/
    private Queue<Integer> philo_queue;
    private Queue<Semaphore> ask_queue;
    private EtatPhilosophe[] states;
    private Semaphore queue_access;

    public PhiloSem(int nbPhilosophes) {
        ask_queue = new ConcurrentLinkedQueue<>();
        philo_queue = new ConcurrentLinkedQueue<>();
        queue_access = new Semaphore(1);
        states = new EtatPhilosophe[nbPhilosophes];
        for (int i = 0; i < nbPhilosophes; i++) {
            states[i] = EtatPhilosophe.Pense;
        }
    }

    /**
     * Le philosophe no demande les fourchettes.
     * Précondition : il n'en possède aucune.
     * Postcondition : quand cette méthode retourne, il possède les deux fourchettes adjacentes à son assiette.
     */
    public void demanderFourchettes(int no) throws InterruptedException {
        queue_access.acquire();
        Semaphore tmp = new Semaphore(0);
        ask_queue.offer(tmp);
        philo_queue.offer(no);
        if (philo_queue.peek() == no && states[Main.PhiloDroite(no)] != EtatPhilosophe.Mange && states[Main.PhiloGauche(no)] != EtatPhilosophe.Mange) {
            states[no] = EtatPhilosophe.Mange;
            queue_access.release();
        } else {
            states[no] = EtatPhilosophe.Demande;
            queue_access.release();
            tmp.acquire();
        }
    }

    /**
     * Le philosophe no rend les fourchettes.
     * Précondition : il possède les deux fourchettes adjacentes à son assiette.
     * Postcondition : il n'en possède aucune. Les fourchettes peuvent être libres ou réattribuées à un autre philosophe.
     */
    public void libererFourchettes(int no) throws InterruptedException {
        queue_access.acquire();
        states[no] = EtatPhilosophe.Pense;
        Semaphore next_prio = ask_queue.peek();
        Integer next_philo = philo_queue.peek();
        while (next_philo != null && states[Main.PhiloGauche(next_philo)] != EtatPhilosophe.Mange && states[Main.PhiloDroite(next_philo)] != EtatPhilosophe.Mange) {
            next_prio.release();
            ask_queue.poll();
            philo_queue.poll();
            states[next_philo] = EtatPhilosophe.Mange;
            next_philo = philo_queue.peek();
            next_prio = ask_queue.peek();
        }
        queue_access.release();
    }

    /**
     * Nom de cette stratégie (pour la fenêtre d'affichage).
     */
    public String nom() {
        return "Implantation Sémaphores, stratégie ???";
    }

}


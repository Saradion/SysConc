// Time-stamp: <08 déc 2009 08:30 queinnec@enseeiht.fr>

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class PhiloSem1 implements StrategiePhilo {

    /*****************************************************************/
    private ArrayList<Semaphore> forks;
    private int nbPhilo;

    public PhiloSem1(int nbPhilosophes) {
        nbPhilo = nbPhilosophes;
        forks = new ArrayList<>();
        for (int i = 0; i < nbPhilosophes; i++) {
            forks.add(new Semaphore(1));
        }
    }

    /** Le philosophe no demande les fourchettes.
     *  Précondition : il n'en possède aucune.
     *  Postcondition : quand cette méthode retourne, il possède les deux fourchettes adjacentes à son assiette. */
    public void demanderFourchettes (int no) throws InterruptedException
    {
        forks.get((no % nbPhilo)).acquire();
        forks.get((no % nbPhilo) + 1).acquire();
    }

    /** Le philosophe no rend les fourchettes.
     *  Précondition : il possède les deux fourchettes adjacentes à son assiette.
     *  Postcondition : il n'en possède aucune. Les fourchettes peuvent être libres ou réattribuées à un autre philosophe. */
    public void libererFourchettes (int no)
    {
        forks.get((no % nbPhilo)).release();
        forks.get((no % nbPhilo) + 1).release();
    }

    /** Nom de cette stratégie (pour la fenêtre d'affichage). */
    public String nom() {
        return "Implantation Sémaphores, stratégie ???";
    }

}


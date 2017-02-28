// Time-stamp: <08 déc 2009 08:30 queinnec@enseeiht.fr>

import java.util.concurrent.Semaphore;

public class PhiloSem2 implements StrategiePhilo {

    /*****************************************************************/
    private Semaphore access_array;
    private EtatPhilosophe[] etatPhilosophes;
    private Semaphore[] meeting;
    private int nbPhilo;

    public PhiloSem2(int nbPhilosophes) {
        nbPhilo = nbPhilosophes;
        access_array = new Semaphore(1);
        etatPhilosophes = new EtatPhilosophe[nbPhilo];
        meeting = new Semaphore[nbPhilo];
        for (int i = 0; i < nbPhilo; i++) {
            meeting[i] = new Semaphore(0);
            etatPhilosophes[i] = EtatPhilosophe.Pense;
        }
    }

    /**
     * Le philosophe no demande les fourchettes.
     * Précondition : il n'en possède aucune.
     * Postcondition : quand cette méthode retourne, il possède les deux fourchettes adjacentes à son assiette.
     */
    public void demanderFourchettes(int no) throws InterruptedException {
        access_array.acquire();
        etatPhilosophes[no] = EtatPhilosophe.Demande;
        if (etatPhilosophes[Main.PhiloDroite(no)] != EtatPhilosophe.Mange && etatPhilosophes[Main.PhiloGauche(no)] != EtatPhilosophe.Mange) {
            access_array.release();
        } else {
            access_array.release();
            meeting[no].acquire();
        }
        etatPhilosophes[no] = EtatPhilosophe.Mange;
    }

    /**
     * Le philosophe no rend les fourchettes.
     * Précondition : il possède les deux fourchettes adjacentes à son assiette.
     * Postcondition : il n'en possède aucune. Les fourchettes peuvent être libres ou réattribuées à un autre philosophe.
     */
    public void libererFourchettes(int no) throws InterruptedException {
        access_array.acquire();
        etatPhilosophes[no] = EtatPhilosophe.Pense;
        if (etatPhilosophes[Main.PhiloGauche(no)] == EtatPhilosophe.Demande && etatPhilosophes[Main.PhiloGauche(Main.PhiloGauche(no))] != EtatPhilosophe.Mange) {
            meeting[Main.PhiloGauche(no)].release();
        }
        if (etatPhilosophes[Main.PhiloDroite(no)] == EtatPhilosophe.Demande && etatPhilosophes[Main.PhiloDroite(Main.PhiloDroite(no))] != EtatPhilosophe.Mange) {
            meeting[Main.PhiloDroite(no)].release();
        }
        access_array.release();
    }

    /**
     * Nom de cette stratégie (pour la fenêtre d'affichage).
     */
    public String nom() {
        return "Implantation Sémaphores, stratégie ???";
    }

}


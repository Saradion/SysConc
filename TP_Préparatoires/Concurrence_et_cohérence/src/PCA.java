// v0.1, 7/9/16 (PM)
public class PCA {

    static final int N = 10;
    static int dépôt = 0;
    static int retrait = 0;
    static int [] tampon = new int[N];
    static final Object LOCK = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(new Producteur());
        Thread t2 = new Thread(new Consommateur());
        t1.start();
        t2.start();
    }
}

class Producteur implements Runnable {
// dépose des suites de N entiers identiques, en incrémentant la valeur déposée à chaque nouvelle suite
    public void run() {
        for (;;) {
            System.out.println("production...");
            synchronized (PCA.LOCK) {
                while (PCA.dépôt - PCA.retrait == PCA.N) {
                    try {
                        PCA.LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("PCA.dépôt " + PCA.dépôt / PCA.N);
                PCA.tampon[PCA.dépôt % PCA.N] = PCA.dépôt / PCA.N;
                PCA.dépôt++;
                System.out.println("fait");
                PCA.LOCK.notifyAll();
            }
        }
    }
}

class Consommateur implements Runnable {
    public void run() {
        int item;
        for (;;) {
            System.out.println("repos...");
            synchronized (PCA.LOCK) {
                while (PCA.dépôt == PCA.retrait) {
                    try {
                        PCA.LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("PCA.retrait");
                item = PCA.tampon[PCA.retrait % PCA.N];
                PCA.retrait++;
                PCA.LOCK.notifyAll();
            }
            System.out.println("consommation "+item);
        }
    }
}
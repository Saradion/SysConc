/**
 * Corrected version of the ProdConso class and its subclasses.
 * <p>
 * Created by saradion on 03/10/16.
 */
class ProducteurCorrected implements Runnable {
    private ConcurrentBuffer tampon;

    ProducteurCorrected(ConcurrentBuffer t) {
        tampon = t;
    }

    public void run() {
        try {
            Thread.sleep(10);
            for (int i = 0; i < 25; i++) {
                tampon.déposer();
                Thread.sleep(2 * i);
            }
        } catch (InterruptedException e) {
            System.out.println("interrompu");
        }
    }
}

class ConsommateurCorrected implements Runnable {
    private ConcurrentBuffer tampon;
    private int identité;

    ConsommateurCorrected(ConcurrentBuffer t) {
        tampon = t;
    }

    public void run() {
        int res;
        for (int i = 0; i < 25; i++) {
            try {
                res = tampon.retirer();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(10 * i);
            } catch (InterruptedException e) {
                System.out.println("interrompu");
            }
        }
    }
}

public class PvCTest {
    public static void main(String[] args) {
        int nbProd = 5;
        int nbConso = 10;
        int tailleTampon = 10;

        if (args.length != 3) {
            System.out.println("java ProdConso <nbProd> <nbConso> <nbCases>");
            System.out.println("-> choix par défaut : " + nbProd + "/" + nbConso + "/" + tailleTampon);
        } else {
            nbProd = Integer.parseInt(args[0]);
            nbConso = Integer.parseInt(args[1]);
            tailleTampon = Integer.parseInt(args[2]);
        }
        System.out.println("nbProd (arg1) : " + nbProd + " /nbConso (arg2) : " + nbConso
                + " /nbCases) (arg3) : " + tailleTampon);
        ConcurrentBuffer t = new ConcurrentBuffer(tailleTampon);
        for (int i = 0; i < nbProd; i++) {
            new Thread(new ProducteurCorrected(t)).start();
        }
        for (int i = 0; i < nbConso; i++) {
            new Thread(new ConsommateurCorrected(t)).start();
        }
    }
}
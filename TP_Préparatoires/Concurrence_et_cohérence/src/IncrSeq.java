public class IncrSeq {

    private static long cpt = 0L;
    private static final long NB_IT = 1000L;

    private static synchronized void increment(int j) {
        cpt = cpt + j/j;
    }

    public static void main(String[] args) {
        class IncrN extends Thread {
            private int n;

            private IncrN(int n) {
                super();
                this.n = n;
            }

            @Override
            public void run() {
                for(int j = 1; j <= 1000000/n; j++) {
                    increment(j);
                }
            }
        }

        double current = System.nanoTime();
        for (int i = 0; i < IncrSeq.NB_IT; i++) {
            try {
                (new IncrN(Integer.parseInt(args[0]))).start();
                Thread.sleep(1,0);
            } catch (InterruptedException ie) {
                System.out.println("InterruptedException : " + ie);
            }
        }
        double elapsed = System.nanoTime() - current;

        System.out.println(elapsed);
    }
}
package linda.server;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

/**
 * Created by saradion on 09/01/17.
 */
public class Test {
    public static void main(String[] args) throws InterruptedException {
        Linda cl1 = new LindaClient("//localhost:6001/lindaserv", "client1");
        Linda cl2 = new LindaClient("//localhost:6001/lindaserv", "client2");

        cl1.write(new Tuple(1, "Blabla"));
        Tuple test = cl2.read(new Tuple(Integer.class, String.class));
        cl1.eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.FUTURE, new Tuple(Boolean.class), (Callback) System.out::println);
        cl1.eventRegister(Linda.eventMode.READ, Linda.eventTiming.FUTURE, new Tuple(Boolean.class), (Callback) t -> System.out.println("FAIL"));
        System.out.println(test);
        Thread.sleep(10000);
        cl2.write(new Tuple(true));
        cl2.write(new Tuple(true));
        ((LindaClient) cl1).kill();
        ((LindaClient) cl2).kill();
        System.out.println("Finished");
    }
}

/** A little customized lock that can be loosely associated to a given thread.
 *
 * Created by saradion on 03/10/16.
 */
public class CustomLock {
    private String id;
    private Thread thread;

    CustomLock(String new_id) {
        id = new_id;
    }

    void setThread(Thread new_thread) {
        thread = new_thread;
    }

    @Override
    public String toString() {
        return "{" + id + " : " + thread.getName() + "}" ;
    }
}

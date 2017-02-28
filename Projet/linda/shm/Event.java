package linda.shm;

import linda.Callback;
import linda.Tuple;
import linda.Linda.eventMode;
/** Classe représentant les évènements enregistrés dans Linda.
 * Contient le callback, le mode de passage et le template sur lequel le callback est appelé. */
public class Event {
	private Callback callback;
	private eventMode mode;
	private Tuple template;
	
	protected Event(Callback callback, eventMode mode, Tuple template){
		this.callback = callback;
		this.mode = mode;
		this.template = template;
	}
	
	protected Callback getCallback() {
		return callback;
	}
	protected eventMode getMode() {
		return mode;
	}
	protected Tuple getTemplate() {
		return template;
	}
	
	@Override
	public String toString() {
		return callback.toString() +" " + mode.toString() + " " + template.toString();
	}
}

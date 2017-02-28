package linda.shm;


import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {

	// Tuples actuellement en mémoire
	private Collection<Tuple> tuples;
	// Templates moniteurs sur lesquels attendent des reads
	private Collection<Tuple> waitRead;
	// Templates moniteurs sur lesquels attendent des reads
	private Collection<Tuple> waitTake;
	// Mutex pour l'accès à Take et Write
	private Semaphore mutexTakeWrite;
	// Semaphore(n) pour accès à read
	private Semaphore read;
	// Mutex pour l'accès à la liste des évènements enregistrés
	private Semaphore mutexRegisteredEvents;
	// Mutex pour l'accès à la liste des reads en attente
	private Semaphore mutexWaitRead;
	// Mutex pour l'accès à la liste des takes en attente
	private Semaphore mutexWaitTake;
	// Liste des évènements enregistrés
	private Collection<Event> registeredEvents;
	// Nombre maximal de lecteurs en simultané
	private static int nbRead = 4;
	


	public CentralizedLinda() {
		
		tuples = new LinkedList<Tuple>();
		waitRead = new LinkedList<Tuple>();
		waitTake = new LinkedList<Tuple>();
		mutexTakeWrite = new Semaphore(1);
		registeredEvents = new LinkedList<Event>();
		read = new Semaphore(nbRead);
		mutexRegisteredEvents = new Semaphore(1);
		mutexWaitRead = new Semaphore(1);
		mutexWaitTake = new Semaphore(1);

	}

	@Override
	public void write(Tuple t){
		try {
			// Aucun autre write ou take
			mutexTakeWrite.acquire();
			// Aucun lecteur
			read.acquire(nbRead);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Est ce que un write a été exécuté/ va être exécuté (event ou take en attente
		boolean awakenTake = false;
		try {
			// Accès exclusif à la liste des évènements enregistrés+

			mutexRegisteredEvents.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// liste des evènements à supprimer
		ArrayList<Event> deleteEvents = new ArrayList<Event>();
		// On regarde si un callback peut être appelé
		for(Event event:registeredEvents){
			if (t.matches(event.getTemplate()) && !awakenTake){
				if (event.getMode().equals(eventMode.READ)){
					event.getCallback().call(t.deepclone());
					deleteEvents.add(event);
				}
				else {
					event.getCallback().call(t.deepclone());
					deleteEvents.add(event);
					awakenTake = true;
				}
			}
		}
		// Suppression des évènements correspondant aux callbacks appelés
		for (Event event : deleteEvents){
			registeredEvents.remove(event);
		}
		mutexRegisteredEvents.release();
		
		if (!awakenTake){
			// On ajoute le tuple à la liste si il n'a pas déjà été pris par un callback en mode take
			tuples.add(t.deepclone());
			try {
				mutexWaitTake.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// On regarde si on peut réveiller un take sur ce tuple
			for (Tuple template:waitTake){
				synchronized(template){
					if (t.matches(template) && !awakenTake){
						template.notify();
						awakenTake = true;
					}
				}
			}
			mutexWaitTake.release();
		}
		
		if (!awakenTake){
			try {
				mutexWaitRead.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Si on a pas réveillé de take (le tuple est encore dans liste) on regarde si on peut réveiller un read
			for (Tuple template:waitRead){
				synchronized(template){
					if(t.matches(template)){
						template.notify();
					}
				}
			}
			mutexWaitRead.release();
		}
		mutexTakeWrite.release();
		read.release(nbRead);

	}

	@Override
	public Tuple take(Tuple template) {
		try {
			// Aucun autre write ou take
			mutexTakeWrite.acquire();
			// Aucun lecteur
			read.acquire(nbRead);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Tuple retour = null;
		// On regarde si un tuple correspond dans la liste des tuples
		for (Tuple tuple : tuples){
			if (tuple.matches(template)){
				retour = tuple;
			}
		}
		// Si aucun ne correspond
		if (retour == null){
			// Copie de template dans une variable pour éviter de "perdre" template (appel avec new Tuple(..) par exemple)
			Tuple templateMem = template.deepclone();
			// On ajoute le template à la liste des take en attente

			try {
				mutexWaitTake.acquire();
				waitTake.add(templateMem);
				mutexWaitTake.release();
				mutexTakeWrite.release();
				read.release(nbRead);
				synchronized(templateMem){

					// On attend sur le template
					templateMem.wait();
				}
				mutexTakeWrite.acquire();
				read.acquire(nbRead);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				mutexWaitTake.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
				// On s'enlève de la liste d'attente
				waitTake.remove(templateMem);
			mutexWaitTake.release();
			mutexTakeWrite.release();
			read.release(nbRead);
			// On va chercher le tuple à retourner
			return take(template);
		}
		else {
			// On enlève le tuple de la lliste de tuples et on le retourne au Thread appelant
			tuples.remove(retour);
			mutexTakeWrite.release();
			read.release(nbRead);
			return retour;
		}


	}
	@Override
	public Tuple read(Tuple template) {
		try {
			read.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Tuple retour = null;
		// On cherche un tuple correspondant la la litse de tuples
		for (Tuple tuple : tuples){
			if (tuple.matches(template)){
				retour = tuple;
			}
		}
		// Si on n'a pas trouvé
		if (retour == null){
			
			Tuple templateMem = template.deepclone();
			
			try {
				mutexWaitRead.acquire();
				// On ajoute le template dans la liste d'attente
				waitRead.add(templateMem);
				mutexWaitRead.release(); 
				read.release(); 
				synchronized(templateMem){


					// On attend sur le template
					templateMem.wait();

				}
				read.acquire();
			} catch (InterruptedException e) {
				System.out.println("Possible sortie forcée du wait !");
				e.printStackTrace();
			}
			
				try {
					mutexWaitRead.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// On s'enlève de la liste d'attente
				waitRead.remove(templateMem);
				mutexWaitRead.release();
			read.release();
			// On va chercher le tuple correspondant
			return read(template);

		}
		else {
			read.release();
			// On retourne le tuple correspondant
			return retour.deepclone();
		}
	}
	@Override
	public Tuple tryTake(Tuple template) {
		try {
			
			mutexTakeWrite.acquire();
			read.acquire(nbRead);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Tuple retour = null;
		// On cherche un tuple correspondant
		for (Tuple tuple : tuples){
			if (tuple.matches(template)){
				retour = tuple;
			}
		}
		if (retour != null) {
			// Si on a trouvé un tuple on l'enlève de la liste
			tuples.remove(retour);
		}
		mutexTakeWrite.release();
		read.release(nbRead);
		// On retourne null ou le tuple  si on l'a trouvé
		return retour;

	}
	@Override
	public Tuple tryRead(Tuple template) {
		try {
			read.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Tuple retour = null;
		// On cherche un tuple correspondant
		for (Tuple tuple : tuples){
			if (tuple.matches(template)){
				retour = tuple;
			}
		}
		read.release();
		// On retourne null ou le tuple si on l'a trouvé
		return retour;
	}
	@Override
	public Collection<Tuple> takeAll(Tuple template) {
		try {
			mutexTakeWrite.acquire();
			read.acquire(nbRead);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		List<Tuple> retour = new ArrayList<Tuple>();
		// On cherche les tuples correspondant
		for (Tuple tuple : tuples){
			if (tuple.matches(template)){
				retour.add(tuple);
			}
		}
		// On enlève les tuples trouvés de la liste
		for (Tuple tuple : retour){
			tuples.remove(tuple);
		}
		mutexTakeWrite.release();
		read.release(nbRead);
		// On retourne les tuples trouvés
		return retour;
	}
	@Override
	public Collection<Tuple> readAll(Tuple template) {
		try {
			read.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<Tuple> retour = new ArrayList<Tuple>();
		// On cherche les tuples correspondants
		for (Tuple tuple : tuples){
			if (tuple.matches(template)){
				retour.add(tuple);
			}
		}
		read.release();		
		// On retourne les tuples trouvés
		return retour;
	}
	@Override
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
		if (timing.equals(eventTiming.IMMEDIATE)){
			// Si on est en immédiat on regarde si on trouve un tuple correspondant (tryRead et tryTake)
			if (mode.equals(eventMode.TAKE)){
				Tuple retour = tryTake(template);
				if (retour == null){
					// On a pas trouvé, on s'enregistre dans la liste des évènements
					try {
						mutexRegisteredEvents.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					registeredEvents.add(new Event(callback, mode, template));
					mutexRegisteredEvents.release();
				}
				else {
					// On a trouvé, on appelle le callback sur ce tuple
					callback.call(retour);
				}
			}
			else {

				Tuple retour = tryRead(template);
				if (retour == null){
					// On a pas trouvé on s'enregistre dans la liste des évènements
					try {
						mutexRegisteredEvents.acquire();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					registeredEvents.add(new Event(callback, mode, template));
					mutexRegisteredEvents.release();
				}
				else {
					// On a trouvé on appelle le callback sur ce tuple
					callback.call(retour);
				}
			}

		}
		else {
			// On mode Future on s'neregistre dans la liste des evènements
			try {
				mutexRegisteredEvents.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			registeredEvents.add(new Event(callback, mode, template));
			mutexRegisteredEvents.release();
		}
	}

	@Override
	public void debug(String prefix) {
		
		if (prefix.equals("(1)")){
			// Afficher la liste des tuples
			try {
				mutexTakeWrite.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print("(1) ");
			System.out.println(tuples.toString());
			mutexTakeWrite.release();
		}
		if (prefix.equals("(2)")){
			// Afficher la liste des évènements enregistrés
			try {
				mutexRegisteredEvents.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print("(2) ");
			System.out.println(registeredEvents.toString());
			mutexRegisteredEvents.release();
		}
		if (prefix.equals("(3)")){
			// Afficher la liste des read en attente
			System.out.print("(3) ");
			try {
				mutexWaitRead.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(waitRead.toString());
			mutexWaitRead.release();
		}
		if (prefix.equals("(4)")){
			// Afficher la liste des take en attente
			try {
				mutexWaitTake.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print("(4) ");
			System.out.println(waitTake.toString());
			mutexWaitTake.release();
		}

	}



}

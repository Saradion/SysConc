// Time-stamp: <08 Apr 2008 11:35 queinnec@enseeiht.fr>

import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import Synchro.Assert;

/** Lecteurs/rédacteurs
 * stratégie d'ordonnancement: priorité aux rédacteurs,
 * implantation: avec un moniteur. */
public class LectRed_PrioRedacteur implements LectRed
{
    private Lock getAccess;
    private int readQueue;
    private int writeQueue;
    private boolean readprot;
    private int reader;
    private Condition grantReadAccess;
    private Condition grantWriteAccess;

    public LectRed_PrioRedacteur()
    {
        getAccess = new ReentrantLock();
        readQueue = 0;
        writeQueue = 0;
        reader = 0;
        readprot = false;
        grantReadAccess = getAccess.newCondition();
        grantWriteAccess = getAccess.newCondition();
    }

    public void demanderLecture() throws InterruptedException
    {
        getAccess.lock();
        try {
            readQueue++;
            while (writeQueue != 0 || readprot) {
                grantReadAccess.await();
            }
            readQueue--;
            reader++;
        } finally {
            getAccess.unlock();
        }
    }

    public void terminerLecture() throws InterruptedException
    {
        getAccess.lock();
        try {
            reader--;
            if (writeQueue != 0 && reader == 0) {
                grantWriteAccess.signal();
            }
        } finally {
            getAccess.unlock();
        }
    }

    public void demanderEcriture() throws InterruptedException
    {
        getAccess.lock();
        try {
            writeQueue++;
            while (reader != 0 || readprot) {
                grantWriteAccess.await();
            }
            writeQueue--;
            readprot = true;
        } finally {
            getAccess.unlock();
        }
    }

    public void terminerEcriture() throws InterruptedException
    {
        getAccess.lock();
        try {
            readprot = false;
            if (writeQueue != 0) {
                grantWriteAccess.signal();
            } else if (readQueue != 0) {
                grantReadAccess.signalAll();
            }
        } finally {
            getAccess.unlock();
        }
    }

    public String nomStrategie()
    {
        return "Stratégie: Priorité Rédacteurs.";
    }
}

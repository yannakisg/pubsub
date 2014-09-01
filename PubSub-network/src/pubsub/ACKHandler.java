package pubsub;

import java.util.Comparator;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;
import pubsub.localrendezvous.LocRCClient;
import pubsub.localrendezvous.LocRCClientFactory;
import pubsub.messages.net.NetMessage;
import pubsub.util.StoppableThread;
import pubsub.util.ThreadSafeSortedList;

/**
 *
 * @author John Gasparis
 */
public class ACKHandler extends StoppableThread {

    private static final Logger logger = Logger.getLogger(ACKHandler.class);
    private final LocRCClient locRCClient;
    private static final long TIMEOUT = 8000;
    private static final long SLEEP = 2000;
    private final Lock lockObj = new ReentrantLock();
    private final Condition condVar = lockObj.newCondition();
    private ThreadSafeSortedList<Entry> tsSortedList;

    public ACKHandler() {
        locRCClient = LocRCClientFactory.createNewClient("ACKHandler");

        tsSortedList = new ThreadSafeSortedList<Entry>(new Comparator<Entry>() {

            @Override
            public int compare(Entry o1, Entry o2) {
                if (o1.getTimeout() > o2.getTimeout()) {
                    return 1;
                } else if (o2.getTimeout() > o1.getTimeout()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        this.setNamePrefix("ACKHandler");
    }

    @Override
    public void run() {
        int i;
        Entry entry;

        while (!isShutDown()) {

            lockObj.lock();
            try {
                while (tsSortedList.isEmpty()) {
                    condVar.await();
                }
            } catch (InterruptedException ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                lockObj.unlock();
            }

            for (i = 0; i < tsSortedList.getSize(); i++) {
                entry = tsSortedList.get(i);
                if ((entry.getTimeout() - SLEEP) <= 0) {
                    tsSortedList.remove(entry);
                    retransmit(entry);
                } else {
                    entry.setTimeout(entry.getTimeout() - SLEEP);
                }
            }

            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    private void retransmit(Entry entry) {
        logger.debug("Retransmit [" + entry.getID() + "]");
        entry.getMessage().publishMutableData(locRCClient, entry.getMessage().toBytes());
        
        if (entry.decreamentAndGet() > 0) {
            entry.setTimeout(TIMEOUT);
            tsSortedList.add(entry);
        }
    }

    public void addEntry(NetMessage message, long timeout) {
        Entry entry = new Entry(message, timeout);
        tsSortedList.add(entry);
        lockObj.lock();
        try {
            condVar.signal();
        } finally {
            lockObj.unlock();
        }
    }

    public void addEntry(NetMessage message) {
        addEntry(message, TIMEOUT);
    }

    public void removeEntry(int id) {
        for (int i = 0; i < tsSortedList.getSize(); i++) {
            if (tsSortedList.get(i).getID() == id) {
                tsSortedList.remove(i);
                break;
            }
        }
    }

    private class Entry {
        private int maxAttempts = 30;
        private long timeout;
        private NetMessage message;

        public Entry(NetMessage message, long timeout) {
            this.message = message;
            this.timeout = timeout;
        }

        public NetMessage getMessage() {
            return this.message;
        }

        public int getID() {
            return message.getID();
        }

        public long getTimeout() {
            return this.timeout;
        }
        
        public int decreamentAndGet() {
            return --maxAttempts;
        }

        public void setTimeout(long timeout) {
            logger.debug("New timeout [" + message.getID() + "] => " + timeout);
            this.timeout = timeout;
        }
    }
}

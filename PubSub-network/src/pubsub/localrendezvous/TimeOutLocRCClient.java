package pubsub.localrendezvous;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import pubsub.Publication;
import pubsub.Subscription;
import pubsub.util.Consumer;
import pubsub.util.ProducerConsumerQueue;
import pubsub.util.StoppableThread;

/**
 *
 * @author tsilo
 */
public class TimeOutLocRCClient {

    public static enum Mode {

        DEEP, SHALLOW;
    }
    private static final Logger logger = Logger.getLogger(TimeOutLocRCClient.class);
    private final LocRCClient locRC;
    private final Map<Subscription, ProducerConsumerQueue<Publication>> buffers;
    private final ReceiverThread recThread;
    private final Mode mode;

    public TimeOutLocRCClient(LocRCClient locRC, Mode m) {
        this.locRC = locRC;
        buffers = new HashMap<Subscription, ProducerConsumerQueue<Publication>>();
        recThread = new ReceiverThread();
        this.mode = m;

        String prefix = locRC.getName();
        prefix = prefix + "/" + this.getClass().getSimpleName();
        recThread.setNamePrefix(prefix);

        recThread.start();
    }

    public void publish(Publication p) {
        this.locRC.publish(p);
    }

    /**
     * Blocking subscribe
     *
     * @param s
     * @throws InterruptedException
     */
    public Publication subscribe(Subscription s) throws InterruptedException {
        Consumer<Publication> c = subscribeNonBlock(s);
        return c.take();
    }

    /**
     * Subscribe with timeout
     *
     * @param s
     * @throws InterruptedException
     */
    public Publication subscribe(Subscription s, long timeout, TimeUnit unit)
            throws InterruptedException {
        Consumer<Publication> c = subscribeNonBlock(s);
        return c.poll(timeout, unit);
    }

    public Consumer<Publication> subscribeNonBlock(Subscription s) {
        Consumer<Publication> c;
        synchronized (buffers) {
            if (!subscribedTo(s)) {
                this.locRC.subscribe(s);
                trackSubscription(s);
            }
            c = getConsumer(s);
        }
        return c;
    }

    private Consumer<Publication> getConsumer(Subscription s) {
        return buffers.get(s).getConsumer();
    }

    public void unsubscribe(Subscription s) {
        synchronized (buffers) {
            buffers.remove(s);
        }
        this.locRC.unsubscribe(s);
    }

    private boolean subscribedTo(Subscription s) {
        return this.buffers.get(s) != null;
    }

    private void trackSubscription(Subscription s) {
        ProducerConsumerQueue<Publication> queue = buffers.get(s);
        if (queue == null) {
            queue = ProducerConsumerQueue.createNew();
            this.buffers.put(s, queue);
        }
    }

    public void close() {
        if (!recThread.isShutDown()) {
            recThread.shutDown();
            recThread.interrupt();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.close();
        } finally {
            super.finalize();
        }
    }

    private void addPublication(Publication p) throws InterruptedException {
        Subscription s = Subscription.fromPublication(p);
        ProducerConsumerQueue<Publication> queue = this.buffers.get(s);
        if (queue == null) {
            logger.debug("received non subscribed publication");
            return;
        }
        queue.getProduder().put(p);
    }

    private class ReceiverThread extends StoppableThread {

        @Override
        public void run() {
            Publication emptyPub = Publication.createEmpty();
            while (!isShutDown()) {
                try {
                    Publication p = locRC.receiveNext();
                    if (p != null) {
                        if (p.equals(emptyPub)) {
                            break;
                        } else {
                            addPublication(p);
                        }
                    }
                } catch (InterruptedException e) {
                    if (!isShutDown()) {
                        logger.debug(e);
                    }
                }
            }
            logger.debug("receiving thread shut down gracefully");
        }

        @Override
        public void shutDown() {
            super.shutDown();
            if (mode.equals(Mode.DEEP)) {
                try {
                    locRC.close();
                } catch (Exception e) {
                    logger.debug(e);
                }
            }
        }
    }
}

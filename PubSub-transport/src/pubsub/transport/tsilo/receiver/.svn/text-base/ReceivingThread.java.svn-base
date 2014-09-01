package pubsub.transport.tsilo.receiver;

import org.apache.log4j.Logger;

import pubsub.Publication;
import pubsub.util.Consumer;
import pubsub.util.Pair;
import pubsub.util.Producer;
import pubsub.util.StoppableThread;

public class ReceivingThread extends StoppableThread {

    private static final Logger logger = Logger.getLogger(ReceivingThread.class);
    private Consumer<Publication> queue;
    private Producer<Pair<Publication, Long>> outgoingQueue;

    public ReceivingThread(Consumer<Publication> incomingDataQueue, Producer<Pair<Publication, Long>> outgoingQueue) {
        this.queue = incomingDataQueue;
        this.outgoingQueue = outgoingQueue;
    }

    @Override
    public void run() {
        while (!isShutDown()) {
            try {
                Publication publication = queue.take();
                outgoingQueue.offer(new Pair<Publication, Long>(publication, System.currentTimeMillis()));
            } catch (InterruptedException e) {
                if (!isShutDown()) {
                    logger.debug("Receiving thread was interrupted abnormally");
                }
            }
        }
    }
}

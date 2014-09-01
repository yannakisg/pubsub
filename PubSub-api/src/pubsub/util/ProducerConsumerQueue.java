package pubsub.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author tsilo
 */
public class ProducerConsumerQueue<T> {

    private final Producer<T> produder;
    private final Consumer<T> consumer;
    private final BlockingQueue<T> q;

    public ProducerConsumerQueue() {
        q = new LinkedBlockingQueue<T>();
        produder = new Producer<T>(q);
        consumer = new Consumer<T>(q);
    }

    public Producer<T> getProduder() {
        return produder;
    }

    public Consumer<T> getConsumer() {
        return consumer;
    }

    public static <T> ProducerConsumerQueue<T> createNew() {
        return new ProducerConsumerQueue<T>();
    }

    public int getSize() {
        return q.size();
    }

    public void clear() {
        q.clear();

    }
}

package pubsub.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author tsilo
 */
public class Producer<T> {

    private final BlockingQueue<T> queue;

    public Producer(BlockingQueue<T> q) {
        queue = q;
    }

    public void put(T item) throws InterruptedException {
        queue.put(item);
    }

    public boolean offer(T item) {
        return queue.offer(item);
    }

    public boolean offerT(T item, long timeout, TimeUnit unit) throws InterruptedException {
        return queue.offer(item, timeout, unit);
    }
}

package pubsub.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author tsilo
 */
public class Consumer<T> {

    private final BlockingQueue<T> queue;

    public Consumer(BlockingQueue<T> q) {
        queue = q;
    }

    public T take() throws InterruptedException {
        T val = queue.take();
        return val;
    }

    public T poll() {
        return queue.poll();
    }

    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        T val = queue.poll(timeout, unit);
        return val;
    }
}

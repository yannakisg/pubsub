package pubsub.tmc;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author John Gasparis
 */
public class Atomic {

    private static AtomicInteger integer = new AtomicInteger(0);

    public static void increase() {
        integer.incrementAndGet();
    }

    public static void decrease() {
        integer.decrementAndGet();
    }

    public static int getValue() {
        return integer.get();
    }
}

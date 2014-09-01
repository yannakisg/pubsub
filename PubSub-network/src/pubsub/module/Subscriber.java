package pubsub.module;

import pubsub.Publication;

/**
 *
 * @author tsilo
 */
public interface Subscriber {

    void deliver(Publication publication);

    public String getIdentity();
}

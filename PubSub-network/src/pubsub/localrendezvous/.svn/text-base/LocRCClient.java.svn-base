package pubsub.localrendezvous;

import java.io.IOException;

import pubsub.Publication;
import pubsub.Subscription;

/**
 *
 * @author tsilo
 * @author John Gasparis
 */
public interface LocRCClient {

    public void publish(Publication p);

    public void subscribe(Subscription s);

    public void unsubscribe(Subscription s);

    public boolean communicationWorks();

    public Publication receiveNext() throws InterruptedException;

    public void close() throws IOException;

    public String getName();
}

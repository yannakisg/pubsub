package pubsub.module;

import pubsub.Publication;
import pubsub.Subscription;

/**
 *
 * @author tsilo
 */
public interface PubSubModule {

    public void publish(Publication publication);

    public void subscribe(Subscription subscription, Subscriber subscriber);

    public void unsubscribe(Subscription subscription, Subscriber subscriber);

    public void removeSubscriber(Subscriber s);
}

package pubsub.module;

import java.util.Set;

import pubsub.Publication;
import pubsub.Subscription;

/**
 *
 * @author tsilo
 */
public interface SubscriptionTable {

    void storeSubscription(Subscription subscription, Subscriber subscriber);

    void removeSubscription(Subscription subscription, Subscriber subscriber);

    Set<Subscriber> matchSubcriptions(Publication publication);

    void removeSubscriber(Subscriber s);
}

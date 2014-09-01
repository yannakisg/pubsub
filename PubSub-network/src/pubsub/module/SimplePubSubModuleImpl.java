package pubsub.module;

import java.util.Set;


import pubsub.Publication;
import pubsub.Subscription;

/**
 *
 * @author tsilo
 */
public class SimplePubSubModuleImpl implements PubSubModule {
    private final SubscriptionTable subscriptionTable;

    public SimplePubSubModuleImpl(SubscriptionTable subscriptionTable) {
        this.subscriptionTable = subscriptionTable;
    }

    @Override
    public void publish(Publication publication) {
        Set<Subscriber> subscribers = subscriptionTable.matchSubcriptions(publication);
        for (Subscriber subscriber : subscribers) {
            subscriber.deliver(publication);
        }
    }

    @Override
    public void subscribe(Subscription subscription, Subscriber subscriber) {
        subscriptionTable.storeSubscription(subscription, subscriber);
    }

    @Override
    public void unsubscribe(Subscription subscription, Subscriber subscriber) {
        subscriptionTable.removeSubscription(subscription, subscriber);
    }

    @Override
    public void removeSubscriber(Subscriber s) {
        subscriptionTable.removeSubscriber(s);

    }
}

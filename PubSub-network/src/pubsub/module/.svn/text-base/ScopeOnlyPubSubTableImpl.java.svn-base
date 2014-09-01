package pubsub.module;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pubsub.PubSubID;
import pubsub.Publication;
import pubsub.Subscription;

/**
 *
 * @author tsilo
 */
public class ScopeOnlyPubSubTableImpl implements SubscriptionTable {

    private final Map<PubSubID, Set<Subscriber>> subscriptionTable = new HashMap<PubSubID, Set<Subscriber>>();

    @Override
    public Set<Subscriber> matchSubcriptions(Publication publication) {
        Set<Subscriber> set = new HashSet<Subscriber>();
        PubSubID key = publication.getRendezvousId();

        synchronized (subscriptionTable) {
            Set<Subscriber> subs = subscriptionTable.get(key);
            if (subs != null && !subs.isEmpty()) {
                synchronized (subs) {
                    set.addAll(subs);
                }
            }
        }
        return set;
    }

    @Override
    public void removeSubscription(Subscription subscription,
            Subscriber subscriber) {
        PubSubID key = subscription.getRendezvousId();
        synchronized (subscriptionTable) {
            Set<Subscriber> subscriberSet = subscriptionTable.get(key);
            if (subscriberSet != null) {
                synchronized (subscriberSet) {
                    subscriberSet.remove(subscriber);
                    if (subscriberSet.isEmpty()) {
                        subscriptionTable.remove(subscriberSet);
                    }
                }
            }
        }
    }

    @Override
    public void storeSubscription(Subscription subscription,
            Subscriber subscriber) {
        PubSubID key = subscription.getScopeId();
        synchronized (subscriptionTable) {
            Set<Subscriber> subscribers = subscriptionTable.get(key);
            if (subscribers == null) {
                subscribers = new HashSet<Subscriber>();
                subscriptionTable.put(key, subscribers);

                synchronized (subscribers) {
                    subscribers.add(subscriber);
                }
            }
        }
    }

    @Override
    public void removeSubscriber(Subscriber s) {
        synchronized (subscriptionTable) {
            for (PubSubID key : subscriptionTable.keySet()) {
                Set<Subscriber> k = subscriptionTable.get(key);
                k.remove(s);
            }
        }

    }
}

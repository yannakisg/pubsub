package pubsub.module;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import pubsub.Publication;
import pubsub.Subscription;

/**
 *
 * @author tsilo
 * @author John Gasparis
 */
public class ScopeRendezvousIdTable implements SubscriptionTable {

    
    private final Map<Subscription, SubscriberSet> subscriptionTable = new HashMap<Subscription, SubscriberSet>();    

    @Override
    public void storeSubscription(Subscription subscription,
            Subscriber subscriber) {
    	SubscriberSet subscriberSet = null;
    	synchronized (subscriptionTable) {
    		subscriberSet = this.subscriptionTable.get(subscription);
    		if(subscriberSet == null){
    			subscriberSet = new SubscriberSet();
    			this.subscriptionTable.put(subscription, subscriberSet);
    		}
		}
    	
    	subscriberSet.store(subscriber);        
    }

    @Override
    public void removeSubscription(Subscription subscription,
            Subscriber subscriber) {    	
    	SubscriberSet subscriberSet = null;
    	synchronized (subscriptionTable) {
    		subscriberSet = this.subscriptionTable.get(subscription);    		
		}
    	if(subscriberSet!= null){
    		subscriberSet.remove(subscriber);
    	}   	    	        
    }

    @Override
    public Set<Subscriber> matchSubcriptions(Publication publication) {
    	Set<Subscriber> retval = new HashSet<Subscriber>();
    	Subscription sub = Subscription.fromPublication(publication);
    	
    	SubscriberSet subscriberSet = null;
    	synchronized (subscriptionTable) {
    		subscriberSet = this.subscriptionTable.get(sub);    		
		}
    	
    	if(subscriberSet !=null){
    		subscriberSet.getAll(retval);
    	}
    	
    	return retval;
        
    }

    @Override
    public void removeSubscriber(Subscriber s) {
    	synchronized (subscriptionTable) {
    		Iterator<Subscription> iterator = this.subscriptionTable.keySet().iterator();
    		while(iterator.hasNext()){
    			Subscription next = iterator.next();
    			SubscriberSet subscriberSet = this.subscriptionTable.get(next);    			
    			subscriberSet.remove(s);
    			if(subscriberSet.isEmpty()){
    				iterator.remove();
    			}
    		}    		
		}       
    }
}

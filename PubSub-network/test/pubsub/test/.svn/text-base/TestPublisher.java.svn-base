package pubsub.test;

import java.util.Arrays;

import pubsub.PubSubID;
import pubsub.Publication;
import pubsub.localrendezvous.LocRCClient;
import pubsub.localrendezvous.LocRCClientFactory;

public class TestPublisher {
	public static void main(String[] args) throws InterruptedException {
		byte [] scopeD = new byte[PubSubID.ID_LENGTH];
		byte [] rvsD = new byte[PubSubID.ID_LENGTH];
		
		Arrays.fill(scopeD, (byte)0);
		Arrays.fill(rvsD, (byte)1);
		
		PubSubID scope = new PubSubID(scopeD);
		PubSubID rid = new PubSubID(rvsD);
		
		byte [] data = new byte[10000];
		Arrays.fill(data, (byte)5);
		
		LocRCClient client = LocRCClientFactory.createNewClient("testPublisher");
		System.out.println("publishing");
		client.publish(Publication.createMutableData(scope, rid, data));
		
		Thread.sleep(5000);
	}

}

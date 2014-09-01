package pubsub.test;

import java.util.Arrays;

import pubsub.PubSubID;
import pubsub.Publication;
import pubsub.Subscription;
import pubsub.localrendezvous.LocRCClient;
import pubsub.localrendezvous.LocRCClientFactory;

public class TestSubscriber {
	public static void main(String[] args) throws InterruptedException {
		byte [] scopeD = new byte[PubSubID.ID_LENGTH];
		byte [] rvsD = new byte[PubSubID.ID_LENGTH];
		
		Arrays.fill(scopeD, (byte)0);
		Arrays.fill(rvsD, (byte)1);
		
		PubSubID scope = new PubSubID(scopeD);
		PubSubID rid = new PubSubID(rvsD);
		
		byte [] data = new byte[10000];
		Arrays.fill(data, (byte)5);
		
		LocRCClient client = LocRCClientFactory.createNewClient("localhost", 10000, "testSubscriber");
		System.out.println("subscribing");
		client.subscribe(Subscription.createSubToMutableData(scope, rid));
		while(true){
			Publication p = client.receiveNext();
			System.out.printf("data len %d\n", p.getDataLength());
			for (byte b : p.getDataArray()) {
				System.out.print(b+" ");
			}
			System.out.println();
		}
	}

}

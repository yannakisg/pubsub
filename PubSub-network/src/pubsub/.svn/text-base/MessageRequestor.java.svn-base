package pubsub;

import pubsub.localrendezvous.TimeOutLocRCClient;
import pubsub.util.Consumer;

/**
 *
 * @author tsilo
 */
public class MessageRequestor {

    private final TimeOutLocRCClient lClient;

    public MessageRequestor(TimeOutLocRCClient lClient) {
        this.lClient = lClient;
    }

    public byte[] request(Subscription serverSub, byte[] processRequest) throws InterruptedException {
        PubSubID tmpScope = PubSubID.createRandom();
        PubSubID tmpRendezvous = PubSubID.createRandom();
        Subscription sub = Subscription.createSubToMutableData(tmpScope, tmpRendezvous);

        Publication p = Publication.createMutableData(tmpScope, tmpRendezvous, processRequest);
        Publication request = Publication.createMutableData(serverSub.getScopeId(), serverSub.getRendezvousId(), p.toBytes());

        Consumer<Publication> buffer = this.lClient.subscribeNonBlock(sub);
        this.lClient.publish(request);
        Publication response = buffer.take();
        this.lClient.unsubscribe(sub);

        return response.getDataArray();
    }
}

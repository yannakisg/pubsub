package pubsub.forwarding;

import java.nio.ByteBuffer;

import pubsub.ForwardIdentifier;
import pubsub.MessageRequestor;
import pubsub.PubSubID;
import pubsub.Publication;
import pubsub.Subscription;
import pubsub.bloomfilter.BloomFilter;
import pubsub.invariants.WellKnownIds;
import pubsub.localrendezvous.LocRCClient;
import pubsub.localrendezvous.TimeOutLocRCClient;

/**
 *
 * @author tsilo
 */
public class FwdUtils {

    private static final PubSubID scopeId = PubSubID.fromHexString(
            WellKnownIds.Fwd.FWD_SID);
    private static final PubSubID rendezvousId = PubSubID.fromHexString(
            WellKnownIds.Fwd.FWD_RID);

    /**
     * Publishes to the forwarding component via the LocRCClient provided
     * @param locrc
     * @param fid
     * @param pub
     */
    public static void publishToFwd(ForwardIdentifier fid, Publication pub, LocRCClient locRC) {
        FwdStruct fPub = new FwdStruct(fid, pub);
        Publication enc = Publication.createMutableData(scopeId, rendezvousId, fPub.toBytes());
        locRC.publish(enc);
    }

    public static void publishToFwd(ForwardIdentifier fid, Publication pub, TimeOutLocRCClient locRC) {
        FwdStruct fPub = new FwdStruct(fid, pub);
        Publication enc = Publication.createMutableData(scopeId, rendezvousId, fPub.toBytes());
        locRC.publish(enc);
    }

    public static BloomFilter getFwdVLID(TimeOutLocRCClient client) throws InterruptedException {
        MessageRequestor requestor = new MessageRequestor(client);
        Subscription sub = Subscription.createSubToMutableData(FwdComponent.FWD_SID, FwdComponent.FWD_INFO);

        byte[] answer = requestor.request(sub, FwdInfoHandler.REQUEST_VLID);
        return new BloomFilter(answer);
    }

    public static int getMTU(TimeOutLocRCClient client) throws InterruptedException {
        MessageRequestor requestor = new MessageRequestor(client);
        Subscription sub = Subscription.createSubToMutableData(FwdComponent.FWD_SID, FwdComponent.FWD_INFO);

        byte[] answer = requestor.request(sub, FwdInfoHandler.REQUEST_MTU);
        return ByteBuffer.wrap(answer).getInt();
    }
}

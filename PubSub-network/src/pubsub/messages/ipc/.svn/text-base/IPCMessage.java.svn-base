package pubsub.messages.ipc;

import pubsub.PubSubID;
import pubsub.Publication;
import pubsub.localrendezvous.LocRCClient;
import pubsub.localrendezvous.TimeOutLocRCClient;
import pubsub.messages.Message;
import pubsub.messages.MessageType;

/**
 *
 * @author John Gasparis
 */
public abstract class IPCMessage extends Message {

    private PubSubID sid;
    private PubSubID rid;

    public IPCMessage(MessageType.Type msgType, PubSubID sid, PubSubID rid) {
        super(msgType);
        this.sid = sid;
        this.rid = rid;
    }

    public PubSubID getScopeID() {
        return this.sid;
    }

    public PubSubID getRendezvousID() {
        return this.rid;
    }

    @Override
    public void publishMutableData(LocRCClient locRCClient, byte[] data) {
        Publication pub = Publication.createMutableData(sid, rid, data);
        locRCClient.publish(pub);
    }
    
    public void publishMutableData(TimeOutLocRCClient locRCClient, byte[] data) {
        Publication pub = Publication.createMutableData(sid, rid, data);
        locRCClient.publish(pub);
    }
}

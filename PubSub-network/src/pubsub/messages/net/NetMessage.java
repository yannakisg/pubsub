package pubsub.messages.net;

import java.nio.ByteBuffer;
import pubsub.ContentType;
import pubsub.ForwardIdentifier;
import pubsub.PubSubID;
import pubsub.Publication;
import pubsub.forwarding.FwdUtils;
import pubsub.localrendezvous.LocRCClient;
import pubsub.localrendezvous.TimeOutLocRCClient;
import pubsub.messages.Message;
import pubsub.messages.MessageType;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public abstract class NetMessage extends Message {

    private PubSubID sid;
    private PubSubID rid;
    private ForwardIdentifier fid;
    private int id;
    private static int LENGTH = -1;

    public NetMessage(MessageType.Type msgType, PubSubID sid, PubSubID rid, ForwardIdentifier fid) {
        super(msgType);

        this.sid = sid;
        this.rid = rid;
        this.fid = fid;
        this.id = Util.getRandomInteger();
    }

    protected NetMessage(MessageType.Type msgType, PubSubID sid, PubSubID rid) {
        super(msgType);

        this.sid = sid;
        this.rid = rid;
        this.id = Util.getRandomInteger();
    }

    public void setScopeID(PubSubID sid) {
        this.sid = sid;
    }

    public void setRendezvousID(PubSubID rid) {
        this.rid = rid;
    }

    public PubSubID getScopeID() {
        return this.sid;
    }

    public PubSubID getRendezvousID() {
        return this.rid;
    }

    public void setFID(ForwardIdentifier fid) {
        this.fid = fid;
    }
    
    public void computeNewID() {
        this.id = Util.getRandomInteger();
    }

    public int getID() {
        return this.id;
    }
    
    public static int findID(ByteBuffer buffer) {
        return (buffer.getInt(buffer.position() + Util.SIZEOF_BYTE));
    }

    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = super.getSerializedLength() + Util.SIZEOF_INT;
        }
        return LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        buff.putInt(id);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);
        id = buff.getInt();
    }

    public void publish(LocRCClient locRCClient, ContentType cType, byte[] data) {
        Publication pub = Publication.createPublication(sid, rid, cType, data);
        FwdUtils.publishToFwd(fid, pub, locRCClient);
    }

    public void publish(TimeOutLocRCClient locRCClient, ContentType cType, byte[] data) {
        Publication pub = Publication.createPublication(sid, rid, cType, data);
        FwdUtils.publishToFwd(fid, pub, locRCClient);
    }

    public void publishImmutableData(LocRCClient locRCClient, byte[] data) {
        Publication pub = Publication.createImmutableData(sid, rid, data);
        FwdUtils.publishToFwd(fid, pub, locRCClient);
    }

    public void publishImmutableData(TimeOutLocRCClient locRCClient, byte[] data) {
        Publication pub = Publication.createImmutableData(sid, rid, data);
        FwdUtils.publishToFwd(fid, pub, locRCClient);
    }

    @Override
    public void publishMutableData(LocRCClient locRCClient, byte[] data) {
        Publication pub = Publication.createMutableData(sid, rid, data);
        FwdUtils.publishToFwd(fid, pub, locRCClient);
    }

    public void publishMutableData(TimeOutLocRCClient locRCClient, byte[] data) {
        Publication pub = Publication.createMutableData(sid, rid, data);
        FwdUtils.publishToFwd(fid, pub, locRCClient);
    }
}

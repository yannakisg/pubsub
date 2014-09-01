package pubsub;

import java.nio.ByteBuffer;
import pubsub.util.Util;

/**
 *
 * @author tsilo
 * @author John Gasparis
 */
public class Subscription extends BasePubSubPacket {

    private static final int LENGTH = PubSubID.ID_LENGTH * 2 + Util.SIZEOF_BYTE;
    
    private Subscription() {
        this.scopeId = null;
        this.rendezvousId = null;
        this.cType = ContentType.LOCAL;
    }

    private Subscription(PubSubID scopeId, PubSubID rId, ContentType type) {
        setScopeId(scopeId);
        setRendezvousId(rId);
        setContentType(type);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public int getSerializedLength() {
        return LENGTH;
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        cType = ContentType.getType(buff.get());
        scopeId = PubSubID.parseByteBuffer(buff);
        rendezvousId = PubSubID.parseByteBuffer(buff);
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        buff.put(cType.byteValue());
        scopeId.writeTo(buff);
        rendezvousId.writeTo(buff);
    }

    public static Subscription parseByteBuffer(ByteBuffer buffer) {
        Subscription subscription = new Subscription();
        subscription.readBuffer(buffer);
        return subscription;
    }

    public static Subscription parseByteArray(byte[] data) {
        Subscription subscription = new Subscription();
        subscription.fromBytes(data);
        return subscription;
    }

    @Override
    public String toString() {
        return this.scopeId.toString() + "/" + this.rendezvousId.toString() + "/" + this.cType.toString();
    }

    public static Subscription createRequestToImmutableData(PubSubID sid, PubSubID rid) {
        return new Subscription(sid, rid, ContentType.REQUEST_IMMUTABLE_DATA);
    }

    public static Subscription createSubToImmutableData(PubSubID sid, PubSubID rid) {
        return new Subscription(sid, rid, ContentType.IMMUTABLE_DATA);
    }

    public static Subscription createSubToMutableData(PubSubID sid, PubSubID rid) {
        return new Subscription(sid, rid, ContentType.MUTABLE_DATA);
    }

    public static Subscription createSubToChannel(PubSubID sid, PubSubID rid) {
        return new Subscription(sid, rid, ContentType.CHANNEL);
    }

    public static Subscription createSubToDocument(PubSubID sid, PubSubID rid) {
        return new Subscription(sid, rid, ContentType.DOCUMENT);
    }

    public static Subscription createSubscription(PubSubID sid, PubSubID rid, ContentType cType) {
        return new Subscription(sid, rid, cType);
    }

    public static Subscription fromPublication(Publication pub) {
        return new Subscription(pub.getScopeId(), pub.getRendezvousId(), pub.getContentType());
    }
}

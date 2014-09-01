package pubsub.transport.channel.sink;

import java.nio.ByteBuffer;
import org.apache.log4j.Logger;
import pubsub.ByteIdentifier;
import pubsub.PubSubID;
import pubsub.Publication;
import pubsub.Subscription;
import pubsub.messages.RVAAnnouncement;
import pubsub.messages.net.transport.ChannelPacketMessage;
import pubsub.rva.RVS;
import pubsub.transport.Sink;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class ChannelSink extends Sink {

    private Logger logger;
    private boolean isClosed;
    private Subscription subscription;
    private long announceTime;
    private boolean isFirst = true;
    private int totalPackets = 0;
    private int outOfOrder = 0;
    private int seqNum = -1;
    private ByteIdentifier procID;

    public ChannelSink(String chName) {
        super();
        isClosed = false;
        setName(chName);

        ByteBuffer buffer = ByteBuffer.allocate(Util.SIZEOF_INT + Util.SIZEOF_INT);
        buffer.putInt(Util.getPID());
        buffer.putInt(Util.getRandomInteger());

        this.procID = new ByteIdentifier(Util.sha256toBytes(buffer.array()));
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void announceSubscription(PubSubID sid, PubSubID rid, long lifeTime) {
        this.announceTime = System.currentTimeMillis();
        subscription = Subscription.createSubToChannel(sid, rid);

        RVS.subscribe(subscription, RVAAnnouncement.RVPAction.DEFAULT, lifeTime, procID, locRCClient);
        locRCClient.subscribe(subscription);
    }

    public void announceUnSubscription(PubSubID sid, PubSubID rid) {
        try {
            logger.debug("Statistics: totalPackets [" + totalPackets + "], outOfOrder[" + outOfOrder + "], received correctly[" + (totalPackets - outOfOrder) + "]");
        } catch (java.lang.OutOfMemoryError ex) {
            System.out.println("Statistics: totalPackets [" + totalPackets + "], outOfOrder[" + outOfOrder + "], received correctly[" + (totalPackets - outOfOrder) + "]");
        }
        Subscription sub = Subscription.createSubToChannel(sid, rid);
        RVS.unsubscribe(sub, RVAAnnouncement.RVPAction.DEFAULT, procID, locRCClient);
        locRCClient.unsubscribe(sub);
    }

    @Override
    protected void processPublication(Publication pub) {
        ChannelPacketMessage message = ChannelPacketMessage.parseByteBuffer(pub.wrapData());
        if (isFirst) {
            isFirst = false;
            logger.debug("Flow establishment: " + (System.currentTimeMillis() - announceTime));
        }
        try {
            logger.debug("[SEQ] => " + message.getSEQ());
        } catch (java.lang.OutOfMemoryError ex) {
        }

        totalPackets++;
        if (message.getSEQ() > seqNum) {
            int diff = message.getSEQ() - seqNum;
            if (diff > 1) {
                outOfOrder += diff - 1;
            }
            seqNum = message.getSEQ();
        }
    }

    public void close() {
        if (!isClosed) {
            isClosed = true;
            this.shutDown();
            this.interrupt();
        }
    }
}

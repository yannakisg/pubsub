package pubsub.transport.api.channel;

import org.apache.log4j.Logger;
import pubsub.PubSubID;
import pubsub.messages.net.transport.ChannelPacketInfo;
import pubsub.transport.channel.sink.ChannelSink;
import pubsub.util.ProducerConsumerQueue;

/**
 *
 * @author John Gasparis
 */
public abstract class ChannelSinkAPI {

    private ChannelSink sink = null;

    public ChannelSinkAPI(String chName) {
        if (sink == null) {
            sink = new ChannelSink(chName);
        }
    }
    
    protected void setLogger(Logger logger) {
        sink.setLogger(logger);
    }

    protected void announceSubscription(PubSubID sid, PubSubID rid, long lifeTime) {
        sink.announceSubscription(sid, rid, lifeTime);
    }
    
    protected void announceUnSubscription(PubSubID sid, PubSubID rid) {
        sink.announceUnSubscription(sid, rid);
    }

    protected void startAll() {
        sink.start();
    }
    
    protected void stopAll() {
        sink.close();
    }
}

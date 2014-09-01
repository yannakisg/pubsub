package pubsub.transport.api.channel;

import java.io.IOException;
import pubsub.PubSubID;
import pubsub.transport.channel.source.ChannelSource;

/**
 *
 * @author John Gasparis
 */
public abstract class ChannelSourceAPI /*implements ChannelSourceListener*/ {

    private ChannelSource source = null;

    public ChannelSourceAPI(ChannelSource.ChannelType chType, String chName, long bitRate, int chunkSize) {
        if (source == null) {
            source = new ChannelSource(chType, chName, bitRate, chunkSize);
        }
    }

    protected void announcePublication(PubSubID sid, PubSubID rid, long lifeTime) {
        source.announcePublication(sid, rid, lifeTime);
    }

    protected void startAll() {
        source.start();
        //source.startAll();
    }
    
    protected void stopAll() throws IOException {
        source.close();
    }

  /*  protected void addListener(ChannelSourceListener listener) {
        source.setListener(listener);
    }

   /* protected void enqueue(PacketInfoEntry packetInfo) {
        source.enqueue(packetInfo);
    }*/
}

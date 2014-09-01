package pubsub.experiments.channel.publisher;

import java.security.NoSuchAlgorithmException;
import org.apache.log4j.Logger;
import pubsub.PubSubID;
import pubsub.transport.api.channel.ChannelSourceAPI;
import pubsub.transport.channel.source.ChannelSource;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class Publisher extends ChannelSourceAPI {

    private Logger logger;
    private String channelName;
    private long lifeTime;
    private PubSubID sid;
    private PubSubID rid;
    private boolean isClosed;

    public Publisher(String channelName, ChannelSource.ChannelType chType, long bitRate, long lifeTime, int chunkSize) {
        super(chType, channelName, bitRate, chunkSize);

        this.channelName = channelName;
        this.lifeTime = lifeTime;
        this.logger = Logger.getLogger(channelName);
        this.isClosed = false;
    }

    public void announce() {
        computePubSubIDs();

        super.announcePublication(sid, rid, lifeTime);
        super.startAll();
    }

    private void computePubSubIDs() {
        try {
            sid = Util.sha256toPubSubId(channelName.getBytes());
            rid = Util.sha256toPubSubId(sid.getId());

          //  logger.debug("ChannelName : " + channelName + "\nsid : " + sid.toString() + "\nrid : " + rid.toString());
        } catch (NoSuchAlgorithmException ex) {
        }
    }
}

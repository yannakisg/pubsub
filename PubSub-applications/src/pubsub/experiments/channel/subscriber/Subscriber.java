package pubsub.experiments.channel.subscriber;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import pubsub.PubSubID;
import pubsub.transport.api.channel.ChannelSinkAPI;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class Subscriber extends ChannelSinkAPI {

    private final Logger logger = Logger.getLogger(Subscriber.class);
    private String channelName;
    private int duration;
    private boolean isClosed;
    private PubSubID sid;
    private PubSubID rid;
    private static boolean hasAdded = false;

    public Subscriber(String channelName, int duration, int count) {
        super(channelName);

        count++;
        this.channelName = channelName;
        this.duration = duration;
        this.isClosed = false;

        if (!hasAdded) {
            try {

                logger.addAppender(new FileAppender(new PatternLayout("%d [%t] %-5p %c - %m%n"), "subscriber" + count + ".log", false));
                hasAdded = true;

            } catch (IOException ex) {
                hasAdded = false;
                java.util.logging.Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        setLogger(logger);
    }

    public void announce() {
        computePubSubIDs();

        announceSubscription(sid, rid, duration * 1000);
        logger.debug("Announce subscription at: " + System.currentTimeMillis());
        startAll();
    }

    private void computePubSubIDs() {
        try {
            sid = Util.sha256toPubSubId(channelName.getBytes());
            rid = Util.sha256toPubSubId(sid.getId());
            // logger.debug("Name : " + channelName + "\nsid : " + sid.toString() + "\nrid : " + rid.toString());
        } catch (NoSuchAlgorithmException ex) {
        }
    }

    public void unsubscribe() {
        logger.debug("Announce Unsubscription");
        announceUnSubscription(sid, rid);
    }

    public void close() {
        if (!isClosed) {
            super.stopAll();
            isClosed = true;
        }
    }

    @Override
    public void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }
}

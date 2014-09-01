package pubsub.experiments.document.subscriber;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import org.apache.log4j.Logger;
import pubsub.PubSubID;
import pubsub.transport.api.document.DocumentEntryEvent;
import pubsub.transport.api.document.DocumentSinkAPI;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class Subscriber extends DocumentSinkAPI {

    private PubSubID sid;
    private PubSubID rid;
    private long lifeTime;
    private final Logger logger;
    
    public Subscriber(Logger logger, String name, long lifeTime) {
        super(new File(name));
        
        this.logger = logger;
        this.lifeTime = lifeTime;
        
        computePubSubIDs(name);        
    }

    private void computePubSubIDs(String name) {
        try {
            sid = Util.sha256toPubSubId(name.getBytes());
            rid = Util.sha256toPubSubId(sid.getId());
            logger.debug("Name : " + name + "\nsid : " + sid.toString() + "\nrid : " + rid.toString());
        } catch (NoSuchAlgorithmException ex) {
        }
    }

    public void announce() {
        announceSubscription(sid, rid, lifeTime);

        setListener(this);

        startAll();
    }

    @Override
    public void documentReceived(DocumentEntryEvent event) {
        logger.debug("Document was received");
        try {
            stopAll();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

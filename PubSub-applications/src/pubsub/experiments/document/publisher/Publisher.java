package pubsub.experiments.document.publisher;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import org.apache.log4j.Logger;
import pubsub.PubSubID;
import pubsub.transport.FileInfo;
import pubsub.transport.api.document.DocumentSourceAPI;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class Publisher extends DocumentSourceAPI {

    private File file;
    private String fileName;
    private PubSubID sid;
    private PubSubID rid;
    private long lifeTime;
    private final Logger logger;

    public Publisher(Logger logger, String fileName, long lifeTime) {
        super();
        this.file = new File(fileName);
        this.fileName = fileName;
        this.logger = logger;
        this.lifeTime = lifeTime;
    }

    public void announce() throws IOException {
        computePubSubIDs();

        FileInfo fInfo = new FileInfo(file, Announcer.CHUNK_SIZE, sid, rid);
        super.announcePublication(sid, rid, lifeTime, fInfo);
        super.startAll();
    }

    private void computePubSubIDs() {
        try {
            sid = Util.sha256toPubSubId(fileName.getBytes());
            rid = Util.sha256toPubSubId(sid.getId());
            
            //logger.debug("Name : " + fileName + "\nsid : " + sid.toString() + "\nrid : " + rid.toString());
        } catch (NoSuchAlgorithmException ex) {
        }
    }
}

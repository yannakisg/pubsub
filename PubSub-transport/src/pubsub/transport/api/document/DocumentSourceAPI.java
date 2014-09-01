package pubsub.transport.api.document;

import java.io.IOException;
import pubsub.PubSubID;
import pubsub.transport.document.source.DocumentSource;
import pubsub.transport.FileInfo;

/**
 *
 * @author John Gasparis
 */
public abstract class DocumentSourceAPI {

    private DocumentSource source = null;

    public DocumentSourceAPI() {
        if (source == null) {
            source = new DocumentSource();
        }
    }

    protected void announcePublication(PubSubID sid, PubSubID rid, long lifeTime, FileInfo fileInfo) {
        source.announcePublication(sid, rid, lifeTime, fileInfo);
    }

    protected void startAll() {
        source.start();
    }

    protected void stopAll() throws IOException {
        source.close();
    }
}

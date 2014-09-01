package pubsub.transport.api.document;

import java.io.File;
import java.io.IOException;
import pubsub.PubSubID;
import pubsub.Subscription;
import pubsub.transport.document.sink.DocumentSink;

/**
 *
 * @author John Gasparis
 */
public abstract class DocumentSinkAPI implements DocumentSinkListener {

    private DocumentSink sink;
    protected Subscription sub;

    public DocumentSinkAPI(File file) {
        sink = new DocumentSink(file);
        sub = null;
    }

    protected void announceSubscription(PubSubID sid, PubSubID rid, long lifeTime) {
        sub = Subscription.createSubToDocument(sid, rid);
        sink.announceSubscription(sub, lifeTime);
    }

    protected void startAll() {
        sink.start();
    }

    protected void stopAll() throws IOException {
        sink.close();
    }

    protected void setListener(DocumentSinkListener listener) {
        if (sub == null) {
            throw new IllegalStateException("You have to announce a subscription first");
        }

        sink.setListener(listener);
    }
}

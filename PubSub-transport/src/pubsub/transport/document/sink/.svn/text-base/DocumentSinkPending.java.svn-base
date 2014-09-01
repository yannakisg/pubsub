package pubsub.transport.document.sink;

import java.io.File;
import java.io.IOException;
import pubsub.transport.api.document.DocumentSinkListener;
import pubsub.messages.net.transport.DataMessage;
import pubsub.transport.api.document.DocumentEntry;
import pubsub.transport.api.document.DocumentEntryEvent;

/**
 *
 * @author John Gasparis
 */
public class DocumentSinkPending {

    private DocumentEntry documentEntry;
    private DocumentSinkListener listener;

    public void setEntry(File file, int chunkSize) throws IOException {
        documentEntry = new DocumentEntry(file, chunkSize);
    }

    public int getTotalReceivedChunks() {
        if (documentEntry != null) {
            return documentEntry.getTotalReceivedChunks();
        } else {
            return -1;
        }
    }
    
    public long getTotalSize() {
        return documentEntry.getTotalSize();
    }

    public int addData(DataMessage message) {
        documentEntry.addData(message);
        return documentEntry.getTotalReceivedChunks();
    }

    public boolean hasReceived(int chunkNum) {
        return documentEntry.hasReceived(chunkNum);
    }

    public synchronized void documentReceived() {
        documentEntry.documentReceived();
        fireEntryEvent(documentEntry);
    }

    public synchronized void setListener(DocumentSinkListener listener) {
        this.listener = listener;
    }

    public synchronized void removeListener(DocumentSinkListener listener) {
        this.listener = null;
    }

    private synchronized void fireEntryEvent(DocumentEntry entry) {
        DocumentEntryEvent event = new DocumentEntryEvent(this, entry);

        listener.documentReceived(event);
    }
}

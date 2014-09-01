package pubsub.transport.api.document;

/**
 *
 * @author John Gasparis
 */
public interface DocumentSinkListener {

    public void documentReceived(DocumentEntryEvent event);
}

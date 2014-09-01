package pubsub.transport.api.document;

import java.util.EventObject;

/**
 *
 * @author John Gasparis
 */
public class DocumentEntryEvent extends EventObject {

    private DocumentEntry entry;

    public DocumentEntryEvent(Object source, DocumentEntry entry) {
        super(source);
        this.entry = entry;
    }

    public DocumentEntry entry() {
        return this.entry;
    }
}

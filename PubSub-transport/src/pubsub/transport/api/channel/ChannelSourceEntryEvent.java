package pubsub.transport.api.channel;

import java.util.EventObject;

/**
 *
 * @author John Gasparis
 */
public class ChannelSourceEntryEvent extends EventObject {

    private ChannelSourceEntry entry;

    public ChannelSourceEntryEvent(Object source, ChannelSourceEntry entry) {
        super(source);
        this.entry = entry;
    }

    public ChannelSourceEntry entry() {
        return this.entry;
    }
}

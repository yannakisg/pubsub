package pubsub.transport.channel.source;

import pubsub.transport.api.channel.ChannelSourceEntry;
import pubsub.transport.api.channel.ChannelSourceEntryEvent;
import pubsub.transport.api.channel.ChannelSourceListener;

/**
 *
 * @author John Gasparis
 */
public class ChannelSourcePending {

    private ChannelSourceEntry entry;
    private ChannelSourceListener listener;

    public void createEntry() {
        entry = new ChannelSourceEntry();
    }

    public void setID(int id) {
        entry.setID(id);
        fireEntryEvent(entry);
    }

    public synchronized void setListener(ChannelSourceListener listener) {
        this.listener = listener;
    }

    public void stopTransmission() {
        //listener.stopTransmission();
    }

    private synchronized void fireEntryEvent(ChannelSourceEntry entry) {
        ChannelSourceEntryEvent event = new ChannelSourceEntryEvent(this, entry);
        listener.rvpInstructReceived(event);
    }
}

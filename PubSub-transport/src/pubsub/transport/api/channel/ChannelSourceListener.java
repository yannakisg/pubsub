package pubsub.transport.api.channel;

/**
 *
 * @author John Gasparis
 */
public interface ChannelSourceListener {

    public void rvpInstructReceived(ChannelSourceEntryEvent event);
    
}

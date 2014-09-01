package pubsub.localrendezvous.uds;

import pubsub.localrendezvous.LocRCIPCMessage;

/**
 *
 * @author John Gasparis
 */
public interface MessageProcessor {

    public void startAll();

    public void stop();

    public void addToQueue(LocRCIPCMessage msg, IPCMessageWriter writer);
}

package pubsub.localrendezvous.uds;

import java.util.HashMap;
import java.util.Map;
import pubsub.Publication;
import pubsub.localrendezvous.LocRCIPCMessage;
import pubsub.module.Subscriber;

/**
 *
 * @author John Gasparis
 */
public class UDSSubscriber implements Subscriber {

    private final static Map<IPCMessageWriter, UDSSubscriber> map = new HashMap<IPCMessageWriter, UDSSubscriber>();
    private final IPCMessageWriter writer;

    private UDSSubscriber(IPCMessageWriter writer) {
        this.writer = writer;
    }

    @Override
    public void deliver(Publication publication) {
        LocRCIPCMessage mesg = LocRCIPCMessage.createPublicationMessage(publication);
        writer.addToQueue(mesg);
    }

    @Override
    public String getIdentity() {
        return writer.toString();
    }

    public static Subscriber getSubscriber(IPCMessageWriter writer) {
        UDSSubscriber subscriber = null;
        synchronized (map) {
            subscriber = map.get(writer);
            if (subscriber == null) {
                subscriber = new UDSSubscriber(writer);
                map.put(writer, subscriber);
            }
        }
        return subscriber;
    }
}

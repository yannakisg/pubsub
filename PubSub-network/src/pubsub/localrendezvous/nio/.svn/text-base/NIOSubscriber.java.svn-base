package pubsub.localrendezvous.nio;

import pubsub.localrendezvous.LocRCIPCMessage;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import pubsub.Publication;
import pubsub.module.Subscriber;

/**
 *
 * @author tsilo
 * @author John Gasparis
 */
public class NIOSubscriber implements Subscriber {

    private final static Map<SocketChannel, NIOSubscriber> map = new HashMap<SocketChannel, NIOSubscriber>();
    private final SocketChannel channel;
    private final ChannelQueueManager queueManager;

    private NIOSubscriber(SocketChannel channel, ChannelQueueManager manager) {
        this.channel = channel;
        this.queueManager = manager;
    }

    @Override
    public void deliver(Publication publication) {
        LocRCIPCMessage mesg = LocRCIPCMessage.createPublicationMessage(publication);
        this.queueManager.addPendingMessage(channel, mesg);
    }

    @Override
    public String getIdentity() {
        return channel.toString();
    }

    public static Subscriber getSubscriber(SocketChannel channel, ChannelQueueManager manager) {
        NIOSubscriber subscriber = null;
        synchronized (map) {
            subscriber = map.get(channel);
            if (subscriber == null) {
                subscriber = new NIOSubscriber(channel, manager);
                map.put(channel, subscriber);
            }
        }
        return subscriber;
    }
}

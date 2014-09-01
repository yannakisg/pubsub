package pubsub.localrendezvous.nio;

import pubsub.localrendezvous.LocRCIPCMessage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 *
 * @author tsilo
 * @author John Gasparis
 */
public class ChannelQueueManager {

    private final Logger logger = Logger.getLogger(ChannelQueueManager.class);
    private final static Map<Selector, ChannelQueueManager> map = new HashMap<Selector, ChannelQueueManager>();
    private final Selector selector;
    private final Set<SocketChannel> pendingRequests = new HashSet<SocketChannel>();
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingMessages = new HashMap<SocketChannel, Queue<ByteBuffer>>();

    private ChannelQueueManager(Selector sel) {
        this.selector = sel;
    }

    public static ChannelQueueManager getBySelector(Selector sel) {
        ChannelQueueManager manager = null;
        synchronized (map) {
            manager = map.get(sel);
            if (manager == null) {
                manager = new ChannelQueueManager(sel);
                map.put(sel, manager);
            }
        }
        return manager;
    }

    public void checkPendingRequests() {
        synchronized (pendingRequests) {
            for (SocketChannel channel : pendingRequests) {
                channel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
            }
            pendingRequests.clear();
        }
    }

    public void addPendingMessage(SocketChannel channel, LocRCIPCMessage message) {
        synchronized (this.pendingRequests) {
            this.pendingRequests.add(channel);

            synchronized (this.pendingMessages) {
                Queue<ByteBuffer> queue = this.pendingMessages.get(channel);
                if (queue == null) {
                    queue = new LinkedList<ByteBuffer>();
                    this.pendingMessages.put(channel, queue);
                }
                if (!queue.add(ByteBuffer.wrap(message.toBytes()))) {
                    //TODO
                    logger.error("DID NOT ADD MESG TO QUEUE");
                }
            }
        }
        this.selector.wakeup();
    }

    public void sendPendingByKey(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        synchronized (pendingMessages) {
            Queue<ByteBuffer> queue = pendingMessages.get(channel);
            if (queue == null) {
                return;
            }

            while (!queue.isEmpty()) {
                ByteBuffer buffer = queue.peek();
                channel.write(buffer);

                if (buffer.remaining() > 0) {
                    logger.debug("Buffer has remaining data");
                    break;
                }

                queue.remove();
            }

            if (queue.isEmpty()) {
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    public void removePending(SocketChannel channel) {
        synchronized (pendingRequests) {
            pendingRequests.remove(channel);

            synchronized (pendingMessages) {
                pendingMessages.remove(channel);
            }
        }
    }
}

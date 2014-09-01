package pubsub.localrendezvous.nio;

import pubsub.localrendezvous.LocRCIPCMessage;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

import org.apache.log4j.Logger;

import pubsub.Publication;
import pubsub.util.ProducerConsumerQueue;
import pubsub.util.StoppableThread;

/**
 *
 * @author tsilo
 * @author John Gasparis
 */
public class ClientWorker extends StoppableThread {

    private static final Logger logger = Logger.getLogger(ClientWorker.class);
    private final Selector selector;
    private final ChannelQueueManager queueManager;
    private final SocketChannel socketChannel;
    private final ProducerConsumerQueue<Publication> queue = ProducerConsumerQueue.createNew();

    protected ClientWorker(String host, int port) throws IOException {
        selector = initSelector();
        this.queueManager = ChannelQueueManager.getBySelector(selector);

        socketChannel = initConnection(host, port);
        SelectionKey key = socketChannel.register(selector, SelectionKey.OP_WRITE);
        key.attach(new IPCMessageReader(key));
    }

    private Selector initSelector() throws IOException {
        return SelectorProvider.provider().openSelector();
    }

    private SocketChannel initConnection(String host, int port)
            throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);

        if (!channel.connect(new InetSocketAddress(host, port))) {
            channel.finishConnect();
        }
        return channel;
    }

    public void addMessage(LocRCIPCMessage mesg) {
        this.queueManager.addPendingMessage(this.socketChannel, mesg);
    }

    public Publication getNextPublication() throws InterruptedException {
        return this.queue.getConsumer().take();
    }

    @Override
    public void run() {

        while (!isShutDown()) {
            try {
                this.queueManager.checkPendingRequests();
                while (selector.select() > 0) {
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();

                        if (!key.isValid()) {
                            continue;
                        }

                        if (key.isReadable()) {
                            read(key);
                        } else if (key.isWritable()) {
                            write(key);
                        }
                    }

                }
            } catch (Exception e) {
                if (!isShutDown()) {
                    logger.trace(e, e);
                    throw new RuntimeException(e);
                } else {
                    logger.debug("shutting down gracefully");
                }
            }
        }
    }

    @Override
    public synchronized void shutDown() {
        super.shutDown();

        try {
            this.selector.close();
            this.socketChannel.close();
        } catch (IOException e) {
            logger.debug("exception while shutting down", e);
        }
    }

    private void read(SelectionKey key) {
        IPCMessageReader reader = (IPCMessageReader) key.attachment();
        try {
            reader.read();
        } catch (IOException e) {
            cancel(key);
        }
        if (reader.completed()) {
            LocRCIPCMessage mesg = reader.getIPCMessage();
            if (!mesg.isPublication()) {
                String errMesg = "invalid message from locRC " + mesg.type();
                logger.debug(errMesg);
                throw new RuntimeException(errMesg);
            }
            this.queue.getProduder().offer(mesg.getPublication());
            reader.reset();
        }
    }

    private void write(SelectionKey key) {
        try {
            this.queueManager.sendPendingByKey(key);
        } catch (IOException e) {
            cancel(key);
        }

    }

    private void cancel(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();

        logger.debug("cancel connection and removing all related state: "
                + channel.toString());
        key.cancel();

        this.queueManager.removePending(channel);
    }
}

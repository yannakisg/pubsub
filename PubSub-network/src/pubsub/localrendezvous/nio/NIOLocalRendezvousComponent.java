package pubsub.localrendezvous.nio;

import pubsub.localrendezvous.LocRCIPCMessage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

import org.apache.log4j.Logger;

import pubsub.localrendezvous.LocalRComponent;
import pubsub.module.PubSubModuleManager;
import pubsub.module.Subscriber;

/**
 *
 * @author John Gasparis
 * @author tsilo
 */
public class NIOLocalRendezvousComponent extends LocalRComponent {

    private static Logger logger = Logger.getLogger(NIOLocalRendezvousComponent.class);
    private static final String NIO_LOCRC_NAME = "NIOLocalRendezvousComponent";
    private final Selector selector;
    private final MessageProccesor messageProccesor;

    public NIOLocalRendezvousComponent(String host, int port) throws IOException {
        InetAddress address = InetAddress.getByName(host);
        selector = this.initSelector(address, port);
        setName(NIO_LOCRC_NAME);
        ChannelQueueManager manager = ChannelQueueManager.getBySelector(selector);
        messageProccesor = new MessageProccesor(manager, getName());

        logger.debug("Listening on port " + port);
    }

    private Selector initSelector(InetAddress address, int port)
            throws IOException {
        Selector socketSelector = SelectorProvider.provider().openSelector();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();

        ServerSocket serverSocket = serverChannel.socket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(new InetSocketAddress(address, port));
        serverChannel.configureBlocking(false);
        serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

        return socketSelector;
    }

    @Override
    public void startAll() {
        this.start();
        this.messageProccesor.startAll();
    }

    @Override
    public void run() {
        while (!isShutDown()) {
            ChannelQueueManager.getBySelector(selector).checkPendingRequests();

            logger.debug("Waiting for IO events");
            try {
                while (selector.select() > 0) {
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();

                        if (!key.isValid()) {
                            ChannelQueueManager.getBySelector(selector).removePending((SocketChannel) key.channel());
                            continue;
                        }

                        if (key.isAcceptable()) {
                            this.accept(key);
                        } else if (key.isReadable()) {
                            read(key);
                        } else if (key.isWritable()) {
                            write(key);
                        }
                    }
                }
            } catch (IOException e) {
                if (!isShutDown()) {
                    logger.trace(e, e);
                } else {
                    logger.debug("shutting down gracefully");
                }

            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel server;
        SocketChannel channel;

        server = (ServerSocketChannel) key.channel();
        channel = server.accept();

        if (channel == null) {
            return;
        }

        channel.configureBlocking(false);
        SelectionKey selKey = channel.register(selector, SelectionKey.OP_READ);

        IPCMessageReader readState = new IPCMessageReader(selKey);
        selKey.attach(readState);
    }

    private void write(SelectionKey key) {
        try {
            ChannelQueueManager.getBySelector(selector).sendPendingByKey(key);
        } catch (IOException e) {
            cancelAll(key);
        }
    }

    private void read(SelectionKey key) {
        IPCMessageReader reader = (IPCMessageReader) key.attachment();
        try {
            reader.read();
        } catch (IOException e) {
            cancelAll(key);
        }
        if (reader.completed()) {
            LocRCIPCMessage mesg = reader.getIPCMessage();
            this.messageProccesor.addToQueue(mesg, key);
            reader.reset();
        }
    }

    private void cancelAll(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();

        logger.debug("cancel connection and removing all related state: " + channel.toString());
        key.cancel();

        ChannelQueueManager manager = ChannelQueueManager.getBySelector(selector);
        manager.removePending(channel);

        Subscriber s = NIOSubscriber.getSubscriber(channel, manager);
        PubSubModuleManager.getModule().removeSubscriber(s);
    }
}

package pubsub.localrendezvous.nio;

import pubsub.localrendezvous.LocRCIPCMessage;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import pubsub.Publication;
import pubsub.Subscription;
import pubsub.module.PubSubModuleManager;
import pubsub.module.Subscriber;
import pubsub.util.Pair;
import pubsub.util.ProducerConsumerQueue;
import pubsub.util.StoppableThread;

/**
 *
 * @author tsilo
 */
public class MessageProccesor {

    private static final Logger logger = Logger.getLogger(MessageProccesor.class);
    private static final ProducerConsumerQueue<Pair<LocRCIPCMessage, SelectionKey>> queue = ProducerConsumerQueue.createNew();
    private final ChannelQueueManager manager;
    private ProcessThread procThread = new ProcessThread();
    private boolean stopped = false;

    public MessageProccesor(ChannelQueueManager mng, String name) {
        this.manager = mng;
        this.procThread.setNamePrefix(name + "/" + this.getClass().getSimpleName());
    }

    public void startAll() {
        this.procThread.start();
    }

    public void stop() {
        if (!stopped) {
            stopped = true;
            this.procThread.shutDown();
            this.procThread.interrupt();
        }
    }

    @Override
    public void finalize() throws Throwable {
        try {
            stop();
        } finally {
            super.finalize();
        }
    }

    private void processMessage(LocRCIPCMessage mesg, SelectionKey key) {
        /*dispatch message*/
        if (mesg.isPublication()) {
            handlePublication(mesg.getPublication());
        } else if (mesg.isSubscription()) {
            handleSubscription(mesg.getSubscription(), key);
        } else if (mesg.isUnsubscribe()) {
            handleUnSubscription(mesg.getSubscription(), key);
        } else {
            logger.debug("unknown IPC Message type " + mesg.type());
        }
    }

    private void handlePublication(Publication publication) {
        PubSubModuleManager.getModule().publish(publication);
    }

    private void handleSubscription(Subscription subscription, SelectionKey key) {
        SocketChannel chan = (SocketChannel) key.channel();
        Subscriber niosubscriber = NIOSubscriber.getSubscriber(chan, this.manager);
        PubSubModuleManager.getModule().subscribe(subscription, niosubscriber);
    }

    private void handleUnSubscription(Subscription subscription,
            SelectionKey key) {
        SocketChannel chan = (SocketChannel) key.channel();
        Subscriber niosubscriber = NIOSubscriber.getSubscriber(chan, this.manager);
        PubSubModuleManager.getModule().unsubscribe(subscription, niosubscriber);
    }

    public void addToQueue(LocRCIPCMessage mesg, SelectionKey key) {
        Pair<LocRCIPCMessage, SelectionKey> pair = new Pair<LocRCIPCMessage, SelectionKey>(mesg, key);
        if (!queue.getProduder().offer(pair)) {
            logger.error("DID NOT offer mesg to queue");
        }
    }

    private class ProcessThread extends StoppableThread {

        @Override
        public void run() {
            while (!isShutDown()) {
                try {
                    Pair<LocRCIPCMessage, SelectionKey> pair = queue.getConsumer().take();
                    processMessage(pair.getFirst(), pair.getSecond());
                } catch (InterruptedException e) {
                    if (!isShutDown()) {
                        logger.error(e.getMessage(), e);
                        throw new RuntimeException(e);
                    } else {
                        logger.debug("shut down gracefully");
                    }
                }
            }
        }
    }
}

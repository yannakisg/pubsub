package pubsub.localrendezvous.uds;

import org.apache.log4j.Logger;
import pubsub.Publication;
import pubsub.Subscription;
import pubsub.localrendezvous.LocRCIPCMessage;
import pubsub.module.PubSubModuleManager;
import pubsub.module.Subscriber;
import pubsub.util.Pair;
import pubsub.util.ProducerConsumerQueue;
import pubsub.util.StoppableThread;

/**
 *
 * @author John Gasparis
 */
public class ServerMessageProcessor implements MessageProcessor {

    private static final Logger logger = Logger.getLogger(ServerMessageProcessor.class);
    private static final ProducerConsumerQueue<Pair<LocRCIPCMessage, IPCMessageWriter>> queue = ProducerConsumerQueue.createNew();
    private ProcessThread procThread;
    private boolean stopped = false;

    public ServerMessageProcessor(String name) {
        this.procThread = new ProcessThread();
        this.procThread.setNamePrefix(name + "/" + this.getClass().getSimpleName());
    }

    @Override
    public void startAll() {
        this.procThread.start();
    }

    @Override
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

    @Override
    public synchronized void addToQueue(LocRCIPCMessage msg, IPCMessageWriter writer) {
        Pair<LocRCIPCMessage, IPCMessageWriter> pair = new Pair<LocRCIPCMessage, IPCMessageWriter>(msg, writer);
        if (!queue.getProduder().offer(pair)) {
            logger.error("DID NOT offer mesg to queue");
        }
    }

    private void processMessage(LocRCIPCMessage msg, IPCMessageWriter writer) {
        if (msg.isPublication()) {
            handlePublication(msg.getPublication());
        } else if (msg.isSubscription()) {
            handleSubscription(msg.getSubscription(), writer);
        } else if (msg.isUnsubscribe()) {
            handleUnSubscription(msg.getSubscription(), writer);
        } else {
            logger.debug("unknown IPC Message type " + msg.type());
        }
    }

    private void handlePublication(Publication publication) {
        PubSubModuleManager.getModule().publish(publication);
    }

    private void handleSubscription(Subscription subscription, IPCMessageWriter writer) {
        Subscriber udsSubscriber = UDSSubscriber.getSubscriber(writer);
        PubSubModuleManager.getModule().subscribe(subscription, udsSubscriber);
    }

    private void handleUnSubscription(Subscription subscription, IPCMessageWriter writer) {
        Subscriber udsSubscriber = UDSSubscriber.getSubscriber(writer);
        PubSubModuleManager.getModule().unsubscribe(subscription, udsSubscriber);
    }

    private class ProcessThread extends StoppableThread {

        @Override
        public void run() {
            Pair<LocRCIPCMessage, IPCMessageWriter> pair;
            while (!isShutDown()) {
                try {
                    pair = queue.getConsumer().take();
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

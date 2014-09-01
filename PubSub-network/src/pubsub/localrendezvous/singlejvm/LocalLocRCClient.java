package pubsub.localrendezvous.singlejvm;

import java.io.IOException;
import org.apache.log4j.Logger;

import pubsub.Publication;
import pubsub.Subscription;
import pubsub.localrendezvous.LocRCClient;
import pubsub.module.PubSubModuleManager;
import pubsub.module.Subscriber;
import pubsub.util.ProducerConsumerQueue;

public class LocalLocRCClient implements LocRCClient, Subscriber {

    private static final Logger logger = Logger.getLogger(LocalLocRCClient.class);
    private static long counter = 0;
    private final long regNum;
    private final ProducerConsumerQueue<Publication> buffer = ProducerConsumerQueue.createNew();
    private final String strRepr;

    public LocalLocRCClient() {
        regNum = ++counter;
        strRepr = this.getClass().getName() + "/" + regNum;
    }

    public LocalLocRCClient(String name) {
        regNum = ++counter;
        strRepr = name;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public boolean communicationWorks() {
        return true;
    }

    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public void publish(Publication p) {
        PubSubModuleManager.getModule().publish(p);
    }

    @Override
    public Publication receiveNext() throws InterruptedException {
        return buffer.getConsumer().take();
    }

    @Override
    public void subscribe(Subscription s) {
        PubSubModuleManager.getModule().subscribe(s, this);
    }

    @Override
    public void unsubscribe(Subscription s) {
        PubSubModuleManager.getModule().unsubscribe(s, this);
    }

    @Override
    public void deliver(Publication publication) {

        if (!this.buffer.getProduder().offer(publication)) {
            logger.debug("MESSAGE LOST");
        }
    }

    @Override
    public String getIdentity() {
        return this.toString();
    }

    @Override
    public String toString() {
        return strRepr;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (regNum ^ (regNum >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        LocalLocRCClient other = (LocalLocRCClient) obj;
        if (regNum != other.regNum) {
            return false;
        }
        return true;
    }
}

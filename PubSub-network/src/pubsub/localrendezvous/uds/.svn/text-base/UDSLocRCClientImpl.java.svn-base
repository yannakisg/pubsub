package pubsub.localrendezvous.uds;

import java.io.IOException;
import org.apache.log4j.Logger;
import pubsub.Publication;
import pubsub.Subscription;
import pubsub.localrendezvous.LocRCClient;
import pubsub.localrendezvous.LocRCFactory;
import pubsub.localrendezvous.LocRCIPCMessage;

/**
 *
 * @author John Gasparis
 */
public class UDSLocRCClientImpl implements LocRCClient {
    private String name;
    private ClientMessageProcessor worker;
    private boolean closed = false;

    public UDSLocRCClientImpl() throws IOException {
        this("");
    }

    public UDSLocRCClientImpl(String name) throws IOException {
        this(LocRCFactory.FILENAME, name);
    }

    public UDSLocRCClientImpl(String fileName, String name) throws IOException {
        this.name = name;
        worker = new ClientMessageProcessor(fileName, name);
        worker.startAll();
    }

    @Override
    public void publish(Publication p) {
        LocRCIPCMessage mesg = LocRCIPCMessage.createPublicationMessage(p);
        worker.sendMessage(mesg);
    }

    @Override
    public void subscribe(Subscription s) {
        LocRCIPCMessage mesg = LocRCIPCMessage.createSubscriptionMessage(s);
        worker.sendMessage(mesg);
    }

    @Override
    public void unsubscribe(Subscription s) {
        LocRCIPCMessage mesg = LocRCIPCMessage.createUnsubscribeMessage(s);
        worker.sendMessage(mesg);
    }

    @Override
    public boolean communicationWorks() {
        return !worker.isShutDown();
    }

    @Override
    public Publication receiveNext() throws InterruptedException {
        return worker.getNextPublication();
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            closed = true;
            worker.stop();
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }
}

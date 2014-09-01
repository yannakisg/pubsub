package pubsub.localrendezvous.nio;

import pubsub.localrendezvous.LocRCIPCMessage;
import java.io.IOException;

import pubsub.Publication;
import pubsub.Subscription;
import pubsub.localrendezvous.LocRCClient;
import pubsub.localrendezvous.LocRCFactory;

/**
 *
 * @author John Gasparis
 * @author tsilo
 */
public class NIOLocRCClientImpl implements LocRCClient {

    private String name;
    private ClientWorker worker;
    private boolean closed = false;

    public NIOLocRCClientImpl() throws IOException {
        this("");
    }

    public NIOLocRCClientImpl(String name) throws IOException {
        this(LocRCFactory.HOST, LocRCFactory.PORT, name);
    }

    public NIOLocRCClientImpl(String host, int port, String name)
            throws IOException {
        this.name = name;
        worker = new ClientWorker(host, port);
        worker.setNamePrefix(this.name);
        worker.start();
    }

    @Override
    public void publish(Publication p) {
        LocRCIPCMessage mesg = LocRCIPCMessage.createPublicationMessage(p);
        worker.addMessage(mesg);
    }

    @Override
    public void subscribe(Subscription s) {
        LocRCIPCMessage mesg = LocRCIPCMessage.createSubscriptionMessage(s);
        worker.addMessage(mesg);
    }

    @Override
    public void unsubscribe(Subscription s) {
        LocRCIPCMessage mesg = LocRCIPCMessage.createUnsubscribeMessage(s);
        worker.addMessage(mesg);
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
            worker.shutDown();
            worker.interrupt();
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

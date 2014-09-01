package pubsub.localrendezvous.uds;

import com.etsy.net.JUDS;
import com.etsy.net.UnixDomainSocket;
import com.etsy.net.UnixDomainSocketClient;
import java.io.IOException;
import org.apache.log4j.Logger;
import pubsub.Publication;
import pubsub.localrendezvous.LocRCIPCMessage;
import pubsub.util.ProducerConsumerQueue;

/**
 *
 * @author John Gasparis
 */
public class ClientMessageProcessor implements MessageProcessor {

    private static final Logger logger = Logger.getLogger(ClientMessageProcessor.class);
    private final ProducerConsumerQueue<Publication> queue = ProducerConsumerQueue.createNew();
    private UnixDomainSocket clientSocket;
    private IPCMessageReader reader;
    private IPCMessageWriter writer;
    private boolean closed = false;
    private String name;

    protected ClientMessageProcessor(String fileName, String name) throws IOException {
        clientSocket = new UnixDomainSocketClient(fileName, JUDS.SOCK_STREAM);
        this.name = name + "/ClientMessageProcessor";
        reader = new IPCMessageReader(this, clientSocket.getInputStream(), this.name);
        writer = new IPCMessageWriter(clientSocket.getOutputStream(), this.name);

        reader.setIPCMessageWriter(writer);
        writer.setIPCMessageReader(reader);
    }

    public void sendMessage(LocRCIPCMessage mesg) {
        writer.addToQueue(mesg);
    }

    public Publication getNextPublication() throws InterruptedException {
        return queue.getConsumer().take();
    }

    public boolean isShutDown() {
        return !closed;
    }

    @Override
    public void startAll() {
        reader.start();
        writer.start();
    }

    @Override
    public void stop() {
        
        if (!closed) {
            closed = true;
            
            logger.debug("Stop the reader");
            reader.close();
            
            logger.debug("Stop the writer");
            writer.close();
            
            clientSocket.close();
        }
    }

    @Override
    public void addToQueue(LocRCIPCMessage msg, IPCMessageWriter writer) {
        this.queue.getProduder().offer(msg.getPublication());
    }
}

package pubsub.localrendezvous.uds;

import com.etsy.net.JUDS;
import com.etsy.net.UnixDomainSocket;
import com.etsy.net.UnixDomainSocketServer;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import pubsub.localrendezvous.LocalRComponent;

/**
 *
 * @author John Gasparis
 */
public class UDSLocalRendezvousComponent extends LocalRComponent {

    private static final Logger logger = Logger.getLogger(UDSLocalRendezvousComponent.class);
    private static final int BACK_LOG = 256;
    private static final int SOCKET_TYPE = JUDS.SOCK_STREAM;
    private UnixDomainSocketServer server;
    private ServerMessageProcessor messageProcessor;

    public UDSLocalRendezvousComponent(String fileName) throws IOException {
        try {
            server = new UnixDomainSocketServer(fileName, SOCKET_TYPE, BACK_LOG);
        } catch (IOException ex) {
            logger.debug("Deleting the file : " + fileName);
            new File(fileName).delete();
            server = new UnixDomainSocketServer(fileName, SOCKET_TYPE, BACK_LOG);
        }
        messageProcessor = new ServerMessageProcessor(getName());
        messageProcessor.startAll();

        this.setName("UDSLocalRendezvousComponent");
    }

    @Override
    public void startAll() {
        this.start();
    }

    @Override
    public void run() {
        UnixDomainSocket socket;

        while (!isShutDown()) {
            try {
                socket = server.accept();
                createAndRun(socket);
            } catch (IOException e) {
                if (!isShutDown()) {
                    logger.trace(e, e);
                } else {
                    logger.debug("shutting down gracefully");
                }

            }
        }
    }

    private void createAndRun(UnixDomainSocket socket) {
        IPCMessageReader reader;
        IPCMessageWriter writer;

        reader = new IPCMessageReader(messageProcessor, socket.getInputStream(), "UDSLocalRendezvousComponent");
        writer = new IPCMessageWriter(socket.getOutputStream(), "UDSLocalRendezvousComponent");

        reader.setIPCMessageWriter(writer);
        writer.setIPCMessageReader(reader);

        reader.start();
        writer.start();
    }

    @Override
    public void finalize() throws Throwable {
        try {
            server.unlink();
            server.close();
        } finally {
            super.finalize();
        }
    }
}

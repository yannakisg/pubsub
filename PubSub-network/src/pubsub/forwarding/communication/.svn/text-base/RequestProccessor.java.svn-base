package pubsub.forwarding.communication;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import pubsub.util.StoppableThread;

public class RequestProccessor extends StoppableThread {

    private final static Logger logger = Logger.getLogger(RequestProccessor.class);
    private Socket socket;
    private AttachLinkServer attachLinkServer;

    public RequestProccessor(Socket socket, AttachLinkServer attachLinkServer) {
        this.socket = socket;
        this.attachLinkServer = attachLinkServer;
    }

    @Override
    public void run() {
        while (!isShutDown()) {
            InputStream in;
            try {
                in = socket.getInputStream();
                int opCode = in.read();
                if (opCode == AttachLinkServer.CREATE_LINK) {
                    logger.debug("Received attach create link message");
                    attachLinkServer.createNewLink(socket);
                } else if (opCode == AttachLinkServer.CREATE_LINK_EXPLICIT) {
                    attachLinkServer.createNewLinkExplicit(socket);
                } else if (opCode == AttachLinkServer.ATTACH_LINK) {
                    attachLinkServer.attachLink(socket);
                    shutDown();
                }
            } catch (IOException e) {
                logger.error("connection aborted", e);
                shutDown();
            }
        }
    }
}

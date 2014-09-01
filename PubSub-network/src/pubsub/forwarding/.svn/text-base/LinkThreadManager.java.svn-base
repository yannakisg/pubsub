package pubsub.forwarding;

import pubsub.forwarding.communication.CommunicationLink;
import org.apache.log4j.Logger;


/**
 *
 * @author tsilo
 */
public class LinkThreadManager {

    private static final Logger logger = Logger.getLogger(LinkThreadManager.class);
    private LinkIncomingThread incomingThread;
    private FwdComponent fwdC;
    private CommunicationLink link;
    private KeepAliveThread keepAliveThread;

    public LinkThreadManager(CommunicationLink communicationLink,
            int linkIndex, FwdComponent fwdComponent, double weight) {
        this.link = communicationLink;
        this.fwdC = fwdComponent;
        String prefix = this.fwdC.getName() + "/" + linkIndex + "/";

        incomingThread = new LinkIncomingThread(link, fwdC);
        incomingThread.setNamePrefix(prefix);

        keepAliveThread = new KeepAliveThread(link, fwdC, weight);
        keepAliveThread.setNamePrefix(prefix);

        logger.debug("creating link threads: incoming, outgoing, keepalive");
    }

    public void handleKeepAlive(KeepAliveMessage mesg) {
        this.keepAliveThread.inform(mesg);
    }

    public void startThreads() {
        incomingThread.start();
        keepAliveThread.start();
    }
}

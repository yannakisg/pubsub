package pubsub.transport.tsilo.source;

import org.apache.log4j.Logger;

import pubsub.ContentType;
import pubsub.Publication;
import pubsub.Subscription;
import pubsub.localrendezvous.TimeOutLocRCClient;
import pubsub.messages.net.rva.InstructDocumentTransfer;
import pubsub.messages.net.transport.ControlMessage;
import pubsub.util.Consumer;
import pubsub.util.StoppableThread;

public class FirstRequestThread extends StoppableThread {

    private final static Logger logger = Logger.getLogger(FirstRequestThread.class);
    private int pieceSize;
    private int totalPieces;
    private Consumer<Publication> notificationQueue;
    private Subscription orgnlDoc;
    private Subscription feedBackName;
    private TimeOutLocRCClient locRCClient;

    public FirstRequestThread(Consumer<Publication> notificationQueue, Subscription orgnDoc, Subscription feedback,
            int pieceSize, int totalPieces, TimeOutLocRCClient client) {
        this.orgnlDoc = orgnDoc;
        this.feedBackName = feedback;
        this.notificationQueue = notificationQueue;
        this.pieceSize = pieceSize;
        this.totalPieces = totalPieces;
        this.locRCClient = client;
    }

    @Override
    public void run() {
        while (!isShutDown()) {
            try {
                Publication pub = this.notificationQueue.take();
                logger.debug("document request arrived");
                InstructDocumentTransfer rvpInstruct = InstructDocumentTransfer.parseByteBuffer(pub.wrapData());

                ControlMessage mesg = new ControlMessage(orgnlDoc.getScopeId(), orgnlDoc.getRendezvousId(), rvpInstruct.getPubtoSub(), feedBackName.getScopeId(), feedBackName.getRendezvousId(), rvpInstruct.getPubtoSub(), rvpInstruct.getSubtoPub(), this.pieceSize, this.totalPieces);
                mesg.publish(this.locRCClient, ContentType.DOCUMENT, mesg.toBytes());
            } catch (InterruptedException e) {
                if (!isShutDown()) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

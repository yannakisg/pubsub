package pubsub.transport.tsilo.receiver;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import pubsub.ForwardIdentifier;
import pubsub.PubSubID;
import pubsub.Publication;
import pubsub.Subscription;
import pubsub.localrendezvous.LocRCClientFactory;
import pubsub.localrendezvous.TimeOutLocRCClient;
import pubsub.messages.net.transport.ControlMessage;
import pubsub.rva.RVS;
import pubsub.tmc.TMCInfo;
import pubsub.tmc.graph.GatewayNode;
import pubsub.util.Consumer;

public class DocumentReceiver implements TransportCallBack {

    private static final Logger logger = Logger.getLogger(DocumentReceiver.class);
    private static String LOCALHOST_ADDR = "localhost";

    public static void configureLocalhostAddr(String loopbackAddr) {
        LOCALHOST_ADDR = loopbackAddr;
    }
    private static int LOCALHOST_PORT = 10000;

    public static void configureLocalhostPort(int port) {
        LOCALHOST_PORT = port;
    }
    private PubSubID docScope;
    private PubSubID docRid;
    private File file;
    private final TimeOutLocRCClient locRC;
    private int maxConnectionTries = 3;
    private long timeout = 5000;
    private ControlMessage ctrlMsg = null;
    private FileChunkHandler fileChunkHandler;

    public DocumentReceiver(PubSubID scope, PubSubID rid, File file) {
        this.docScope = scope;
        this.docRid = rid;
        this.file = file;
        this.locRC = LocRCClientFactory.createTimeOutClient(LOCALHOST_ADDR, LOCALHOST_PORT, DocumentReceiver.class.getName());
    }

    public void connect() throws DocumentReceiverExcpetion {
        Subscription documentSubscription = Subscription.createSubToDocument(
                this.docScope, this.docRid);
        Consumer<Publication> incoming = locRC.subscribeNonBlock(documentSubscription);
        Publication init = null;
        int connectionTries = 0;
        try {
            while (init == null && connectionTries < maxConnectionTries) {
                logger.debug("subscribing to document");
                //RVS.subscribe(documentSubscription, this.locRC);
                init = incoming.poll(timeout, TimeUnit.MILLISECONDS);
                if (init == null) {
                    logger.debug("timeout, re-subscribe");
                } else {
                    logger.debug("got control message");
                }
                connectionTries++;
            }

        } catch (InterruptedException e) {
            throw new DocumentReceiverExcpetion(e);
        }

        if (init == null) {
            String error = "init publication did not arrive after " + connectionTries + " tries";
            logger.debug(error);
            throw new DocumentReceiverExcpetion(error);
        }
        this.locRC.unsubscribe(documentSubscription);

        this.ctrlMsg = ControlMessage.parseByteBuffer(init.wrapData());
        ForwardIdentifier subGWToPub = this.ctrlMsg.getSubToPub();

        GatewayNode gw = TMCInfo.getDefaultGateway(this.locRC);
        ForwardIdentifier fidToGW = new ForwardIdentifier(gw.getLidORVlid(gw.getLID()), (short) 1);
        subGWToPub.addPath(fidToGW);
    }

    public void transfer() throws IOException {
        //compute file length
        this.file.createNewFile();

        Subscription orgnlDocName = Subscription.createRequestToImmutableData(docScope, docRid);
        ReceiverTransportProtocol recvTransport = new ReceiverTransportProtocol(orgnlDocName, this.locRC, this.ctrlMsg);
        recvTransport.setNamePrefix(this.getClass().getName());
        recvTransport.registerCallBack(this);

        fileChunkHandler = new FileChunkHandler(this.file, this.ctrlMsg.getChunkSize(), this.ctrlMsg.getTotalChunks());
        recvTransport.registerChunkArrivalHandler(fileChunkHandler);

        recvTransport.start();
    }

    public void setMaxConnectionTries(int tries) {
        this.maxConnectionTries = Math.min(0, tries);
    }

    public void setConnectionTimeout(long timeout) {
        this.timeout = Math.min(0, timeout);
    }

    public void notifyTransferCompleted() {
        try {
            fileChunkHandler.close();
        } catch (IOException e) {
            logger.debug(e.getMessage(), e);
        }
    }
}

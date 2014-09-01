package pubsub.rva;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import pubsub.ACKHandler;
import pubsub.ForwardIdentifier;
import pubsub.Publication;
import pubsub.Subscription;
import pubsub.messages.Message;
import pubsub.messages.MessageType;
import pubsub.messages.RVAAnnouncement;
import pubsub.messages.ipc.rva.HostAppToRVAIPCMessage;
import pubsub.messages.net.rva.InstructChannelTransfer;
import pubsub.messages.net.rva.InstructDocumentTransfer;
import pubsub.messages.net.rva.InstructUnsubscribe;
import pubsub.messages.net.rva.RVA2RVANetMessage;
import pubsub.messages.net.rva.RVAAckMessage;
import pubsub.messages.net.rvp.RVPAckMessage;
import pubsub.tmc.TMCInfo;
import pubsub.tmc.graph.GatewayNode;

/**
 * 
 * @author John Gasparis
 * @author xvas 
 */
class RVAHostComponent extends RVAComponentBase {
    
    private static final Logger logger = Logger.getLogger(RVAHostComponent.class);
    private ACKHandler handler;
    private ForwardIdentifier defaultGWFIDwithVLID = null; // my default gateway router (proxy router)
    private ForwardIdentifier defaultGWFID = null;
    private GatewayNode gw = null;
    private boolean closed = false;
    private final Map<Subscription, Subscription> notificationMap = new HashMap<Subscription, Subscription>();

    public RVAHostComponent() {
        super();
        setName("RVAHostComponent");
        this.handler = new ACKHandler();
        handler.setDaemon(false);
        try {
            logger.addAppender(new FileAppender(new PatternLayout("%d [%t] %-5p %c - %m%n"), "rvaHost.log", false));
        } catch (IOException ex) {
        }
    }

    /*
     * What to subscribe to ...
     */
    private void subscribeFor() {
        Subscription sub = Subscription.createSubToMutableData(RVAUtil.RVA_SID, RVAUtil.RVA_RID);
        locRCClient.subscribe(sub);

        Subscription subRVACom = Subscription.createSubToMutableData(RVAUtil.RVA_SID, RVAUtil.RVA_COM_RID);
        locRCClient.subscribe(subRVACom);
    }

    private void processForwardIPCMessage(HostAppToRVAIPCMessage appAnnouncement) {
        RVAAnnouncement rvaAnn = appAnnouncement.getRVAAnnouncement();

        if (rvaAnn.isPublication()) {
            logger.debug("Announce Publication");

            Subscription itemname = Subscription.createSubscription(rvaAnn.getSID(), rvaAnn.getRID(), rvaAnn.getContentType());
            Subscription notification = appAnnouncement.getNotificationName();
            addNotification(itemname, notification);
        } else if (rvaAnn.isSubscription()){
           logger.debug("Announce Subscription");
        } else {
           logger.debug("Announce UnSubscription");
        }

        RVA2RVANetMessage fwdNet = new RVA2RVANetMessage(appAnnouncement.getRVAAnnouncement(), getMyNodeID(), RVA2RVANetMessage.ForwardMessageSource.HOST, defaultGWFID, defaultGWFIDwithVLID);

        fwdNet.publishMutableData(locRCClient, fwdNet.toBytes());
        handler.addEntry(fwdNet);
    }

    private void processInstructDocMessage(InstructDocumentTransfer instruction) {
        Subscription noitificationName = this.notificationMap.get(instruction.getItemName());
        Publication notificationToApplication = Publication.createPublication(noitificationName.getScopeId(), noitificationName.getRendezvousId(), noitificationName.getContentType(), instruction.toBytes());
        sendRVPAckMessage(instruction.getID(), instruction.getPubtoRVP());
        locRCClient.publish(notificationToApplication);
    }

    private void processInstructChanMessage(InstructChannelTransfer instruction) {
        Subscription notificationName = this.notificationMap.get(instruction.getItemName());
        Publication notificationToApplication = Publication.createPublication(notificationName.getScopeId(), notificationName.getRendezvousId(), notificationName.getContentType(), instruction.toBytes());
        sendRVPAckMessage(instruction.getID(), instruction.getPubtoRVP());
        locRCClient.publish(notificationToApplication);
    }
    
    private void processInstructUnsubscribeMessage(InstructUnsubscribe instruction) {
        Subscription notificationName = this.notificationMap.get(instruction.getItemName());
        Publication notificationToApplication = Publication.createPublication(notificationName.getScopeId(), notificationName.getRendezvousId(), notificationName.getContentType(), instruction.toBytes());
        sendRVPAckMessage(instruction.getID(), instruction.getPubToRVP());
        locRCClient.publish(notificationToApplication);
    }
    
    private void sendRVPAckMessage(int id, ForwardIdentifier pubToRVP) {
        RVPAckMessage message = new RVPAckMessage(id, pubToRVP);
        logger.debug("Sending RVP Ack Message");
        message.publishMutableData(locRCClient, message.toBytes());
    }

    @Override
    /**
     * Intercepts publications publications and subscriptions within the RVA scope.
     * Publications that contain nested subscriptions or publications to be forwarded
     * to the domain-level rendezvous point, include the following in their metadata:
     *
     * <ul>
     * <li> A byte that denotes this a publication from a host's RVA
     * <li> The ByteIdentifier of the current host
     * <li> The nested publication or subscription to be issued in the domain-level rendezvous
     * </ul>
     *
     * @see pubsub.rva.RVAComponentBase for scope IDs and RIDs intercepted.
     */
    public void run() {
        subscribeFor();
        
        handler.start();

        while (!isShutDown()) {
            try {
                Publication pub = locRCClient.receiveNext();

                if (defaultGWFIDwithVLID == null) {
                    gw = TMCInfo.getDefaultGateway(timeOutLocRCClient);

                    if (gw == null) {
                        logger.error("Unknown gateway");
                        continue;
                    }

                    defaultGWFID = new ForwardIdentifier(gw.getLID(), (short) 1);
                    defaultGWFIDwithVLID = new ForwardIdentifier(gw.getLidORVlid(gw.getLID()), (short) 1);
                 //   logger.debug("Received defaultGW");
                }

                if (!pub.getScopeId().equals(RVAUtil.RVA_SID)) {
                    logger.debug("Unknown publication");
                    continue;
                }

                if (pub.getRendezvousId().equals(RVAUtil.RVA_RID)) {
                    MessageType.Type msgType = Message.getMessageType(pub.getByteAt(0));


                    if (msgType == MessageType.Type.RVA_FORWARD_IPC_MESSAGE) {
                        HostAppToRVAIPCMessage appAnnouncement = HostAppToRVAIPCMessage.parseByteBuffer(pub.wrapData());
                        processForwardIPCMessage(appAnnouncement);
                    } else {
                        logger.debug("Unexpected Message");
                    }

                } else if (pub.getRendezvousId().equals(RVAUtil.RVA_COM_RID)) {
                    MessageType.Type msgType = Message.getMessageType(pub.getByteAt(0));
                  //  logger.debug("Received => " + msgType);
                    if (msgType == MessageType.Type.INSTRUCT_DOCUMENT_MESSAGE) {
                        InstructDocumentTransfer instruction = InstructDocumentTransfer.parseByteBuffer(pub.wrapData());
                        processInstructDocMessage(instruction);
                    } else if (msgType == MessageType.Type.INSTRUCT_CHANNEL_MESSAGE) {
                        InstructChannelTransfer instruction = InstructChannelTransfer.parseByteBuffer(pub.wrapData());
                        processInstructChanMessage(instruction);
                    } else if (msgType == MessageType.Type.INSTRUCT_UNSUBSCRIBE_MESSAGE) {
                        InstructUnsubscribe instruction = InstructUnsubscribe.parseByteBuffer(pub.wrapData());
                        processInstructUnsubscribeMessage(instruction);
                    }
                    else if (msgType == MessageType.Type.RVA_ACK_MESSAGE) {
                        RVAAckMessage message = RVAAckMessage.parseByteBuffer(pub.wrapData());
                        handler.removeEntry(message.getAckID());
                    }
                } else {
                    logger.debug("Unexpected publication");
                }

            } catch (InterruptedException e) {
                if (!isShutDown()) {
                    logger.warn("run() interupted without being shut down", e);
                }
            }
        }
    }

    private void addNotification(Subscription itemname,
            Subscription notification) {
        this.notificationMap.put(itemname, notification);
    }
    
    private void close() throws IOException {
        if (!closed) {
            closed = true;
            
            this.shutDown();
            this.interrupt();

            this.handler.shutDown();
            this.handler.interrupt();
            
            super.closeLocRC();
        }
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

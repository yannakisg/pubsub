package pubsub.rva;

import pubsub.messages.net.rva.RVA2RVANetMessage;
import java.io.IOException;
import java.util.Map;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import pubsub.ACKHandler;
import pubsub.ByteIdentifier;
import pubsub.Publication;
import pubsub.Subscription;
import pubsub.bloomfilter.BloomFilter;
import pubsub.ForwardIdentifier;
import pubsub.tmc.TMCInfo;
import pubsub.messages.Message;
import pubsub.messages.MessageType;
import pubsub.messages.net.rva.RVAErrorMessage;
import pubsub.messages.net.rva.RVAProxyInfoMessage;
import pubsub.messages.net.rva.RVARequestProxyInfoMessage;
import pubsub.messages.net.rva.InstructRVAAckMessage;
import pubsub.messages.net.rva.RVAAckMessage;
import pubsub.rva.util.AttachedNodesCache;
import pubsub.tmc.graph.Link;
import pubsub.util.Consumer;

/**
 * 
 * @author xvas
 * @author netharis
 * @author John Gasparis
 */
class RVARouterComponent extends RVAComponentBase {
    
    private static final Logger logger = Logger.getLogger(RVARouterComponent.class);
    private Link proxyRouterLink = null;
    private Link rvpLink = null;
    private AttachedNodesCache rvaCache;
    private ACKHandler handler;
    private boolean closed = false;

    /**
     * The default constructor
     * @throws IOException
     * @throws Exception
     */
    public RVARouterComponent() {
        super();
        rvaCache = new AttachedNodesCache();
        this.handler = new ACKHandler();
        handler.setDaemon(false);

        setName("RVARouterComponent");
        try {
            logger.addAppender(new FileAppender(new PatternLayout("%d [%t] %-5p %c - %m%n"), "rvaRouter.log", false));
        } catch (IOException ex) {
        }
    }

    private void handlePubAnnounce(RVA2RVANetMessage fwdNetMsg) {

        if (fwdNetMsg.getMessageSource() == RVA2RVANetMessage.ForwardMessageSource.GW) {
            
            if (rvpLink == null) {
                rvpLink = TMCInfo.getRVPLink(timeOutLocRCClient);
            }
            
            

            fwdNetMsg.setScopeID(RVARVPComponent.RVP_SID);
            fwdNetMsg.setRendezvousID(RVARVPComponent.RVP_RID);

            ForwardIdentifier fid = new ForwardIdentifier(rvpLink.getLidORVlid(), (short) 1);
            fwdNetMsg.setFID(fid);
            
            logger.debug("Publish to the rvp");
            fwdNetMsg.publishMutableData(locRCClient, fwdNetMsg.toBytes());
        } else if (fwdNetMsg.getMessageSource() == RVA2RVANetMessage.ForwardMessageSource.HOST) {
            fwdNetMsg.setMessageSource(RVA2RVANetMessage.ForwardMessageSource.GW);

            ForwardIdentifier gwToPub = rvaCache.get(fwdNetMsg.getNodeSenderID());

            if (gwToPub == null) {
                gwToPub = TMCInfo.getAttachedHost(timeOutLocRCClient, fwdNetMsg.getNodeSenderID());
                rvaCache.put(fwdNetMsg.getNodeSenderID(), gwToPub);
            }

            if (gwToPub == null) {
                logger.error("Unknown attached host. Wtf ?");
                return;
            }

            sendRVAAckMessage(fwdNetMsg.getID(), gwToPub);

            fwdNetMsg.setGWtoHost(gwToPub);
            fwdNetMsg.setHostID(fwdNetMsg.getNodeSenderID());
            fwdNetMsg.setNodeSenderID(getMyNodeID());

            if (proxyRouterLink.getEndpoint().getID().equals(getMyNodeID())) {
                if (rvpLink == null) {
                    rvpLink = TMCInfo.getRVPLink(timeOutLocRCClient);
                }

                fwdNetMsg.setScopeID(RVARVPComponent.RVP_SID);
                fwdNetMsg.setRendezvousID(RVARVPComponent.RVP_RID);

                ForwardIdentifier fid = new ForwardIdentifier(rvpLink.getLidORVlid(), (short) 1);
                fwdNetMsg.setFID(fid);
                fwdNetMsg.publishMutableData(locRCClient, fwdNetMsg.toBytes());
            } else {
                ForwardIdentifier fid = new ForwardIdentifier(proxyRouterLink.getLidORVlid(), Short.MAX_VALUE);
                fwdNetMsg.setScopeID(RVAUtil.RVA_SID);
                fwdNetMsg.setRendezvousID(RVAUtil.RVA_COM_RID);
                fwdNetMsg.setFID(fid);
                fwdNetMsg.publishMutableData(locRCClient, fwdNetMsg.toBytes());
            }

            handler.addEntry(fwdNetMsg);
        } else {
            logger.error("Unknown Message Source. Wtf ?");
        }
    }

    private void handleSubAnnounce(RVA2RVANetMessage fwdNetMsg) {
        if (fwdNetMsg.getMessageSource() == RVA2RVANetMessage.ForwardMessageSource.GW) {

            if (rvpLink == null) {
                rvpLink = TMCInfo.getRVPLink(timeOutLocRCClient);
            }

            fwdNetMsg.setScopeID(RVARVPComponent.RVP_SID);
            fwdNetMsg.setRendezvousID(RVARVPComponent.RVP_RID);

            ForwardIdentifier fid = new ForwardIdentifier(rvpLink.getLidORVlid(), (short) 1);
            fwdNetMsg.setFID(fid);

            fwdNetMsg.publishMutableData(locRCClient, fwdNetMsg.toBytes());
        } else if (fwdNetMsg.getMessageSource() == RVA2RVANetMessage.ForwardMessageSource.HOST) {
            fwdNetMsg.setMessageSource(RVA2RVANetMessage.ForwardMessageSource.GW);

            ForwardIdentifier gwToSub = rvaCache.get(fwdNetMsg.getNodeSenderID());

            if (gwToSub == null) {
                gwToSub = TMCInfo.getAttachedHost(timeOutLocRCClient, fwdNetMsg.getNodeSenderID());
                rvaCache.put(fwdNetMsg.getNodeSenderID(), gwToSub);
            }

            if (gwToSub == null) {
                logger.error("Unknown attached host. Wtf ?");
                return;
            }

            sendRVAAckMessage(fwdNetMsg.getID(), gwToSub);

            fwdNetMsg.setGWtoHost(gwToSub);
            fwdNetMsg.setHostID(fwdNetMsg.getNodeSenderID());
            fwdNetMsg.setNodeSenderID(getMyNodeID());


            if (proxyRouterLink.getEndpoint().getID().equals(getMyNodeID())) {
                if (rvpLink == null) {
                    rvpLink = TMCInfo.getRVPLink(timeOutLocRCClient);
                }

                fwdNetMsg.setScopeID(RVARVPComponent.RVP_SID);
                fwdNetMsg.setRendezvousID(RVARVPComponent.RVP_RID);

                ForwardIdentifier fid = new ForwardIdentifier(rvpLink.getLidORVlid(), (short) 1);
                fwdNetMsg.setFID(fid);
                fwdNetMsg.publishMutableData(locRCClient, fwdNetMsg.toBytes());
            } else {
                ForwardIdentifier fid = new ForwardIdentifier(proxyRouterLink.getLidORVlid(), Short.MAX_VALUE);
                fwdNetMsg.setScopeID(RVAUtil.RVA_SID);
                fwdNetMsg.setRendezvousID(RVAUtil.RVA_COM_RID);
                fwdNetMsg.setFID(fid);
                fwdNetMsg.publishMutableData(locRCClient, fwdNetMsg.toBytes());
            }
            
            handler.addEntry(fwdNetMsg);
        }
    }

    private void sendRVAAckMessage(InstructRVAAckMessage message) {
        if (getMyNodeID().equals(message.getDestID())) {
            handler.removeEntry(message.getAckID());
        } else {
            ForwardIdentifier fid = TMCInfo.getFID(timeOutLocRCClient, getMyNodeID(), message.getDestID());
            RVAAckMessage rvaAckMsg = new RVAAckMessage(message.getAckID(), fid);
            rvaAckMsg.publishMutableData(locRCClient, rvaAckMsg.toBytes());
        }
    }

    private void sendRVAAckMessage(int id, ForwardIdentifier gwToHost) {
        RVAAckMessage message = new RVAAckMessage(id, gwToHost);
        message.publishMutableData(locRCClient, message.toBytes());
    }

    private void pushProxyPresence(Publication pub) {
        RVARequestProxyInfoMessage message = RVARequestProxyInfoMessage.parseByteBuffer(pub.wrapData());
        ByteIdentifier routerID = message.getProxyInfoID();
        ForwardIdentifier fid = TMCInfo.getFID(timeOutLocRCClient, routerID);

        if (proxyRouterLink == null) {
            RVAErrorMessage errorMsg = new RVAErrorMessage(fid);

            errorMsg.setScopeID(RVAUtil.RVA_SID);
            errorMsg.setRendezvousID(RVAUtil.RVA_PROXY_PUSH_RID);
            errorMsg.publishMutableData(locRCClient, errorMsg.toBytes());
        } else {
            RVAProxyInfoMessage infoMessage = new RVAProxyInfoMessage(proxyRouterLink, fid);

            infoMessage.setScopeID(RVAUtil.RVA_SID);
            infoMessage.setRendezvousID(RVAUtil.RVA_PROXY_PUSH_RID);
            infoMessage.publishMutableData(locRCClient, infoMessage.toBytes());
        }
    }

    private Link pullRVPProxy() {
        Map<ByteIdentifier, ForwardIdentifier> neighbors = TMCInfo.getNeighbors(timeOutLocRCClient);
        BloomFilter bloomFilter = BloomFilter.createZero();
        ForwardIdentifier fid;
        Publication pub;
        RVARequestProxyInfoMessage message;
        RVAProxyInfoMessage infoMessage;

        for (ForwardIdentifier f : neighbors.values()) {
            bloomFilter.or(f.getBloomFilter());
        }

        fid = new ForwardIdentifier(bloomFilter, (short) 1);
        message = new RVARequestProxyInfoMessage(this.getMyNodeID(), fid);

        Subscription sub = Subscription.createSubToMutableData(RVAUtil.RVA_SID, RVAUtil.RVA_PROXY_PUSH_RID);
        Consumer<Publication> consumer = timeOutLocRCClient.subscribeNonBlock(sub);

        message.publishMutableData(locRCClient, message.toBytes());

        for (int i = 0; i < neighbors.size(); i++) {
            try {
                pub = consumer.take();
                infoMessage = RVAProxyInfoMessage.parseByteBuffer(pub.wrapData());

                if (infoMessage.getLink() != null) {
                    timeOutLocRCClient.unsubscribe(sub);
                    return infoMessage.getLink();
                }
            } catch (InterruptedException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        timeOutLocRCClient.unsubscribe(sub);
        return null;
    }

    private boolean getRVPProxy() {
        if (proxyRouterLink == null) {
            proxyRouterLink = TMCInfo.getProxyRouterLink(timeOutLocRCClient);
        } else {
            return true;
        }

        if (proxyRouterLink == null) {
            proxyRouterLink = pullRVPProxy();

            ForwardIdentifier fid = TMCInfo.getFID(timeOutLocRCClient, getMyNodeID(), proxyRouterLink.getEndpoint().getID());
            proxyRouterLink.setLID(fid.getBloomFilter());
        } else {
            return true;
        }

        if (proxyRouterLink == null) {
            logger.debug("Could not find the Proxy Router");
            // send error

            return false;
        } else {
            return true;
        }
    }

    private void subscribeFor() {

        Subscription sub4RVACom = Subscription.createSubToMutableData(RVAUtil.RVA_SID, RVAUtil.RVA_COM_RID);
        locRCClient.subscribe(sub4RVACom);

    }

    @Override
    public void run() {
        this.handler.start();

        proxyRouterLink = TMCInfo.getProxyRouterLink(timeOutLocRCClient);
        if (proxyRouterLink != null) {
            String str = "Proxy Router Link : " + proxyRouterLink.toString() + "\n"
                    + proxyRouterLink.getEndpoint().toString();
            logger.debug(str);
        }

        subscribeFor();

        while (!isShutDown()) {
            try {
                Publication pub = locRCClient.receiveNext();

                if (pub.getScopeId().equals(RVAUtil.RVA_SID)) {
                    if (pub.getRendezvousId().equals(RVAUtil.RVA_COM_RID)) {

                        MessageType.Type msgType = Message.getMessageType(pub.getByteAt(0));

                        if (msgType == MessageType.Type.RVA_FORWARD_NET_MESSAGE) {
                            //logger.debug("received Forward_NET_Message");

                            if (!getRVPProxy()) {
                                logger.debug("I do not know an rvp proxy");
                                continue;
                            }

                            RVA2RVANetMessage netMsg = RVA2RVANetMessage.parseByteBuffer(pub.wrapData());

                            if (netMsg.getRVAAnnouncement().isPublication()) {
                                //logger.debug("Handle PubAnnounce");
                                handlePubAnnounce(netMsg);
                            } else if (netMsg.getRVAAnnouncement().isSubscription()){
                              //  logger.debug("Handle SubAnnounce");
                                handleSubAnnounce(netMsg);
                            } else {
                              // logger.debug("Handle UnSubAnnounce"); 
                               handleSubAnnounce(netMsg);
                            }
                        } else if (msgType == MessageType.Type.RVA_REQUEST_PROXY_INFO_MESSAGE) {
                            if (!getRVPProxy()) {
                                continue;
                            }
                           // logger.debug("handleAnnounceRVPPresence");
                            pushProxyPresence(pub);
                        } else if (msgType == MessageType.Type.RVA_ACK_MESSAGE) {
                           // logger.debug("Received RVA_ACK_MESSAGE");
                            RVAAckMessage message = RVAAckMessage.parseByteBuffer(pub.wrapData());
                            handler.removeEntry(message.getAckID());
                        } else if (msgType == MessageType.Type.INSTRUCT_RVA_ACK_MESSAGE) {
                            InstructRVAAckMessage message = InstructRVAAckMessage.parseByteBuffer(pub.wrapData());
                            sendRVAAckMessage(message);
                        }
                    }
                } else {
                    logger.error("Unknown Publication");
                }
            } catch (InterruptedException e) {
                if (!isShutDown()) {
                    logger.warn(e, e);
                }
            }
        }
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

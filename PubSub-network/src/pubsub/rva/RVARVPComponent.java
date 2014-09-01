package pubsub.rva;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import pubsub.ACKHandler;
import pubsub.ByteIdentifier;
import pubsub.ContentType;
import pubsub.ForwardIdentifier;
import pubsub.PubSubID;
import pubsub.Publication;
import pubsub.Subscription;
import pubsub.bloomfilter.BloomFilter;
import pubsub.invariants.WellKnownIds;
import pubsub.messages.Message;
import pubsub.messages.MessageType;
import pubsub.messages.RVAAnnouncement;
import pubsub.messages.ipc.rvp.RVPProxyRouterAnnouncement;
import pubsub.messages.net.rva.InstructChannelTransfer;
import pubsub.messages.net.rva.InstructDocumentTransfer;
import pubsub.messages.net.rva.InstructRVAAckMessage;
import pubsub.messages.net.rva.InstructUnsubscribe;
import pubsub.messages.net.rva.RVA2RVANetMessage;
import pubsub.messages.net.rvp.RVPAckMessage;
import pubsub.messages.net.tmc.ProxyRouterAnnouncement;
import pubsub.store.NodeEntry;
import pubsub.store.PubSubStore;
import pubsub.store.SteinerPointStore;
import pubsub.tmc.TMCInfo;
import pubsub.tmc.graph.Link;

/**
 *
 * @author John Gasparis
 * @author xvas
 * @author Christos Tsilopoulos
 */
public class RVARVPComponent extends RVAComponentBase {

    private static final Logger logger = Logger.getLogger(RVARVPComponent.class);
    /**
     * A place to store pubs and subs that pass through this router.
     */
    private PubSubStore announcedPublications = new PubSubStore();// TODO replace with DAO
    private PubSubStore pendingSubscriptions = new PubSubStore();// TODO replace with DAO
    private SteinerPointStore steinerPointStore = new SteinerPointStore();
    private Link proxyRouterLink;
    private ACKHandler handler;
    private static ForwardIdentifier fidToProxyRouter = null;
    private boolean closed = false;
    private BloomFilter zero = BloomFilter.createZero();
    /**
     * General purpose SID owned by RVP
     */
    public static final PubSubID RVP_SID = PubSubID.fromHexString(WellKnownIds.RVP.RVP_SID);
    /**
     * General purpose RID owned by RVP
     */
    public static final PubSubID RVP_RID = PubSubID.fromHexString(WellKnownIds.RVP.RVP_RID);
    /**
     * RID to announce (push) the existence of this RVP to its proxy router(s)
     */
    public static final PubSubID RVP_PRESENCE_RID = PubSubID.fromHexString(WellKnownIds.RVP.RVP_PRESENCE_RID);
    private static final long PERIOD = 10000;
    private static final long DELAY = 10000;
    private Timer timer;

    /**
     * Simply constructs the RVP component. Invocation to method startPushing() will
     * start pushing the presence of this RVP to its prospective rvp proxy router(s).
     */
    public RVARVPComponent() {
        handler = new ACKHandler();
        handler.setDaemon(false);

        timer = new Timer();
        setName("RVARVPComponent");
        try {
            logger.addAppender(new FileAppender(new PatternLayout("%d [%t] %-5p %c - %m%n"), "rvp.log", false));
        } catch (IOException ex) {
        }
    }

    private void handlePublicationAnnouncement(RVA2RVANetMessage fwdedNestedPub) {

        /*
         * 1 extract id of publisher
         * 2 update PUBSUBSTORE, i.e, map intercepted nested pub,
         * 3 check for a match
         */

        RVAAnnouncement fwdMessage = fwdedNestedPub.getRVAAnnouncement();

        PubSubID sid = fwdMessage.getSID();
        PubSubID rid = fwdMessage.getRID();
        ContentType cType = fwdMessage.getContentType();
        ByteIdentifier senderID = fwdedNestedPub.getNodeSenderID();
        ByteIdentifier hostID = fwdedNestedPub.getHostID();
        ForwardIdentifier gwToHost = fwdedNestedPub.getGWtoHost();
        ForwardIdentifier hostToGW = fwdedNestedPub.getHostToGW();

        //2
        this.announcedPublications.addEntry(sid, rid, cType, fwdMessage.getRVPAction(), senderID, hostID, gwToHost, hostToGW, fwdMessage.getLifeTime(), fwdMessage.getProcID());

        //3
        Set<NodeEntry> subscribers = this.pendingSubscriptions.findEntries(sid, rid, cType);
        if (subscribers != null) {
            synchronized (subscribers) {
                Iterator<NodeEntry> iterator = subscribers.iterator();
                NodeEntry entry;

                while (iterator.hasNext()) {
                    entry = iterator.next();

                    sendInstructMessage(entry.getProcID(), fwdMessage.getRVPAction(), fwdedNestedPub, entry, RVAAnnouncement.AnnouncementType.PUBLICATION);
                    iterator.remove();
                }
            }
        }
    }

    private void handleUnSubscription(RVA2RVANetMessage fwdedNestedSub) {
        RVAAnnouncement fwdMessage = fwdedNestedSub.getRVAAnnouncement();
        PubSubID sid = fwdMessage.getSID();
        PubSubID rid = fwdMessage.getRID();
        ContentType cType = fwdMessage.getContentType();

        NodeEntry entry = this.announcedPublications.findFirstEntry(sid, rid, cType);

        if (entry != null) {
            sendInstructMessage(fwdMessage.getProcID(), entry.getRvpAction(), fwdedNestedSub, entry, RVAAnnouncement.AnnouncementType.UNSUBSCRIPTION);
        }
    }

    private void handleSubscription(RVA2RVANetMessage fwdedNestedSub) {

        RVAAnnouncement fwdMessage = fwdedNestedSub.getRVAAnnouncement();
        PubSubID sid = fwdMessage.getSID();
        PubSubID rid = fwdMessage.getRID();
        ContentType cType = fwdMessage.getContentType();
        ByteIdentifier senderID = fwdedNestedSub.getNodeSenderID();
        ByteIdentifier hostID = fwdedNestedSub.getHostID();
        ForwardIdentifier gwToHost = fwdedNestedSub.getGWtoHost();
        ForwardIdentifier hostToGW = fwdedNestedSub.getHostToGW();


        NodeEntry entry = this.announcedPublications.findFirstEntry(sid, rid, cType);

        if (entry != null) {
            sendInstructMessage(fwdMessage.getProcID(), entry.getRvpAction(), fwdedNestedSub, entry, RVAAnnouncement.AnnouncementType.SUBSCRIPTION);
        } else {
            this.pendingSubscriptions.addEntry(fwdMessage.getSID(),
                    fwdMessage.getRID(), fwdMessage.getContentType(), fwdMessage.getRVPAction(), senderID, hostID, gwToHost, hostToGW, fwdMessage.getLifeTime(), fwdMessage.getProcID());
        }
    }

    private void announceRVPPresence(Publication pub) {
        if (proxyRouterLink == null) {
            proxyRouterLink = TMCInfo.getProxyRouterLink(timeOutLocRCClient);
        }

        RVPProxyRouterAnnouncement announcement = RVPProxyRouterAnnouncement.parseByteBuffer(pub.wrapData());
        ForwardIdentifier fid = new ForwardIdentifier(proxyRouterLink.getLidORVlid(), (short) 1);
        ProxyRouterAnnouncement message = new ProxyRouterAnnouncement(getMyNodeID(), announcement.getProxyRouterLink(), (short) 0, fid);
        message.publishMutableData(locRCClient, message.toBytes());

        handler.addEntry(message);
    }

    private void sendRVAAckMessage(RVA2RVANetMessage fwdNetMsg) {
        InstructRVAAckMessage message = new InstructRVAAckMessage(fwdNetMsg.getID(), fwdNetMsg.getNodeSenderID(), fidToProxyRouter);
        message.publishMutableData(locRCClient, message.toBytes());
    }

    private void sendInstructMessage(ByteIdentifier subProcID, RVAAnnouncement.RVPAction action, RVA2RVANetMessage fwdedNested, NodeEntry entry, RVAAnnouncement.AnnouncementType annType) {
        RVAAnnouncement annc = fwdedNested.getRVAAnnouncement();
        RVAAnnouncement.RVPAction rvpAction = action;
        Subscription itemName = Subscription.createSubscription(annc.getSID(), annc.getRID(), annc.getContentType());
        ByteIdentifier pubGWid;
        ByteIdentifier subGWid;
        ByteIdentifier subHostID;
        ForwardIdentifier pubGWtoHost;
        ForwardIdentifier pubHostToGW;
        ForwardIdentifier subGWtoHost;
        ForwardIdentifier subHostToGW;
        ForwardIdentifier rvpToPub;
        ForwardIdentifier pubToSub;
        ForwardIdentifier pubToRVP;
        long time;
        long diff;

        if (annType == RVAAnnouncement.AnnouncementType.SUBSCRIPTION || annType == RVAAnnouncement.AnnouncementType.UNSUBSCRIPTION) {
            pubGWid = entry.getGwID();
            subHostID = fwdedNested.getHostID();
            subGWid = fwdedNested.getNodeSenderID();

            pubGWtoHost = entry.getGwToHost();
            pubHostToGW = entry.getHostToGW();

            subGWtoHost = fwdedNested.getGWtoHost();
            subHostToGW = fwdedNested.getHostToGW();
        } else {
            pubGWid = fwdedNested.getNodeSenderID();
            subGWid = entry.getGwID();
            subHostID = entry.getHostID();

            pubGWtoHost = fwdedNested.getGWtoHost();
            pubHostToGW = fwdedNested.getHostToGW();

            subGWtoHost = entry.getGwToHost();
            subHostToGW = entry.getHostToGW();
        }

        if (rvpAction == RVAAnnouncement.RVPAction.CHANNEL_STEINER_TREES) {
            if (annType == RVAAnnouncement.AnnouncementType.UNSUBSCRIPTION) {
                steinerPointStore.remove(annc.getSID(), annc.getRID(), pubGWid, subGWid, subHostID);
            } else {
                steinerPointStore.put(annc.getSID(), annc.getRID(), pubGWid, subGWid, subHostID, subGWtoHost);
            }

            pubToSub = TMCInfo.getSteinerTree(timeOutLocRCClient, steinerPointStore.getSteinerPointsGW(annc.getSID(), annc.getRID(), pubGWid));

            Collection<Set<ByteIdentifier>> hosts = steinerPointStore.getSteinerPointsHost(annc.getSID(), annc.getRID());
            ForwardIdentifier fid;
            short ttl = (short) (pubToSub.getTTL() + 1);

            for (Set<ByteIdentifier> setHost : hosts) {
                for (ByteIdentifier host : setHost) {
                    fid = steinerPointStore.getGwToHost(host);
                    if (fid != null) {
                        pubToSub.addOnlyPath(fid);
                    }
                }
            }
            pubToSub.setTTL(ttl);

            if (!pubToSub.getBloomFilter().equals(zero)) {
                pubToSub.addOnlyPath(pubHostToGW);
            }
        } else {
            time = System.currentTimeMillis();
            pubToSub = TMCInfo.getFID(timeOutLocRCClient, pubGWid, subGWid, false);
            pubToSub.addPath(pubHostToGW, (short) 1);
            pubToSub.addPath(subGWtoHost, (short) 1);
            diff = System.currentTimeMillis() - time;

            logger.debug("Time[PubToSub]: " + diff);
        }

        time = System.currentTimeMillis();
        pubToRVP = TMCInfo.getFID(timeOutLocRCClient, pubGWid, getMyNodeID(), true);
        pubToRVP.addPath(pubHostToGW, (short) 1);
        diff = System.currentTimeMillis() - time;

        logger.debug("Time[PubToRVP]: " + diff);
        time = System.currentTimeMillis();
        rvpToPub = TMCInfo.getFID(timeOutLocRCClient, getMyNodeID(), pubGWid, false);
        rvpToPub.addPath(pubGWtoHost, (short) 1);

        diff = System.currentTimeMillis() - time;

        logger.debug("Time[RVPToPub]: " + diff);
        if (annType == RVAAnnouncement.AnnouncementType.UNSUBSCRIPTION) {
            InstructUnsubscribe msg = new InstructUnsubscribe(subProcID, itemName, pubToRVP, pubToSub, rvpToPub);
            msg.publishMutableData(locRCClient, msg.toBytes());
            handler.addEntry(msg);
        } else if (fwdedNested.getRVAAnnouncement().getContentType() == ContentType.DOCUMENT) {
            ForwardIdentifier subToPub = TMCInfo.getFID(timeOutLocRCClient, subGWid, pubGWid, false);
            subToPub.addPath(subHostToGW, (short) 1);
            subToPub.addPath(pubGWtoHost, (short) 1);

            InstructDocumentTransfer msg = new InstructDocumentTransfer(subProcID, itemName, pubToSub, subToPub, pubToRVP, rvpToPub);
            msg.publishMutableData(locRCClient, msg.toBytes());
            handler.addEntry(msg);
        } else if (fwdedNested.getRVAAnnouncement().getContentType() == ContentType.CHANNEL) {
            InstructChannelTransfer msg = new InstructChannelTransfer(subProcID, itemName, pubToSub, pubToRVP, rvpToPub);
            msg.publishMutableData(locRCClient, msg.toBytes());
            handler.addEntry(msg);
        }
    }

    @Override
    public void run() {

        this.handler.start();

        Subscription rvpPresenceAnnouncement = Subscription.createSubToMutableData(RVARVPComponent.RVP_SID, RVARVPComponent.RVP_PRESENCE_RID);
        locRCClient.subscribe(rvpPresenceAnnouncement);

        Subscription rvpRIDMessages = Subscription.createSubToMutableData(RVARVPComponent.RVP_SID, RVARVPComponent.RVP_RID);
        locRCClient.subscribe(rvpRIDMessages);

        timer.scheduleAtFixedRate(new Worker(PERIOD), DELAY, PERIOD);

        while (!isShutDown()) {
            try {

                Publication pub = locRCClient.receiveNext();
                Subscription receivedItemName = Subscription.createSubToMutableData(pub.getScopeId(), pub.getRendezvousId());

                if (proxyRouterLink == null) {
                    proxyRouterLink = TMCInfo.getProxyRouterLink(timeOutLocRCClient);
                    if (proxyRouterLink != null) {
                        fidToProxyRouter = new ForwardIdentifier(proxyRouterLink.getLidORVlid(), (short) 1);
                    }
                }

                if (receivedItemName.equals(rvpPresenceAnnouncement)) {
                    announceRVPPresence(pub);
                } else if (receivedItemName.equals(rvpRIDMessages)) {
                    MessageType.Type msgType = Message.getMessageType(pub.getByteAt(0));

                    if (msgType == MessageType.Type.RVP_ACK_MESSAGE) {
                        RVPAckMessage message = RVPAckMessage.parseByteBuffer(pub.wrapData());
                        handler.removeEntry(message.getAckID());
                    } else if (msgType == MessageType.Type.RVA_FORWARD_NET_MESSAGE) {
                        RVA2RVANetMessage fwdNetMsg = RVA2RVANetMessage.parseByteBuffer(pub.wrapData());
                        sendRVAAckMessage(fwdNetMsg);
                        if (fwdNetMsg.getRVAAnnouncement().isPublication()) {
                            handlePublicationAnnouncement(fwdNetMsg);
                        } else if (fwdNetMsg.getRVAAnnouncement().isSubscription()) {
                            handleSubscription(fwdNetMsg);
                        } else {
                            handleUnSubscription(fwdNetMsg);
                        }
                    }
                } else {
                    logger.warn("Unexpected RID of publication received: "
                            + pub.getRendezvousId());
                }
            } catch (InterruptedException e) {
                if (!isShutDown()) {
                    logger.warn(e, e);
                }
            }
        }
    }

    private class Worker extends TimerTask {

        private long decreament;
        private ContentType[] cTypes = {ContentType.CHANNEL, ContentType.DOCUMENT};

        public Worker(long decreament) {
            this.decreament = decreament;
        }

        @Override
        public void run() {
            for (int i = 0; i < cTypes.length; i++) {
                announcedPublications.decreamentLifeTime(cTypes[i], decreament);
                pendingSubscriptions.decreamentLifeTime(cTypes[i], decreament);
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
            this.timer.cancel();

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

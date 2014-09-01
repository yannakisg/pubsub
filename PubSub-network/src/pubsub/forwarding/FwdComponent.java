package pubsub.forwarding;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import pubsub.ContentType;
import pubsub.PubSubID;
import pubsub.PubSubServerProcess;
import pubsub.Publication;
import pubsub.RequestHandler;
import pubsub.Subscription;
import pubsub.bloomfilter.BloomFilter;
import pubsub.forwarding.communication.AttachLinkServer;
import pubsub.forwarding.communication.CommunicationLink;
import pubsub.forwarding.communication.PointToPointUDPLink;
import pubsub.invariants.WellKnownIds;
import pubsub.localrendezvous.LocRCClientFactory;
import pubsub.localrendezvous.TimeOutLocRCClient;
import pubsub.messages.net.transport.RequestChunkMessage;
import pubsub.tmc.TMC_Mode;
import pubsub.tmc.TMComponentFactory;
import pubsub.util.Consumer;
import pubsub.util.FwdConfiguration;
import pubsub.util.Pair;
import pubsub.util.StoppableThread;
import pubsub.util.Util;

/**
 * @author tsilo
 * @author John Gasparis
 */
public class FwdComponent extends StoppableThread {

    private static final Logger logger = Logger.getLogger(FwdComponent.class);
    public static final PubSubID FWD_SID = PubSubID.fromHexString(WellKnownIds.Fwd.FWD_SID);
    public static final PubSubID FWD_KEEP_ALIVE = PubSubID.fromHexString(WellKnownIds.Fwd.FWD_KEEP_ALIVE);
    public static final PubSubID FWD_LINK_ESTABLISHMENT = PubSubID.fromHexString(WellKnownIds.Fwd.FWD_LINK_ESTABLISH);
    public static final PubSubID FWD_LINK_DOWN = PubSubID.fromHexString(WellKnownIds.Fwd.FWD_LINK_DOWN);
    public static PubSubID FWD_RID = PubSubID.fromHexString(WellKnownIds.Fwd.FWD_RID);
    public static PubSubID FWD_INFO = PubSubID.fromHexString(WellKnownIds.Fwd.FWD_INFO);
    public static PubSubID FWD_MANAGEMENT = PubSubID.fromHexString(WellKnownIds.Fwd.FWD_MANAGEMENT);
    private final Map<BloomFilter, CommunicationLink> routingTable = new ConcurrentHashMap<BloomFilter, CommunicationLink>();
    private final Map<CommunicationLink, LinkThreadManager> linkManagerTable = new HashMap<CommunicationLink, LinkThreadManager>();
    private TimeOutLocRCClient syncLocRC;
    private PubSubServerProcess fwdInfo;
    private static final BloomFilter VLID = BloomFilter.createRandom(32,
            FwdConfiguration.ZFILTER_BITS_SET);
    private final AtomicInteger commLinks = new AtomicInteger(0);
    private final AttachLinkServer attachLinkServer;
    private final CachingElement packetCache;
    private static final int RQST_MSG_INIT_POS = FwdConfiguration.FID_LENGTH + FwdConfiguration.PUBLICATION_HEADER_LENGTH;
    private static AtomicLong totalBytesRcv = new AtomicLong(0);
    private static AtomicLong totalBytesSent = new AtomicLong(0);
    private CommunicationLink soleLink = null;

    public FwdComponent() throws UnknownHostException, IOException {
        this.setName(this.getClass().getSimpleName());
        attachLinkServer = new AttachLinkServer(this);
        packetCache = CachingElement.createDefault();
        logger.addAppender(new FileAppender(new PatternLayout("%d [%t] %-5p %c - %m%n"), "fwd.log", false));
    }

    public void attach(CommunicationLink communicationLink, double weight) {
        int linkIndex = this.commLinks.incrementAndGet();
        LinkThreadManager mng = new LinkThreadManager(communicationLink,
                linkIndex, this, weight);
        linkManagerTable.put(communicationLink, mng);
        mng.startThreads();
        if (TMComponentFactory.TMC_MODE != TMC_Mode.ROUTER) {
            soleLink = communicationLink;
        }
    }

    @Override
    public void run() {
        this.syncLocRC = LocRCClientFactory.createTimeOutClient("FWD_LocRC");

        // IPC process for Fwd info
        Subscription infoSub = Subscription.createSubToMutableData(FWD_SID, FWD_INFO);
        RequestHandler fwdHandler = new FwdInfoHandler(this);
        this.fwdInfo = new PubSubServerProcess(syncLocRC, infoSub, fwdHandler);
        String name = this.getClass().getSimpleName() + "/" + "FwdInfoServer";
        this.fwdInfo.setName(name);

        // logger.debug("starting Fwd info thread");
        this.fwdInfo.start();

        // logger.debug("starting attach link server");
        this.attachLinkServer.start();

        Subscription s = Subscription.createSubToMutableData(FwdComponent.FWD_SID,
                FwdComponent.FWD_RID);

        Consumer<Publication> queue = this.syncLocRC.subscribeNonBlock(s);
        Publication pub;
        ByteBuffer buffer;
        ContentType cType;
        double flip;

        Pair<byte[], Integer> pair;
        byte[] data;
        while (!isShutDown()) {
            try {
                pub = queue.take();
                if (pub == null) {
                    continue;
                }

                /* fp = FwdStruct.parseByteBuffer(pub.wrapData());
                Publication nested = fp.getPublication();
                
                cType = nested.getContentType();
                
                if (cType == ContentType.REQUEST_IMMUTABLE_DATA) {
                // bla bla
                } else if (cType == ContentType.IMMUTABLE_DATA) {
                flip = Util.getRandomDouble();
                if (flip < CachingElement.CACHE_PROBABILITY) {
                //store
                }
                }*/
// forward(fp, pub, null);
                buffer = pub.wrapData();
                int prevPos = buffer.position();

                cType = getContentType(buffer);

                if (cType == ContentType.REQUEST_IMMUTABLE_DATA) {
                    buffer.position(buffer.position() + RQST_MSG_INIT_POS);
                    if (handleRequestMsg(buffer)) {
                        continue;
                    }
                } else if (cType == ContentType.IMMUTABLE_DATA) {
                    flip = Util.getRandomDouble();

                    if (flip < CachingElement.CACHE_PROBABILITY) {
                        buffer.position(buffer.position() + FwdConfiguration.FID_LENGTH);
                        store(buffer);
                    }
                }

                buffer.position(prevPos);

                data = pub.toBytes();

                if (data == null || data.length == 0) {
                    continue;
                }
                if (soleLink == null) {
                    pair = new Pair<byte[], Integer>(data, data.length);
                    forward(pub, pair);

                } else {
                    soleLink.transmitDirectly(data, data.length);
               }

            } catch (InterruptedException e) {
                if (!isShutDown()) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
                logger.debug(e.getMessage());
            }
        }
        this.syncLocRC.close();
    }

    /* public void handleIncoming(FwdStruct fp, Publication receivedPub, CommunicationLink incomingLink) throws Exception {
    /* 1 if request
     * 1.1 lookup in cache and transmit
     * 2 if data, cache and forward
     *
    Publication nested = fp.getPublication();
    BloomFilter lFID = fp.getFid().getBloomFilter();
    
    if (nested.getContentType() == ContentType.REQUEST_IMMUTABLE_DATA) {
    RequestChunkMessage mesg = RequestChunkMessage.parseByteBuffer(nested.wrapData());
    
    Publication chunk = this.packetCache.seek(mesg.getAppSID(), mesg.getAppRID(), mesg.getChunkNum());
    if (chunk == null) {
    if (BloomFilter.ANDf(VLID, lFID)) {
    this.syncLocRC.publish(nested);
    }
    
    forward(fp, receivedPub, incomingLink, false);
    return;
    } else {
    logger.debug("extract reverse path, forward and exit");
    FwdStruct fd = new FwdStruct(mesg.getPubToSub(), chunk);
    forward(fd, receivedPub, null, true);
    return;
    }
    } else if (nested.getContentType() == ContentType.IMMUTABLE_DATA) {
    this.packetCache.store(nested);
    } /*else if (nested.getContentType() == ContentType.CHANNEL) {
    logger.debug("Received [FwdComponent] => " + countRcvdChannelPackets);
    countRcvdChannelPackets++;
    }*
    
    forward(fp, receivedPub, incomingLink);
    
    // deliver to local host?
    if (BloomFilter.ANDf(VLID, lFID)) {
    this.syncLocRC.publish(nested);
    }
    }*/

    /*private void forward(FwdStruct fp, Publication sentPub, CommunicationLink excludeLink) throws Exception {
    CommunicationLink link;
    BloomFilter linkId;
    BloomFilter lFID = fp.getFid().getBloomFilter();
    
    
    synchronized (routingTable) {
    if (soleLink == null) {
    for (Entry<BloomFilter, CommunicationLink> entry : routingTable.entrySet()) {
    linkId = entry.getKey();
    link = entry.getValue();
    if (excludeLink != null && link.equals(excludeLink)) {
    continue;
    }
    if (BloomFilter.ANDf(linkId, lFID)) {
    boolean ret = link.transmit(sentPub);
    //logger.debug("Transmit to: " + link + "[" + ret + "]");
    
    }
    }
    }
    }
    }*/
    
    
    private void forward(Publication pub, Pair<byte[], Integer> pair) {
        BloomFilter linkId;
        CommunicationLink link;
        byte[] blFID = new byte[FwdConfiguration.ZFILTER_LENGTH];
        ByteBuffer buf = pub.wrapData();
        int prevPos = buf.position();
        buf.position(buf.position() + Util.SIZEOF_SHORT);
        buf.get(blFID);
        buf.position(prevPos);
        
        Set<Entry<BloomFilter, CommunicationLink>> entrySet = routingTable.entrySet();
        synchronized (routingTable) {
            Iterator<Entry<BloomFilter, CommunicationLink>> iter = entrySet.iterator();
            Entry<BloomFilter, CommunicationLink> entry;
            while (iter.hasNext()) {
                entry = iter.next();
                linkId = entry.getKey();
                link = entry.getValue();
                if (deliver(linkId, blFID, 0)) {
                    if (!link.transmit(pair)) {
                        logger.debug("The packet will not be sent");
                    }
                }
            }
        }
    }

    private ContentType getContentType(ByteBuffer buffer) {
        return ContentType.getType(buffer.get(buffer.position() + FwdConfiguration.FID_LENGTH));
    }

    private boolean handleRequestMsg(ByteBuffer buffer) {
        RequestChunkMessage msg = RequestChunkMessage.parseByteBuffer(buffer);
        Publication chunk = this.packetCache.seek(msg.getAppSID(), msg.getAppRID(), msg.getChunkNum());

        if (chunk != null) {
            this.syncLocRC.publish(chunk);
            return true;
        } else {
            return false;
        }
    }

    public void store(ByteBuffer buffer) {
        Publication nested = Publication.parseByteBuffer(buffer);
        this.packetCache.store(nested);
    }

    public void handleRequestMsg(ByteBuffer buffer, byte[] rcvData, int length, CommunicationLink incomingLink) {
        RequestChunkMessage msg = RequestChunkMessage.parseByteBuffer(buffer);
        Publication chunk = this.packetCache.seek(msg.getAppSID(), msg.getAppRID(), msg.getChunkNum());

        if (chunk == null) {
            forward(buffer, rcvData, length, incomingLink);
        } else {
            // logger.debug("extract reverse path, forward and exit");
            FwdStruct fd = new FwdStruct(msg.getPubToSub(), chunk);
            forward(fd, incomingLink);
        }
    }

    public void forward(ByteBuffer buffer, byte[] data, int length, CommunicationLink excludeLink) {
        BloomFilter linkId;
        CommunicationLink link;        
        byte[] sentData = buffer.array();
        Pair<byte[], Integer> pair = new Pair<byte[], Integer>(sentData, length);
        
        Set<Entry<BloomFilter, CommunicationLink>> entrySet = routingTable.entrySet();
        synchronized (routingTable) {
            Iterator<Entry<BloomFilter, CommunicationLink>> iter = entrySet.iterator();
            Entry<BloomFilter, CommunicationLink> entry;
            while (iter.hasNext()) {
                entry = iter.next();
                linkId = entry.getKey();
                link = entry.getValue();
                if (excludeLink != null && link.equals(excludeLink)) {
                    continue;
                }
                if (deliver(linkId, data, FwdConfiguration.FID_BL_INIT_POS)) {
                    if (!link.transmit(pair)) {
                        logger.debug("The packet will not be sent");
                    }
                }
            }
        }

        deliverLocal(buffer, data);
    }

    private void forward(FwdStruct fd, CommunicationLink excludeLink) {
        BloomFilter lFID = fd.getFid().getBloomFilter();
        byte[] data = Publication.createMutableData(FWD_SID, FWD_RID, fd.toBytes()).toBytes();
        Pair<byte[], Integer> pair = new Pair<byte[], Integer>(data, data.length);
        BloomFilter linkId;
        CommunicationLink link;

        synchronized (routingTable) {
            for (Entry<BloomFilter, CommunicationLink> entry : routingTable.entrySet()) {
                linkId = entry.getKey();
                link = entry.getValue();
                if (excludeLink != null && link.equals(excludeLink)) {
                    continue;
                }
                if (BloomFilter.ANDf(linkId, lFID)) {
                    link.transmit(pair);
                }
            }
        }
    }

    private void deliverLocal(ByteBuffer buffer, byte[] rcvData) {
        try {
            if (deliver(VLID, rcvData, FwdConfiguration.FID_BL_INIT_POS)) {
                buffer.position(FwdConfiguration.NESTEDPUB_INIT_POS);
                Publication nested = Publication.parseByteBuffer(buffer);
                this.syncLocRC.publish(nested);
            }
        } catch (BufferUnderflowException ex) {
            logger.debug(ex.getMessage() + " Continue...");
        }
    }

    private boolean deliver(BloomFilter bl, byte[] rcvData, int initPos) {
        byte[] vlid = bl.getBytes();
        int length = initPos + FwdConfiguration.ZFILTER_LENGTH;
        int i, j;
        byte value;

        for (i = initPos, j = 0; i < length; i++, j++) {
            value = (byte) (vlid[j] & rcvData[i]);
            if (value != vlid[j]) {
                return false;
            }
        }

        return true;
    }

    /* 
     * TO BE REVIEWED
     * if (fp.getPublication().getContentType() == ContentType.IMMUTABLE_DATA && (!cached)) {
    ByteBuffer buffer = fp.getPublication().wrapData();
    logger.debug("Previous HopCount -> " + DataMessage.getHopCount(buffer));
    DataMessage.incrementHopCount(buffer);
    logger.debug("Next HopCount -> " + DataMessage.getHopCount(buffer));
    }*/
    public void handleKeepAlive(KeepAliveMessage mesg, CommunicationLink link) {
        linkManagerTable.get(link).handleKeepAlive(mesg);
    }

    private BloomFilter findLinkID(CommunicationLink link) {
        BloomFilter zf = null;
        for (BloomFilter fid : routingTable.keySet()) {
            if (routingTable.get(fid).equals(link)) {
                zf = fid;
                break;
            }
        }
        return zf;
    }

    public void publishLinkEstablishment(CommunicationLink link,
            BloomFilter neighborVLID, double weight) {
        BloomFilter lid = findLinkID(link);
        if (lid == null) {
            lid = BloomFilter.createRandom(32, FwdConfiguration.ZFILTER_BITS_SET);
            routingTable.put(lid, link);
        }
        LinkEstablishAnnouncement lea = new LinkEstablishAnnouncement(lid,
                neighborVLID, weight);
        Publication listEstablishment = Publication.createMutableData(FWD_SID,
                FWD_LINK_ESTABLISHMENT, lea.toBytes());
        this.syncLocRC.publish(listEstablishment);
    }

    public void publishLinkDown(CommunicationLink link) {
        BloomFilter lid = findLinkID(link);
        LinkDownAnnouncement lda = new LinkDownAnnouncement(lid);
        Publication pub = Publication.createMutableData(FWD_SID, FWD_LINK_DOWN, lda.toByteArray());
        this.syncLocRC.publish(pub);
    }

    public List<String> listAllLinks() {
        List<String> list = new ArrayList<String>(routingTable.size());
        synchronized (linkManagerTable) {
            for (CommunicationLink link : linkManagerTable.keySet()) {
                list.add(link.toString());
            }
        }
        return list;
    }

    public BloomFilter getVLID() {
        return VLID;
    }

    public int getMTU() {
        return PointToPointUDPLink.MTU;
    }

    public static void addRcvBytes(long bytes) {
        totalBytesRcv.addAndGet(bytes);
        try {
            logger.debug("[Received] " + totalBytesRcv + " bytes");
        } catch (java.lang.OutOfMemoryError ex) {
            System.out.println("[Received] " + totalBytesRcv + " bytes");
        }
    }

    public static void addSentBytes(long bytes) {
        ;
        try {
            logger.debug("[Sent] " + totalBytesSent.addAndGet(bytes) + " bytes");
        } catch (java.lang.OutOfMemoryError ex) {
            System.out.println("[Sent] " + totalBytesSent.addAndGet(bytes) + " bytes");
        }
    }
}

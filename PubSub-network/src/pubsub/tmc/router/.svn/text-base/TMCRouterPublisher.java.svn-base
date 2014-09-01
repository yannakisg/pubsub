package pubsub.tmc.router;

import java.nio.ByteBuffer;
import java.util.List;
import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.bloomfilter.BloomFilter;
import pubsub.localrendezvous.LocRCClient;
import pubsub.messages.MessageType;
import pubsub.messages.ipc.DebugTopologyMessage;
import pubsub.messages.ipc.IPCTMCInterestMessage;
import pubsub.messages.ipc.tmc.FIDMessage;
import pubsub.messages.ipc.tmc.GetProxyRouterMessage;
import pubsub.messages.ipc.tmc.GetRVPMessage;
import pubsub.messages.ipc.tmc.LIDMessage;
import pubsub.messages.ipc.tmc.NeighborsMessage;
import pubsub.messages.ipc.tmc.NodeIDMessage;
import pubsub.messages.net.TMCAckMessage;
import pubsub.messages.net.rvp.RVPAckMessage;
import pubsub.messages.net.tmc.InterestTopologyChunk;
import pubsub.messages.net.tmc.LinkStateAdvertisement;
import pubsub.messages.net.tmc.ProxyRouterAnnouncement;
import pubsub.messages.net.tmc.TMCNetMessage;
import pubsub.tmc.TMCPublisher;
import pubsub.tmc.graph.Link;
import pubsub.tmc.graph.MyRouterNode;
import pubsub.util.ProducerConsumerQueue;
import pubsub.util.StoppableThread;

/**
 *
 * @author John Gasparis
 */
public class TMCRouterPublisher extends TMCPublisher {

    private MyRouterNode myRouterNode;
    private static ForwardIdentifier rvpFID = null;
    private Worker worker;

    public TMCRouterPublisher(MyRouterNode myNode, LocRCClient locRCClient) {
        super();
        this.locRCClient = locRCClient;
        this.myRouterNode = myNode;
        this.myNode = myNode;
    }

    public void publishLinkDownMsg(ByteIdentifier headerID, BloomFilter removedLID, ByteIdentifier srcNodeID) {
        /*int[] totalLinks = new int[1];
        BloomFilter lid = getConnectedLinksExceptFor(headerID, totalLinks);
        
        if (lid == null) {
        return;
        }
        ForwardIdentifier fid = new ForwardIdentifier(lid, (short) 1);
        LinkDownMessage ldm = new LinkDownMessage(headerID, srcNodeID, removedLID, fid);
        
        ldm.publishMutableData(locRCClient, ldm.toBytes());
        
        logger.debug("Published LinkDown Message");
        
        for (int i = 0; i < totalLinks[0]; i++) {
        handler.addEntry(ldm);
        }*/
    }

    public void publishDebugTopologyMsg(MessageType.Type msgType) {
        DebugTopologyMessage message = new DebugTopologyMessage(msgType, myRouterNode.getWeightedAdjacencyMap());
        message.publishMutableData(locRCClient, message.toBytes());

     //   logger.debug("Published Topology Message");
    }

    public void publishProxyRouterAnnouncement(ProxyRouterAnnouncement message) {
        ByteIdentifier routerID = message.getRouterID();
        message.setRouterID(myRouterNode.getID());

        publishToConnectedLinksExceptFor(message, routerID);

      //  logger.debug("Published Proxy Router Announcement");
    }

    public void publishRVPAckMsg(TMCNetMessage rcvdMsg, Link rvpLink) {
        if (rvpFID == null) {
            rvpFID = new ForwardIdentifier(rvpLink.getLidORVlid(), (short) 1);
        }
        RVPAckMessage message = new RVPAckMessage(rcvdMsg.getID(), rvpFID);

        message.publishMutableData(locRCClient, message.toBytes());
       // logger.debug("Published RVP ACK [" + rcvdMsg.getID() + "]");
    }

    public void publishTMCAckMsg(int id, ByteIdentifier routerID) {
        Link link = this.myRouterNode.getLink(myRouterNode.getID(), routerID);

        if (link == null || link.getLID() == null || link.getEndpoint().getVLID() == null) {
            logger.debug("I don't know the outgoing link for: " + routerID);
            return;
        }

        ForwardIdentifier fid = new ForwardIdentifier(link.getLidORVlid(), (short) 1);
        TMCAckMessage message = new TMCAckMessage(id, fid);

        message.publishMutableData(locRCClient, message.toBytes());
       // logger.debug("Published TMC ACK [" + id + "]");
    }

    public void publishTMCAckMsg(TMCNetMessage rcvdMsg, ByteIdentifier routerID) {
        publishTMCAckMsg(rcvdMsg.getID(), routerID);
    }

    public void processTMCAckMessage(ByteBuffer buff) {
        TMCAckMessage message = TMCAckMessage.parseByteBuffer(buff);
       // logger.debug("processTMCAckMessage " + message.getAckID());
        this.handler.removeEntry(message.getAckID());
    }

    public void publishNodeIDMessage() {
        NodeIDMessage message = NodeIDMessage.getInstance(myRouterNode.getID());

       // logger.debug("Publish NodeIDMessage");
        message.publishMutableData(locRCClient, message.toBytes());
    }

    public void publishNeighborsMessage() {
        NeighborsMessage message = new NeighborsMessage(myRouterNode.getNeighbors(myRouterNode.getID()));

        message.publishMutableData(locRCClient, message.toBytes());
      //  logger.debug("Publish NeighborsMessage");
    }

    public void publishLIDMessage(IPCTMCInterestMessage interest) {
        ByteIdentifier id = interest.getIDA();
        if (id == null) {
            return;
        }

        BloomFilter lid = myRouterNode.getLID(id);

        LIDMessage message = new LIDMessage(lid);

      //  logger.debug("Publish LIDMessage");
        message.publishMutableData(locRCClient, message.toBytes());
    }

    public void publishFIDMessage(MessageType.Type msgType, IPCTMCInterestMessage interest) {
        FIDMessage message;

        if (interest.getIDA() == null && interest.getIDB() == null) {
            return;
        } else {
            if (msgType == MessageType.Type.GET_FID_A_B) {
                BloomFilter lid;
                short[] ttl = new short[1];

                lid = myRouterNode.getPath(interest.getIDA(), interest.getIDB(), ttl, interest.includeDest());

                message = new FIDMessage(msgType, new ForwardIdentifier(lid, ttl[0]));
            } else if (msgType == MessageType.Type.GET_FID_HOST) {
                BloomFilter lid;
                short ttl = 1;

                lid = myRouterNode.getHostLink(interest.getIDA());

                message = new FIDMessage(msgType, new ForwardIdentifier(lid, ttl));
            } else if (msgType == MessageType.Type.GET_FID) {
                BloomFilter lid;
                short[] ttl = new short[1];

                lid = myRouterNode.getPath(myRouterNode.getID(), interest.getIDA(), ttl, interest.includeDest());


                message = new FIDMessage(msgType, new ForwardIdentifier(lid, ttl[0]));
            } else {
                return;
            }
        }

      //  logger.debug("Publish FIDMessage");
        message.publishMutableData(locRCClient, message.toBytes());
    }

    public void publishGetProxyRouterMessage() {
       // logger.debug("publish GetProxyRouterMessage");

        GetProxyRouterMessage message = new GetProxyRouterMessage(myRouterNode.getProxyRouterLink());
        message.publishMutableData(locRCClient, message.toBytes());
    }

    public void publishGetRVPMessage() {
       // logger.debug("publish GetRVPMessage");

        GetRVPMessage message = new GetRVPMessage(myRouterNode.getRVPLink());
        message.publishMutableData(locRCClient, message.toBytes());
    }

    public void publishTopologyToRVP(TopologySender sender) {
        if (rvpFID == null) {
            return;
        }

       // logger.debug("Publish Topology to RVP");

        sender.setProxyToRVP(rvpFID);
        sender.wakeUp();
    }

    public void publishTopologyChunk(InterestTopologyChunk msg, TopologySender sender) {
      //  logger.debug("Publish topology chunk [" + msg.getChunkNum() + "]");
        sender.sendChunk(msg.getChunkNum());
    }

    private void publishToConnectedLinksExceptFor(TMCNetMessage msg, ByteIdentifier receivedFrom) {
        List<Link> links = myRouterNode.getNeighborsExceptFor(receivedFrom);
        BloomFilter bl;
        ForwardIdentifier fid;

        if (links == null || links.isEmpty()) {
            return;
        }

        for (Link link : links) {
            bl = link.getLidORVlid();
            fid = new ForwardIdentifier(bl, (short) 1);

            msg.setFID(fid);
            msg.computeNewID();
            msg.publishMutableData(locRCClient, msg.toBytes());
        //    logger.debug("AddToHandler[" + msg.getID() + "] => " + msg.getMessageType());
            handler.addEntry(msg);
        }
    }

    public void publishLSAMessage(short ttl) {
        Link proxyLink = myRouterNode.getProxyRouterLink();
        ForwardIdentifier proxyFID = new ForwardIdentifier(proxyLink.getLidORVlid(), ttl);
        LinkStateAdvertisement lsa = new LinkStateAdvertisement(myRouterNode, proxyFID);

     //   logger.debug("Publish LSA Message to RVP Proxy");
        lsa.publishMutableData(locRCClient, lsa.toBytes());
        LinkStateAdvertisement.incrementCounter();
        
    //    logger.debug("AddToHandler[" + lsa.getID() + "] => " + lsa.getMessageType());
        handler.addEntry(lsa, 10000);
    }

    public void putTMCAckMessage(ByteIdentifier routerID, int id) throws InterruptedException {
        if (worker == null) {
            worker = new Worker();
            worker.start();
        }

        worker.put(routerID, id);
    }

    private class Worker extends StoppableThread {

        private ProducerConsumerQueue<Entry> pendingACKMessages;

        public Worker() {
            pendingACKMessages = new ProducerConsumerQueue<Entry>();
        }

        public void put(ByteIdentifier routerID, int id) throws InterruptedException {
            Entry entry = new Entry(routerID, id);
            pendingACKMessages.getProduder().put(entry);
        }

        @Override
        public void run() {
            BloomFilter zero = BloomFilter.createZero();
            Entry entry;
            TMCAckMessage ackMsg;
            short[] ttl = new short[1];
            BloomFilter path;
            ForwardIdentifier fid;

            while (!isShutDown()) {
                try {
                    entry = pendingACKMessages.getConsumer().take();
                    path = myRouterNode.getPath(myRouterNode.getID(), entry.getRouterID(), ttl, true);

                    if (path.equals(zero)) {
                        logger.debug("Unknown path");
                        pendingACKMessages.getProduder().put(entry);
                    } else {
                        fid = new ForwardIdentifier(path, ttl[0]);
                        ackMsg = new TMCAckMessage(entry.getID(), fid);

                        logger.debug("Publish ACK Message: " + ackMsg.getAckID());
                        ackMsg.publishMutableData(locRCClient, ackMsg.toBytes());
                    }
                } catch (InterruptedException ex) {
                    logger.error(ex.getMessage(), ex);
                } catch (NullPointerException ex) {
                    logger.debug("Propably unknown path...Waiting");
                }
            }
        }
    }

    private class Entry {

        private ByteIdentifier routerID;
        private int id;

        public Entry(ByteIdentifier routerID, int id) {
            this.routerID = routerID;
            this.id = id;
        }

        public ByteIdentifier getRouterID() {
            return this.routerID;
        }

        public int getID() {
            return this.id;
        }
    }
}

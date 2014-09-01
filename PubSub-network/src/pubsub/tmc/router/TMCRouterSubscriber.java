package pubsub.tmc.router;

import java.io.IOException;
import java.nio.ByteBuffer;

import pubsub.ByteIdentifier;
import pubsub.Publication;
import pubsub.bloomfilter.BloomFilter;
import pubsub.forwarding.LinkDownAnnouncement;
import pubsub.forwarding.LinkEstablishAnnouncement;
import pubsub.localrendezvous.LocRCClient;
import pubsub.messages.Message;
import pubsub.messages.MessageType;
import pubsub.messages.ipc.IPCTMCInterestMessage;
import pubsub.messages.net.NetMessage;
import pubsub.messages.net.tmc.HelloMessage;
import pubsub.messages.net.tmc.InterestTopologyChunk;
import pubsub.messages.net.tmc.LinkDownMessage;
import pubsub.messages.net.tmc.LinkStateAdvertisement;
import pubsub.messages.net.tmc.ProxyRouterAnnouncement;
import pubsub.messages.net.tmc.TMCNetMessage;
import pubsub.tmc.TMCSubscriber;
import pubsub.tmc.TMC_Mode;
import pubsub.tmc.graph.GatewayNode;
import pubsub.tmc.graph.Link;
import pubsub.tmc.graph.MyRouterNode;
import pubsub.tmc.graph.NeighborNode;

/**
 *
 * @author John Gasparis
 */
public class TMCRouterSubscriber extends TMCSubscriber {

    private TMCRouterPublisher publisher;
    private MyRouterNode myNode;
    private GatewayNode gatewayNode;
    private NeighborNode rvpNode;
    private TopologySender sender;
    private LSAHistory lsaHistory;
    private boolean hasPublishedLSAToProxy = false;

    public TMCRouterSubscriber(MyRouterNode myNode, LocRCClient locRCClient) {
        this.myNode = myNode;
        this.lsaHistory = new LSAHistory();
        this.publisher = new TMCRouterPublisher(myNode, locRCClient);
        this.gatewayNode = new GatewayNode(myNode.getVLID(), myNode.getID());
        this.rvpNode = null;
        this.sender = new TopologySender(myNode.getWeightedAdjacencyMap(), myNode.getID(), locRCClient);
        this.sender.start();
    }

    @Override
    protected void processEstablishedLink(Publication pub) {
        LinkEstablishAnnouncement lea = LinkEstablishAnnouncement.parseByteBuffer(pub.wrapData());

        myNode.addLink(myNode.getID(), lea.getVLID(), lea.getLID(), lea.getWeight());

        publisher.publishHelloMsg(lea);
        if (rvpNode != null && rvpNode.getVLID().equals(lea.getVLID())) {
            myNode.setRVPLink(myNode.getLink(myNode.getID(), rvpNode.getID()));
        }
    }

    @Override
    protected void processLinkDown(Publication pub) {
        LinkDownAnnouncement lda = LinkDownAnnouncement.parseByteBuffer(pub.wrapData());

     //   logger.debug("LID " + lda.getLID().toBinaryString());

        ByteIdentifier deadNodeID = myNode.removeLink(myNode.getID(), lda.getLID());

        if (deadNodeID != null) {
            publisher.publishLinkDownMsg(myNode.getID(), lda.getLID(), myNode.getID());
        }
    }

    @Override
    protected void processTMCPublication(Publication pub) throws IOException {
        MessageType.Type msgType;

       // logger.debug("Received " + pub.getDataLength() + " bytes");

        msgType = Message.getMessageType(pub.getByteAt(0));

      //  logger.debug(msgType + " MESSAGE");

        if (msgType == MessageType.Type.HELLO) {
            processHelloMsg(pub.wrapData());
        } else if (myNode.getRVPLink() != null && msgType == MessageType.Type.LSA) {
            processLSAMsg(pub.getDataArray());
        } else if (msgType == MessageType.Type.DEBUG_TOPOLOGY || msgType == MessageType.Type.GET_HOSTS) {
            publisher.publishDebugTopologyMsg(msgType);
        } else if (msgType == MessageType.Type.GET_MYNODE_ID) {
            publisher.publishNodeIDMessage();
        } else if (msgType == MessageType.Type.GET_NEIGHBORS) {
            publisher.publishNeighborsMessage();
        } else if (msgType == MessageType.Type.GET_FID || msgType == MessageType.Type.GET_FID_A_B || msgType == MessageType.Type.GET_FID_HOST) {
            IPCTMCInterestMessage interest = IPCTMCInterestMessage.parseByteBuffer(pub.wrapData());
            publisher.publishFIDMessage(msgType, interest);
        } else if (msgType == MessageType.Type.GET_LID) {
            IPCTMCInterestMessage interest = IPCTMCInterestMessage.parseByteBuffer(pub.wrapData());
            publisher.publishLIDMessage(interest);
        } else if (msgType == MessageType.Type.GET_DEFAULT_GW) {
            publisher.publishDefaultGateway(gatewayNode);
        } else if (msgType == MessageType.Type.LINK_DOWN) {
            LinkDownMessage message;

            message = LinkDownMessage.createNew(pub.wrapData());

            myNode.removeLink(message.getSrcNodeID(), message.getRemovedLID());

            publisher.publishLinkDownMsg(myNode.getID(), message.getRemovedLID(), message.getSrcNodeID());
            publisher.publishTMCAckMsg(message, message.getRouterID());
        } else if (msgType == MessageType.Type.NET_LSA_PROXY_ROUTER) {
            processLSAProxyRouter(pub.wrapData());
        } else if (msgType == MessageType.Type.GET_PROXY_ROUTER) {
            publisher.publishGetProxyRouterMessage();

        } else if (msgType == MessageType.Type.TOPOLOGY_INTEREST_CHUNK) {
            InterestTopologyChunk msg = InterestTopologyChunk.parseByteBuffer(pub.wrapData());
            publisher.publishTopologyChunk(msg, sender);
        } else if (msgType == MessageType.Type.GET_RVP) {
            publisher.publishGetRVPMessage();
        } else if (msgType == MessageType.Type.TMC_ACK) {
            publisher.processTMCAckMessage(pub.wrapData());
        } else {
            ByteBuffer buffer = pub.wrapData();
            publisher.publishTMCAckMsg(NetMessage.findID(buffer), TMCNetMessage.findRouterID(buffer));
        }

        if (msgType == MessageType.Type.HELLO) {
            myNode.debugTopology();
        }
    }

    private void processHelloMsg(ByteBuffer buff) {
        HelloMessage message;
        ByteIdentifier routerID;

        message = HelloMessage.parseByteBuffer(buff);

        routerID = message.getRouterID();

        if (hellomsgCache.contains(message.getVLID())) {
            publisher.publishTMCAckMsg(message, routerID);
            return;
        }

        hasPublishedLSAToProxy = false;
       // logger.debug("Router ID: " + routerID.toString());

        myNode.updateTopology(myNode.getID(), routerID, message.getVLID(), message.getType());

        publisher.publishTMCAckMsg(message, routerID);

        if (message.getType() == TMC_Mode.RVP) {
          //  logger.debug("Received HELLO Message from RVP");
            myNode.setRVPLink(myNode.getLink(myNode.getID(), routerID));
            rvpNode = new NeighborNode(routerID, message.getVLID(), TMC_Mode.RVP);
            myNode.getRVPLink().setEndPoint(rvpNode);
        }

        hellomsgCache.add(message.getVLID());
    }

    private void processLSAMsg(byte[] data) {
        LinkStateAdvertisement message;
        ByteIdentifier routerID;

        message = LinkStateAdvertisement.createNew(myNode, data);

        routerID = message.getRouterID();
       // logger.debug("Router ID: " + routerID.toString());
        try {
            publisher.putTMCAckMessage(routerID, message.getID());
        } catch (InterruptedException ex) {
            try {
                publisher.putTMCAckMessage(routerID, message.getID());
            } catch (InterruptedException ex1) {
                logger.debug("InterruptedException while adding a pending TMCAck message");
            }
        }

        if (lsaHistory.isNewer(routerID, message.getCurrentSeqNum())) {
            publisher.publishTopologyToRVP(sender);
        }
    }

    private void processLSAProxyRouter(ByteBuffer buff) {
        ProxyRouterAnnouncement message;
        Link link;
        Link proxyRouterLink;
        ByteIdentifier routerID;
        boolean publishToNeighbors = false;
        boolean publishLSAToProxy = false;

        message = ProxyRouterAnnouncement.parseByteBuffer(buff);
        routerID = message.getRouterID();

        link = myNode.getLink(myNode.getID(), routerID);
        proxyRouterLink = message.getProxyRouterLink();

        if (link == null) {
          //  logger.debug("I don't know the outgoing link for: " + routerID);
        }

        if ((proxyRouterLink.getEndpoint() != null) && (!proxyRouterLink.getEndpoint().getID().equals(myNode.getID()))) {
            proxyRouterLink.setLID(BloomFilter.OR(proxyRouterLink.getLID(), link.getLID()));
            proxyRouterLink.setCost(proxyRouterLink.getCost() + link.getCost());
        } else {
            if (myNode.getRVPLink() == null || (myNode.getRVPLink().getEndpoint() != null && myNode.getRVPLink().getEndpoint().getID().equals(routerID))) {
                myNode.setRVPLink(link);
            }
            publishToNeighbors = true;
        }

        if (myNode.getProxyRouterLink() == null || Double.compare(myNode.getProxyRouterLink().getCost(), proxyRouterLink.getCost()) > 0) {
            myNode.setProxyRouterLink(proxyRouterLink);
            publishToNeighbors = true;
        }

        String str = "Proxy Router Link : " + message.getProxyRouterLink().toString() + "\n"
                + message.getProxyRouterLink().getEndpoint().toString();
       // logger.debug(str);

        if (myNode.getProxyRouterLink().getEndpoint().getID().equals(myNode.getID()) && myNode.getRVPLink().getEndpoint().getID().equals(routerID)) {
            publisher.publishRVPAckMsg(message, myNode.getRVPLink());
        } else {
            publishLSAToProxy = true;
            publisher.publishTMCAckMsg(message, routerID);
        }

        message.incrementTTL();

        if (publishToNeighbors) {
            publisher.publishProxyRouterAnnouncement(message);
        }

        if (publishLSAToProxy) {
            if (!hasPublishedLSAToProxy) {
                hasPublishedLSAToProxy = true;
                if (!myNode.getProxyRouterLink().getEndpoint().getID().equals(myNode.getID())) {
                    publisher.publishLSAMessage(message.getTTL());
                }
            }
        }
    }
}

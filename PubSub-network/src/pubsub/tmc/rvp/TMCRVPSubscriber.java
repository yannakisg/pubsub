package pubsub.tmc.rvp;

import java.io.IOException;
import java.nio.ByteBuffer;
import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.Publication;
import pubsub.bloomfilter.BloomFilter;
import pubsub.forwarding.LinkEstablishAnnouncement;
import pubsub.localrendezvous.LocRCClient;
import pubsub.messages.Message;
import pubsub.messages.MessageType;
import pubsub.messages.ipc.IPCTMCInterestMessage;
import pubsub.messages.ipc.RequestSteinerTreeMessage;
import pubsub.messages.net.tmc.HelloMessage;
import pubsub.messages.net.tmc.TMCNetMessage;
import pubsub.tmc.TMCSubscriber;
import pubsub.tmc.TMC_Mode;
import pubsub.tmc.graph.Link;
import pubsub.tmc.graph.MyRVPNode;
import pubsub.tmc.graph.NeighborNode;

/**
 *
 * @author John Gasparis
 */
public class TMCRVPSubscriber extends TMCSubscriber {

    private MyRVPNode myNode;
    private TMCRVPPublisher publisher;
    private TopologyReceiver receiver;
    private boolean hasRcvHelloMsg;

    TMCRVPSubscriber(MyRVPNode myNode, LocRCClient locRCClient) {
        this.publisher = new TMCRVPPublisher(myNode, locRCClient);
        this.myNode = myNode;
        this.receiver = new TopologyReceiver(myNode, locRCClient);
        this.hasRcvHelloMsg = false;
    }

    @Override
    protected void processEstablishedLink(Publication pub) {
        LinkEstablishAnnouncement lea = LinkEstablishAnnouncement.parseByteBuffer(pub.wrapData());
        BloomFilter lid;

        lid = lea.getLID();
        if (myNode.getProxyRouterLink() != null) {
            myNode.getProxyRouterLink().setLID(lid);
        } else {
            myNode.setProxyRouterLink(new Link(lid));
        }
        publisher.publishHelloMsg(lea);
        if (hasRcvHelloMsg) {
            publisher.publishRVPProxyRouterAnnouncement();
        }

    }

    @Override
    protected void processLinkDown(Publication pub) {
        myNode.setProxyRouterLink(null);
    }

    @Override
    protected void processTMCPublication(Publication pub) throws IOException {
        MessageType.Type msgType;
        // logger.debug("Received " + pub.getDataLength() + " bytes");


        msgType = Message.getMessageType(pub.getByteAt(0));

        //  logger.debug(msgType + " MESSAGE");
        if (msgType == MessageType.Type.HELLO) {
            processHelloMsg(pub.wrapData());
        } else if (msgType == MessageType.Type.GET_MYNODE_ID) {
            publisher.publishNodeIDMessage();
        } else if (msgType == MessageType.Type.GET_PROXY_ROUTER) {
            publisher.publishGetProxyRouterMessage();
        } else if (msgType == MessageType.Type.TMC_ACK) {
            publisher.processTMCAckMessage(pub.wrapData());
        } else if (msgType == MessageType.Type.TOPOLOGY_MESSAGE) {
            receiver.setRVPtoProxy(new ForwardIdentifier(myNode.getProxyRouterLink().getLidORVlid(), (short) 1));
            receiver.deliverMessage(pub.wrapData());
        } else if (msgType == MessageType.Type.GET_FID_A_B) {
            IPCTMCInterestMessage interest = IPCTMCInterestMessage.parseByteBuffer(pub.wrapData());
            publisher.publishFIDMessage(msgType, interest);
        } else if (msgType == MessageType.Type.REQUEST_STEINER_TREE) {
            RequestSteinerTreeMessage msg = RequestSteinerTreeMessage.parseByteBuffer(pub.wrapData());
            publisher.publishSteinerTree(msg);
        } else {
            publisher.publishTMCAckMsg(TMCNetMessage.findID(pub.wrapData()));
        }
    }

    private void processHelloMsg(ByteBuffer buffer) {
        HelloMessage message;
        ByteIdentifier routerID;

        hasRcvHelloMsg = true;
        message = HelloMessage.parseByteBuffer(buffer);
        routerID = message.getRouterID();

        if (hellomsgCache.contains(message.getVLID())) {
            publisher.publishTMCAckMsg(message);
            return;
        }

        if (message.getType() == TMC_Mode.ROUTER) {
            if (myNode.getProxyRouterLink() == null) {
                myNode.setProxyRouterLink(new Link(new NeighborNode(routerID, message.getVLID(), TMC_Mode.ROUTER)));
            } else {
                myNode.getProxyRouterLink().setEndPoint(new NeighborNode(routerID, message.getVLID(), TMC_Mode.ROUTER));
            }

            publisher.publishTMCAckMsg(message);
            publisher.publishRVPProxyRouterAnnouncement();
            hellomsgCache.add(message.getVLID());
        }
    }
}

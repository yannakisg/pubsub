package pubsub.tmc.host;

import java.io.IOException;
import java.nio.ByteBuffer;

import pubsub.ByteIdentifier;
import pubsub.Publication;
import pubsub.bloomfilter.BloomFilter;
import pubsub.forwarding.LinkEstablishAnnouncement;
import pubsub.localrendezvous.LocRCClient;
import pubsub.messages.Message;
import pubsub.messages.MessageType;
import pubsub.messages.net.NetMessage;
import pubsub.messages.net.tmc.HelloMessage;
import pubsub.tmc.TMCSubscriber;
import pubsub.tmc.TMC_Mode;
import pubsub.tmc.graph.GatewayNode;
import pubsub.tmc.graph.MyHostNode;

/**
 *
 * @author John Gasparis
 */
public class TMCHostSubscriber extends TMCSubscriber {

    private TMCHostPublisher publisher;
    private GatewayNode gatewayNode;

    public TMCHostSubscriber(MyHostNode myNode, LocRCClient locRCClient) {
        publisher = new TMCHostPublisher(myNode, locRCClient);
        gatewayNode = null;
    }

    @Override
    protected void processEstablishedLink(Publication pub) {
        LinkEstablishAnnouncement lea = LinkEstablishAnnouncement.parseByteBuffer(pub.wrapData());
        MessageType.Type nextType;
        BloomFilter lid;
       // logger.debug("VLID " + lea.getVLID().toBinaryString());
       // logger.debug("LID " + lea.getLID().toBinaryString());

        lid = lea.getLID();
        if (gatewayNode != null) {
            gatewayNode.setLID(lid);
        } else {
            gatewayNode = GatewayNode.createNew();
            gatewayNode.setLID(lid);
        }
        publisher.publishHelloMsg(lea);

    }

    @Override
    protected void processLinkDown(Publication pub) {
        gatewayNode = null;
    }

    @Override
    protected void processTMCPublication(Publication pub) throws IOException {
        MessageType.Type msgType;
        //logger.debug("Received " + pub.getDataLength() + " bytes");

        msgType = Message.getMessageType(pub.getByteAt(0));

      //  logger.debug(msgType + " MESSAGE");

        if (msgType == MessageType.Type.HELLO) {
            processHelloMsg(pub.wrapData());
        } else if (msgType == MessageType.Type.GET_DEFAULT_GW) {
            publisher.publishDefaultGateway(gatewayNode);
        } else if (msgType == MessageType.Type.GET_MYNODE_ID) {
            publisher.publishMyNodeIDMessage();
        } else if (msgType == MessageType.Type.TMC_ACK) {
            publisher.processTMCAckMessage(pub.wrapData());
        } else {
            publisher.publishTMCAckMsg(NetMessage.findID(pub.wrapData()), gatewayNode);
        }
    }

    private void processHelloMsg(ByteBuffer byteBuffer) {
        HelloMessage message;
        ByteIdentifier routerID;

        message = HelloMessage.parseByteBuffer(byteBuffer);
        routerID = message.getRouterID();

        if (hellomsgCache.contains(message.getVLID())) {
            publisher.publishTMCAckMsg(message, gatewayNode);
            return;
        }

        if (message.getType() == TMC_Mode.ROUTER) {
            if (gatewayNode == null) {
                gatewayNode = new GatewayNode(message.getVLID(), routerID);
            } else {
                gatewayNode.setVLID(message.getVLID());
                gatewayNode.setID(routerID);
            }

            publisher.publishTMCAckMsg(message, gatewayNode);
            hellomsgCache.add(message.getVLID());
        }
    }
}

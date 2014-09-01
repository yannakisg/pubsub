package pubsub.tmc;

import pubsub.ForwardIdentifier;
import java.io.IOException;
import java.util.Map;
import org.apache.log4j.Logger;
import pubsub.ByteIdentifier;
import pubsub.Publication;
import pubsub.Subscription;
import pubsub.bloomfilter.BloomFilter;
import pubsub.localrendezvous.LocRCClientFactory;
import pubsub.localrendezvous.TimeOutLocRCClient;
import pubsub.messages.MessageType;
import pubsub.messages.ipc.IPCMessage;
import pubsub.tmc.graph.GatewayNode;
import pubsub.tmc.graph.Link;
import pubsub.messages.ipc.IPCTMCInterestMessage;
import pubsub.messages.ipc.tmc.DefaultGWMessage;
import pubsub.messages.ipc.tmc.FIDMessage;
import pubsub.messages.ipc.tmc.GetProxyRouterMessage;
import pubsub.messages.ipc.tmc.GetRVPMessage;
import pubsub.messages.ipc.tmc.LIDMessage;
import pubsub.messages.ipc.tmc.NeighborsMessage;
import pubsub.messages.ipc.tmc.NodeIDMessage;
import pubsub.messages.ipc.RequestSteinerTreeMessage;
import pubsub.util.Consumer;

/**
 * A class that provides methods for exposing TMC information such as the
 * zfilter to the default gateway router or the RVP-proxy
 *
 * @author John Gasparis
 * @version 0.1
 */
public class TMCInfo {

    private static final Logger logger = Logger.getLogger(TMCInfo.class);
    private static final Subscription sub = Subscription.createSubToMutableData(TMCUtil.TMC_SID, TMCUtil.TMC_LOCAL_UTIL_RID);

    public static ByteIdentifier getMyNodeID(TimeOutLocRCClient locRCClient) {
        try {
            NodeIDMessage msg = (NodeIDMessage) getTMCInfo(locRCClient, MessageType.Type.GET_MYNODE_ID);
            return msg.getMyNodeID();
        } catch (InterruptedException ex) {
            logger.error("InterruptedException", ex);
            return null;
        }
    }

    public static ByteIdentifier getMyNodeID() throws Exception {
        TimeOutLocRCClient locRCClient = LocRCClientFactory.createTimeOutClient();
        ByteIdentifier id = getMyNodeID(locRCClient);

        locRCClient.close();
        return id;
    }

    /**
     * Used in order to obtain the neighboring nodes
     * @return A map containing the Bloom Filters and NodeID's of the nodes
     */
    public static Map<ByteIdentifier, ForwardIdentifier> getNeighbors(TimeOutLocRCClient locRCClient) {
        try {
            NeighborsMessage msg = (NeighborsMessage) getTMCInfo(locRCClient, MessageType.Type.GET_NEIGHBORS);
            return msg.getNeighbors();
        } catch (InterruptedException ex) {
            logger.error("InterruptedException", ex);
            return null;
        }
    }

    public static BloomFilter getLID(TimeOutLocRCClient locRCClient, ByteIdentifier nodeID) {
        try {
            LIDMessage msg = (LIDMessage) getTMCInfo(locRCClient, MessageType.Type.GET_LID, nodeID);
            return msg.getLID();
        } catch (InterruptedException ex) {
            logger.error("InterruptedException", ex);
            return null;
        }
    }

    /**
     * Used in order to obtain the neighboring nodes
     * @return A map containing the Bloom Filters and NodeID's of the nodes
     */
    public static Map<ByteIdentifier, ForwardIdentifier> getNeighbors() throws Exception {
        TimeOutLocRCClient locRCClient = LocRCClientFactory.createTimeOutClient();
        Map<ByteIdentifier, ForwardIdentifier> neighbors = getNeighbors(locRCClient);

        locRCClient.close();
        return neighbors;
    }

    public static ForwardIdentifier getFID(TimeOutLocRCClient locRCClient, ByteIdentifier nodeID) {
        return getFID(locRCClient, nodeID, true);
    }

    public static ForwardIdentifier getFID(TimeOutLocRCClient locRCClient, ByteIdentifier nodeID, boolean includeDest) {
        try {
            FIDMessage msg = (FIDMessage) getTMCInfo(locRCClient, MessageType.Type.GET_FID, nodeID, includeDest);
            return msg.getFID();
        } catch (InterruptedException ex) {
            logger.error("InterruptedException", ex);
            return null;
        }
    }

    public static ForwardIdentifier getFID(ByteIdentifier nodeID) throws Exception {
        TimeOutLocRCClient locRCClient = LocRCClientFactory.createTimeOutClient();
        ForwardIdentifier fid = getFID(locRCClient, nodeID);

        locRCClient.close();
        return fid;
    }

    public static ForwardIdentifier getFID(ByteIdentifier nodeA, ByteIdentifier nodeB) throws Exception {
        TimeOutLocRCClient locRCClient = LocRCClientFactory.createTimeOutClient();
        ForwardIdentifier fid = getFID(locRCClient, nodeA, nodeB);

        locRCClient.close();
        return fid;
    }

    public static ForwardIdentifier getFID(TimeOutLocRCClient locRCClient, ByteIdentifier nodeA, ByteIdentifier nodeB) {
        return getFID(locRCClient, nodeA, nodeB, true);
    }

    public static ForwardIdentifier getFID(TimeOutLocRCClient locRCClient, ByteIdentifier nodeA, ByteIdentifier nodeB, boolean includeDest) {
        try {
            FIDMessage msg = (FIDMessage) getTMCInfo(locRCClient, MessageType.Type.GET_FID_A_B, nodeA, nodeB, includeDest);
            return msg.getFID();
        } catch (InterruptedException ex) {
            logger.error("InterruptedException", ex);
            return null;
        }
    }

    public static GatewayNode getDefaultGateway() throws Exception {
        TimeOutLocRCClient locRCClient = LocRCClientFactory.createTimeOutClient();
        GatewayNode gwNode = getDefaultGateway(locRCClient);

        locRCClient.close();
        return gwNode;
    }

    public static GatewayNode getDefaultGateway(TimeOutLocRCClient locRCClient) {
        try {
            DefaultGWMessage msg = (DefaultGWMessage) getTMCInfo(locRCClient, MessageType.Type.GET_DEFAULT_GW);
            GatewayNode gw = msg.getDefaultGW();
            return gw;
        } catch (InterruptedException ex) {
            logger.error("InterruptedException", ex);
            return null;
        }
    }

    public static Link getProxyRouterLink() throws Exception {
        TimeOutLocRCClient locRCClient = LocRCClientFactory.createTimeOutClient();
        Link link = getProxyRouterLink(locRCClient);

        locRCClient.close();
        return link;
    }

    public static Link getProxyRouterLink(TimeOutLocRCClient locRCClient) {
        try {
            GetProxyRouterMessage msg = (GetProxyRouterMessage) getTMCInfo(locRCClient, MessageType.Type.GET_PROXY_ROUTER);

            return msg.getProxyRouterLink();
        } catch (InterruptedException ex) {
            logger.error("InterruptedException", ex);
            return null;
        }
    }

    public static Link getRVPLink() throws Exception {
        TimeOutLocRCClient locRCClient = LocRCClientFactory.createTimeOutClient();
        Link link = getRVPLink(locRCClient);

        locRCClient.close();
        return link;
    }

    public static Link getRVPLink(TimeOutLocRCClient locRCClient) {
        try {
            GetRVPMessage msg = (GetRVPMessage) getTMCInfo(locRCClient, MessageType.Type.GET_RVP);

            return msg.getRVPLink();
        } catch (InterruptedException ex) {
            logger.error("InterruptedException", ex);
            return null;
        }
    }
    
    public static ForwardIdentifier getSteinerTree(ByteIdentifier[] steinerPoints) throws IOException {
        TimeOutLocRCClient locRCClient = LocRCClientFactory.createTimeOutClient();
        ForwardIdentifier fid = getSteinerTree(locRCClient, steinerPoints);
        
        locRCClient.close();
        return fid;
    }
    
    public static ForwardIdentifier getSteinerTree(TimeOutLocRCClient locRCClient, ByteIdentifier[] steinerPoints) {
        try {
            FIDMessage msg = getTMCInfoSteinerTree(locRCClient, steinerPoints);
            return msg.getFID();
        } catch (InterruptedException ex) {
            logger.error("InterruptedException", ex);
            return null;
        }
    }

    public static ForwardIdentifier getAttachedHost(ByteIdentifier hostID) throws IOException {
        TimeOutLocRCClient locRCClient = LocRCClientFactory.createTimeOutClient();
        ForwardIdentifier fid = getAttachedHost(locRCClient, hostID);

        locRCClient.close();
        return fid;
    }

    public static ForwardIdentifier getAttachedHost(TimeOutLocRCClient locRCClient, ByteIdentifier hostID) {
        try {
            FIDMessage msg = (FIDMessage) getTMCInfo(locRCClient, MessageType.Type.GET_FID_HOST, hostID);

            return msg.getFID();
        } catch (InterruptedException ex) {
            logger.error("InterruptedException", ex);
            return null;
        }
    }
    
    private static FIDMessage getTMCInfoSteinerTree(TimeOutLocRCClient locRCClient, ByteIdentifier[] steinerPoints) throws InterruptedException {
        RequestSteinerTreeMessage request = new RequestSteinerTreeMessage(steinerPoints);
        Consumer<Publication> consumer = locRCClient.subscribeNonBlock(sub);
        Publication pub;
        
        request.publishMutableData(locRCClient, request.toBytes());
        pub = consumer.take();
        
        locRCClient.unsubscribe(sub);
        return FIDMessage.parseByteBuffer(pub.wrapData());
    }

    private static IPCMessage getTMCInfo(TimeOutLocRCClient locRCClient, MessageType.Type msgType) throws InterruptedException {
        return getTMCInfo(locRCClient, msgType, null, null, true);
    }

    private static IPCMessage getTMCInfo(TimeOutLocRCClient locRCClient, MessageType.Type msgType, ByteIdentifier idA) throws InterruptedException {
        return getTMCInfo(locRCClient, msgType, idA, null, true);
    }

    private static IPCMessage getTMCInfo(TimeOutLocRCClient locRCClient, MessageType.Type msgType, ByteIdentifier idA, boolean includeDest) throws InterruptedException {
        return getTMCInfo(locRCClient, msgType, idA, null, includeDest);
    }

    private static IPCMessage getTMCInfo(TimeOutLocRCClient locRCClient, MessageType.Type msgType, ByteIdentifier idA, ByteIdentifier idB, boolean includeDest) throws InterruptedException {
        IPCTMCInterestMessage interest = new IPCTMCInterestMessage(msgType, idA, idB, includeDest);
        IPCMessage message;
        Publication pub;

        Consumer<Publication> consumer = locRCClient.subscribeNonBlock(sub);
        interest.publish(locRCClient, interest.toBytes());

        pub = consumer.take();

        if (msgType == MessageType.Type.GET_MYNODE_ID) {
            message = NodeIDMessage.parseByteBuffer(pub.wrapData());
        } else if (msgType == MessageType.Type.GET_NEIGHBORS) {
            message = NeighborsMessage.parseByteBuffer(pub.wrapData());
        } else if (msgType == MessageType.Type.GET_LID) {
            message = LIDMessage.parseByteBuffer(pub.wrapData());
        } else if (msgType == MessageType.Type.GET_FID
                || msgType == MessageType.Type.GET_FID_A_B
                || msgType == MessageType.Type.GET_FID_HOST) {

            message = FIDMessage.parseByteBuffer(pub.wrapData());
        } else if (msgType == MessageType.Type.GET_DEFAULT_GW) {
            message = DefaultGWMessage.parseByteBuffer(pub.wrapData());
        } else if (msgType == MessageType.Type.GET_PROXY_ROUTER) {
            message = GetProxyRouterMessage.parseByteBuffer(pub.wrapData());
        } else if (msgType == MessageType.Type.GET_RVP) {
            message = GetRVPMessage.parseByteBuffer(pub.wrapData());
        } else {
            message = null;
        }

        locRCClient.unsubscribe(sub);

        return message;
    }
}

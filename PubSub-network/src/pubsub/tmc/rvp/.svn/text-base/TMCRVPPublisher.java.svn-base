package pubsub.tmc.rvp;

import java.nio.ByteBuffer;

import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.bloomfilter.BloomFilter;
import pubsub.localrendezvous.LocRCClient;
import pubsub.messages.MessageType;
import pubsub.messages.MessageType.Type;
import pubsub.messages.ipc.IPCTMCInterestMessage;
import pubsub.messages.ipc.rvp.RVPProxyRouterAnnouncement;
import pubsub.messages.ipc.tmc.FIDMessage;
import pubsub.messages.ipc.tmc.GetProxyRouterMessage;
import pubsub.messages.ipc.tmc.NodeIDMessage;
import pubsub.messages.ipc.RequestSteinerTreeMessage;
import pubsub.messages.net.TMCAckMessage;
import pubsub.messages.net.tmc.NetInterestMessage;
import pubsub.messages.net.tmc.TMCNetMessage;
import pubsub.tmc.TMCPublisher;
import pubsub.tmc.graph.Link;
import pubsub.tmc.graph.MyRVPNode;

/**
 *
 * @author John Gasparis
 */
public class TMCRVPPublisher extends TMCPublisher {

    private MyRVPNode myRVPNode;
    private ForwardIdentifier proxyFID;

    public TMCRVPPublisher(MyRVPNode myNode, LocRCClient locRCClient) {
        super();
        this.myRVPNode = myNode;
        this.myNode = myNode;
        this.locRCClient = locRCClient;
    }

    public void publishRVPProxyRouterAnnouncement() {
        RVPProxyRouterAnnouncement message;
        Link proxyRouterLink;

        proxyRouterLink = myRVPNode.getProxyRouterLink();

        message = new RVPProxyRouterAnnouncement(proxyRouterLink);

        message.publishMutableData(locRCClient, message.toBytes());
     //   logger.debug("Published Proxy Router Announcement");
    }

    public void publishInterestMsgToProxy(MessageType.Type type) {

        NetInterestMessage interest;

        if (myRVPNode.getProxyRouterLink() == null) {
            return;
        }
        
        if (proxyFID == null) {
            proxyFID = new ForwardIdentifier(myRVPNode.getProxyRouterLink().getLidORVlid(), (short) 1);
        }

        interest = new NetInterestMessage(type, proxyFID, myRVPNode.getID());

        interest.publishMutableData(locRCClient, interest.toBytes());
       // logger.debug("Published Interest Message To Proxy");
    }

    public void publishNodeIDMessage() {
        NodeIDMessage message = NodeIDMessage.getInstance(myRVPNode.getID());

      //  logger.debug("Publish NodeIDMessage");
        message.publishMutableData(locRCClient, message.toBytes());
    }

    public void publishGetProxyRouterMessage() {
      //  logger.debug("publish GetProxyRouterMessage");

        GetProxyRouterMessage message = new GetProxyRouterMessage(myRVPNode.getProxyRouterLink());
        message.publishMutableData(locRCClient, message.toBytes());
    }
    
    public void publishTMCAckMsg(int id) {
        if (this.myRVPNode.getProxyRouterLink() == null) {
            return;
        }
        
        if (proxyFID == null) {
            proxyFID = new ForwardIdentifier(myRVPNode.getProxyRouterLink().getLidORVlid(), (short) 1);
        }
        
        TMCAckMessage message = new TMCAckMessage(id, proxyFID);

        message.publishMutableData(locRCClient, message.toBytes());
     //   logger.debug("Published TMC ACK ");
    }

    public void publishTMCAckMsg(TMCNetMessage msg) {
        publishTMCAckMsg(msg.getID());
    }

    public void processTMCAckMessage(ByteBuffer buff) {
        TMCAckMessage message = TMCAckMessage.parseByteBuffer(buff);
        this.handler.removeEntry(message.getAckID());
    }
    
    public void publishSteinerTree(RequestSteinerTreeMessage msg) {
        FIDMessage fidMessage;
        ByteIdentifier[] steinerPoints = msg.getSteinerPoints();            
        int i = 0;
        
       /* for (ByteIdentifier id : steinerPoints) {
          //  logger.debug("Point[" + i + "] = " + id);
            i++;
        }*/
        
        ForwardIdentifier fid = myRVPNode.constructSteinerTree(steinerPoints);
        
        fidMessage = new FIDMessage(Type.GET_FID, fid);
        fidMessage.publishMutableData(locRCClient, fidMessage.toBytes());               
    }

    public void publishFIDMessage(Type msgType, IPCTMCInterestMessage interest) {
        FIDMessage message;

        if (interest.getIDA() == null && interest.getIDB() == null) {
            return;
        } else {
            if (msgType == MessageType.Type.GET_FID_A_B) {
                BloomFilter lid;
                short[] ttl = new short[1];

                lid = myRVPNode.getPath(interest.getIDA(), interest.getIDB(), ttl, interest.includeDest());
                
                message = new FIDMessage(msgType, new ForwardIdentifier(lid, ttl[0]));
            }  else {
                return;
            }
        }

        //logger.debug("Publish FIDMessage");
        message.publishMutableData(locRCClient, message.toBytes());
    }
}

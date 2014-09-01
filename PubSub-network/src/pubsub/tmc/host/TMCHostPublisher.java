package pubsub.tmc.host;

import java.nio.ByteBuffer;

import pubsub.ForwardIdentifier;
import pubsub.localrendezvous.LocRCClient;
import pubsub.messages.ipc.tmc.NodeIDMessage;
import pubsub.messages.net.TMCAckMessage;
import pubsub.messages.net.tmc.TMCNetMessage;
import pubsub.tmc.TMCPublisher;
import pubsub.tmc.graph.GatewayNode;
import pubsub.tmc.graph.MyHostNode;

/**
 *
 * @author John Gasparis
 */
public class TMCHostPublisher extends TMCPublisher {

    private MyHostNode myHostNode;
    private ForwardIdentifier gwFID = null;

    public TMCHostPublisher(MyHostNode myNode, LocRCClient locRCClient) {
        super();
        this.locRCClient = locRCClient;
        this.myNode = myNode;
        this.myHostNode = myNode;
    }

    public void publishMyNodeIDMessage() {
        NodeIDMessage message = NodeIDMessage.getInstance(myHostNode.getID());

        message.publishMutableData(locRCClient, message.toBytes());

       // logger.debug("Published Utility Message");
    }
    
    public void publishTMCAckMsg(int id, GatewayNode gw) {
        if (gw == null || gw.getLID() == null || gw.getVLID() == null) {
            return;
        }
        
        if (gwFID == null) {
            gwFID = new ForwardIdentifier(gw.getLidORVlid(gw.getLID()), (short) 1);
        }
        
        TMCAckMessage message = new TMCAckMessage(id, gwFID);
         message.publishMutableData(locRCClient, message.toBytes());
       // logger.debug("Published TMC ACK [" + id + "]");
    }

    public void publishTMCAckMsg(TMCNetMessage rcvdMsg, GatewayNode gw) {
        if (gw == null || gw.getLID() == null || gw.getVLID() == null) {
            return;
        }
        
        if (gwFID == null) {
            gwFID = new ForwardIdentifier(gw.getLidORVlid(gw.getLID()), (short) 1);
        }
        
        TMCAckMessage message = new TMCAckMessage(rcvdMsg.getID(), gwFID);

        message.publishMutableData(locRCClient, message.toBytes());
      //  logger.debug("Published TMC ACK [" + rcvdMsg.getID() + "]");
    }

    public void processTMCAckMessage(ByteBuffer byteBuffer) {
       // logger.debug("processTMCAckMessage");
        TMCAckMessage message = TMCAckMessage.parseByteBuffer(byteBuffer);
        this.handler.removeEntry(message.getAckID());
    }
}

package pubsub.messages.net.rvp;

import pubsub.ForwardIdentifier;
import pubsub.PubSubID;
import pubsub.messages.MessageType;
import pubsub.messages.net.NetMessage;
import pubsub.rva.RVARVPComponent;

/**
 *
 * @author John Gasparis
 */
public class RVPNetMessage extends NetMessage {

    private static final PubSubID RVP_SID = RVARVPComponent.RVP_SID;
    private static final PubSubID RVP_RID = RVARVPComponent.RVP_RID;

    public RVPNetMessage(MessageType.Type msgType, ForwardIdentifier fid) {
        super(msgType, RVP_SID, RVP_RID, fid);
    }
}

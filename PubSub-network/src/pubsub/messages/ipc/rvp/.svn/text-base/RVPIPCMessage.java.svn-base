package pubsub.messages.ipc.rvp;

import pubsub.PubSubID;
import pubsub.messages.MessageType;
import pubsub.messages.ipc.IPCMessage;
import pubsub.rva.RVARVPComponent;

/**
 *
 * @author John Gasparis
 */
public class RVPIPCMessage extends IPCMessage {

    private static final PubSubID RVP_SID = RVARVPComponent.RVP_SID;
    private static final PubSubID RVP_PRESENCE_RID = RVARVPComponent.RVP_PRESENCE_RID;

    public RVPIPCMessage(MessageType.Type msgType) {
        super(msgType, RVP_SID, RVP_PRESENCE_RID);
    }
}

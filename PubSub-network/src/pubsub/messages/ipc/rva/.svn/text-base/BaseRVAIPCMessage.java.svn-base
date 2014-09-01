package pubsub.messages.ipc.rva;

import pubsub.PubSubID;
import pubsub.messages.MessageType;
import pubsub.messages.ipc.IPCMessage;
import pubsub.rva.RVAUtil;

/**
 *
 * @author John Gasparis
 */
public abstract class BaseRVAIPCMessage extends IPCMessage {

    private static final PubSubID RVA_SID = RVAUtil.RVA_SID;
    private static final PubSubID RVA_RID = RVAUtil.RVA_RID;

    public BaseRVAIPCMessage(MessageType.Type msgType) {
        super(msgType, RVA_SID, RVA_RID);
    }
}

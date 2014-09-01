package pubsub.messages.ipc.tmc;

import pubsub.PubSubID;
import pubsub.messages.MessageType;
import pubsub.messages.ipc.IPCMessage;
import pubsub.tmc.TMCUtil;

/**
 *
 * @author John Gasparis
 */
public abstract class TMCIPCMessage extends IPCMessage {

    private static final PubSubID TMC_SID = TMCUtil.TMC_SID;
    private static final PubSubID TMC_LOCAL_UTIL_RID = TMCUtil.TMC_LOCAL_UTIL_RID;

    public TMCIPCMessage(MessageType.Type msgType) {
        super(msgType, TMC_SID, TMC_LOCAL_UTIL_RID);
    }
}

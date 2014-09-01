package pubsub.messages.net.rva;

import pubsub.ForwardIdentifier;
import pubsub.PubSubID;
import pubsub.messages.MessageType;
import pubsub.messages.net.NetMessage;
import pubsub.rva.RVAUtil;

/**
 *
 * @author John Gasparis
 */
public abstract class BaseRVANetMessage extends NetMessage {

    private static final PubSubID RVA_SID = RVAUtil.RVA_SID;
    private static final PubSubID RVA_COM_RID = RVAUtil.RVA_COM_RID;

    public BaseRVANetMessage(MessageType.Type msgType, ForwardIdentifier fid) {
        super(msgType, RVA_SID, RVA_COM_RID, fid);
    }

    public BaseRVANetMessage() {
        super(MessageType.Type.NOTHING, RVA_SID, RVA_COM_RID);
    }
}

package pubsub.messages.net.rva;

import pubsub.ForwardIdentifier;
import pubsub.messages.MessageType;

/**
 *
 * @author John Gasparis
 */
public class RVAErrorMessage extends BaseRVANetMessage {

    public RVAErrorMessage(ForwardIdentifier fid) {
        super(MessageType.Type.RVA_ERROR_MESSAGE, fid);
    }
}

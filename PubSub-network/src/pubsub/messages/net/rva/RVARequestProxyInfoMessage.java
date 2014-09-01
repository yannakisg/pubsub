package pubsub.messages.net.rva;

import java.nio.ByteBuffer;
import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.messages.MessageType;

/**
 *
 * @author John Gasparis
 */
public class RVARequestProxyInfoMessage extends BaseRVANetMessage {

    private ByteIdentifier id;
    private static int LENGTH = -1;

    public RVARequestProxyInfoMessage(ByteIdentifier id, ForwardIdentifier fid) {
        super(MessageType.Type.RVA_REQUEST_PROXY_INFO_MESSAGE, fid);
        this.id = id;
    }

    private RVARequestProxyInfoMessage() {
        this(null, null);
    }

    public void setID(ByteIdentifier id) {
        this.id = id;
    }

    public ByteIdentifier getProxyInfoID() {
        return this.id;
    }

    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = super.getSerializedLength() + this.id.getSerializedLength();
        }
        return LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        id.writeTo(buff);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);
        id = ByteIdentifier.parseByteBuffer(buff);
    }

    public static RVARequestProxyInfoMessage parseByteBuffer(ByteBuffer buff) {
        RVARequestProxyInfoMessage msg = new RVARequestProxyInfoMessage();
        msg.readBuffer(buff);
        return msg;
    }
}

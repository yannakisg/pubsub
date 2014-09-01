package pubsub.messages.ipc.tmc;

import java.nio.ByteBuffer;
import pubsub.messages.MessageType;
import pubsub.tmc.graph.Link;

/**
 *
 * @author John Gasparis
 */
public class GetRVPMessage extends TMCIPCMessage {

    private Link rvpLink;

    public GetRVPMessage(Link rvpLink) {
        super(MessageType.Type.GET_RVP);
        this.rvpLink = rvpLink;
    }

    private GetRVPMessage() {
        this(null);
    }

    @Override
    public int getSerializedLength() {
        return super.getSerializedLength() + (rvpLink != null ? rvpLink.getSerializedLength() : 0);
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        if (rvpLink != null) {
            rvpLink.writeTo(buff);
        }
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);
        if (buff.hasRemaining()) {
            rvpLink = Link.parseByteBuffer(buff);
        }
    }

    public Link getRVPLink() {
        return rvpLink;
    }

    public static GetRVPMessage parseByteBuffer(ByteBuffer buff) {
        GetRVPMessage msg = new GetRVPMessage();
        msg.readBuffer(buff);
        return msg;
    }
}

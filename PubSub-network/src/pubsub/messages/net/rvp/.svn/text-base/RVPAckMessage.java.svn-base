package pubsub.messages.net.rvp;

import java.nio.ByteBuffer;
import pubsub.ForwardIdentifier;
import pubsub.messages.MessageType;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class RVPAckMessage extends RVPNetMessage {

    private int id;
    private static int LENGTH = -1;

    public RVPAckMessage(int id, ForwardIdentifier fid) {
        super(MessageType.Type.RVP_ACK_MESSAGE, fid);
        this.id = id;
    }

    private RVPAckMessage() {
        this(-1, null);
    }

    public int getAckID() {
        return this.id;
    }

    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = super.getSerializedLength() + Util.SIZEOF_INT;
        }
        return LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        buff.putInt(id);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);
        id = buff.getInt();
    }

    public static RVPAckMessage parseByteBuffer(ByteBuffer buffer) {
        RVPAckMessage message = new RVPAckMessage();
        message.readBuffer(buffer);
        return message;
    }

    public static RVPAckMessage parseByteArray(byte[] data) {
        RVPAckMessage message = new RVPAckMessage();
        message.fromBytes(data);
        return message;
    }
}

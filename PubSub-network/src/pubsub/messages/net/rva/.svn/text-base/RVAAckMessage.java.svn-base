package pubsub.messages.net.rva;

import java.nio.ByteBuffer;
import pubsub.ForwardIdentifier;
import pubsub.messages.MessageType;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class RVAAckMessage extends BaseRVANetMessage {

    private int id;
    private static int LENGTH = -1;

    public RVAAckMessage(int id, ForwardIdentifier fid) {
        super(MessageType.Type.RVA_ACK_MESSAGE, fid);
        this.id = id;
    }

    private RVAAckMessage() {
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

    public static RVAAckMessage parseByteBuffer(ByteBuffer buff) {
        RVAAckMessage message = new RVAAckMessage();
        message.readBuffer(buff);
        return message;
    }
}

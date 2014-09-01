package pubsub.messages.net.transport;

import java.nio.ByteBuffer;
import pubsub.ForwardIdentifier;
import pubsub.PubSubID;
import pubsub.messages.MessageType;
import pubsub.messages.net.NetMessage;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class ControlACKMessage extends NetMessage {

    private int id;
    private static int LENGTH = -1;

    public ControlACKMessage(PubSubID sid, PubSubID rid, int id, ForwardIdentifier fid) {
        super(MessageType.Type.CTRL_ACK_MESSAGE, sid, rid, fid);

        this.id = id;
    }

    private ControlACKMessage() {
        this(null, null, -1, null);
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
        this.id = buff.getInt();
    }

    public static ControlACKMessage parseByteBuffer(ByteBuffer buffer) {
        ControlACKMessage msg = new ControlACKMessage();
        msg.readBuffer(buffer);
        return msg;
    }
}

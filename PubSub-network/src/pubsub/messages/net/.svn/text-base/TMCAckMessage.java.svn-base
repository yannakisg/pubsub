package pubsub.messages.net;

import java.nio.ByteBuffer;
import pubsub.ForwardIdentifier;
import pubsub.messages.MessageType;
import pubsub.messages.net.NetMessage;
import pubsub.tmc.TMCUtil;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class TMCAckMessage extends NetMessage {

    private int id;
    private static int LENGTH = -1;

    public TMCAckMessage(int id, ForwardIdentifier fid) {
        super(MessageType.Type.TMC_ACK, TMCUtil.TMC_SID, TMCUtil.TMC_RID, fid);
        this.id = id;
    }

    private TMCAckMessage() {
        super(MessageType.Type.TMC_ACK, TMCUtil.TMC_SID, TMCUtil.TMC_RID);
        this.id = -1;
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

    public static TMCAckMessage parseByteBuffer(ByteBuffer buff) {
        TMCAckMessage message = new TMCAckMessage();
        message.readBuffer(buff);
        return message;
    }
    
    public static TMCAckMessage parseByteArray(byte [] data) {
        TMCAckMessage message = new TMCAckMessage();
        message.fromBytes(data);
        return message;
    }
}

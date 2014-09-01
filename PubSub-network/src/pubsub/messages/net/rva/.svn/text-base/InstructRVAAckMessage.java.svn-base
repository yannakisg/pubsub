package pubsub.messages.net.rva;

import java.nio.ByteBuffer;
import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.messages.MessageType;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class InstructRVAAckMessage extends BaseRVANetMessage{
    private int ackID;
    private ByteIdentifier destID;
    private static int LENGTH = -1;

    public InstructRVAAckMessage(int ackID, ByteIdentifier destID, ForwardIdentifier proxyFID) {
        super (MessageType.Type.INSTRUCT_RVA_ACK_MESSAGE, proxyFID);

        this.ackID = ackID;
        this.destID = destID;
    }

    private InstructRVAAckMessage() {
        this(-1, null, null);
    }

    public int getAckID() {
        return this.ackID;
    }

    public ByteIdentifier getDestID() {
        return this.destID;
    }

    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = super.getSerializedLength() + Util.SIZEOF_INT + destID.getSerializedLength();
        }
        return LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        buff.putInt(ackID);
        destID.writeTo(buff);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);
        ackID = buff.getInt();
        destID = ByteIdentifier.parseByteBuffer(buff);
    }

    public static InstructRVAAckMessage parseByteBuffer(ByteBuffer buff) {
        InstructRVAAckMessage message = new InstructRVAAckMessage();
        message.readBuffer(buff);
        return message;
    }
}

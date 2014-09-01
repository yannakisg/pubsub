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
public class ControlMessage extends NetMessage {

    private PubSubID reverseSID;
    private PubSubID reverseRID;
    private ForwardIdentifier subToPub;
    private ForwardIdentifier pubToSub;
    private int chunkSize;
    private long totalChunks;
    private static int LENGTH = -1;

    public ControlMessage(PubSubID sid, PubSubID rid, ForwardIdentifier fid,
            PubSubID reverseSID, PubSubID reverseRID, ForwardIdentifier pubToSub, ForwardIdentifier subToPub,
            int chunkSize, long totalChunks) {
        super(MessageType.Type.CONTROL_MESSAGE, sid, rid, fid);

        this.subToPub = subToPub;
        this.pubToSub = pubToSub;
        this.reverseSID = reverseSID;
        this.reverseRID = reverseRID;
        this.chunkSize = chunkSize;
        this.totalChunks = totalChunks;
    }

    private ControlMessage() {
        this(null, null, null, null, null, null, null, 0, 0);
    }

    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = super.getSerializedLength() + (subToPub.getSerializedLength() << 1)
                    + (reverseRID.getSerializedLength() << 1) + Util.SIZEOF_INT + Util.SIZEOF_LONG;
        }
        return LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);

        reverseSID.writeTo(buff);
        reverseRID.writeTo(buff);

        pubToSub.writeTo(buff);
        subToPub.writeTo(buff);


        buff.putInt(chunkSize);
        buff.putLong(totalChunks);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);

        reverseSID = PubSubID.parseByteBuffer(buff);
        reverseRID = PubSubID.parseByteBuffer(buff);

        pubToSub = ForwardIdentifier.parseByteBuffer(buff);
        subToPub = ForwardIdentifier.parseByteBuffer(buff);

        chunkSize = buff.getInt();
        totalChunks = buff.getLong();
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public PubSubID getReverseRID() {
        return reverseRID;
    }

    public PubSubID getReverseSID() {
        return reverseSID;
    }

    public long getTotalChunks() {
        return totalChunks;
    }

    public ForwardIdentifier getPubToSub() {
        return this.pubToSub;
    }

    public void setSubToPub(ForwardIdentifier subToPub) {
        this.subToPub = subToPub;
    }

    public ForwardIdentifier getSubToPub() {
        return subToPub;
    }

    public static ControlMessage parseByteBuffer(ByteBuffer buffer) {
        ControlMessage message = new ControlMessage();
        message.readBuffer(buffer);
        return message;
    }

    public static ControlMessage parseByteArray(byte[] data) {
        ControlMessage message = new ControlMessage();
        message.fromBytes(data);
        return message;
    }
}

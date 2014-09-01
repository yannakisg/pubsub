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
public class DataMessage extends NetMessage {
    
    private int hopCount;
    private long timestamp;
    private int chunkNum;
    private byte[] data;
    private static int LENGTH = -1;

    public DataMessage(PubSubID sid, PubSubID rid, int chunkNum, byte[] data, long timestamp) {
        this(sid, rid, null, chunkNum, data, timestamp);
    }
    
    public DataMessage(PubSubID sid, PubSubID rid, ForwardIdentifier fid, int chunkNum, byte[] data, long timestamp) {
        super(MessageType.Type.DATA_MESSAGE, sid, rid, fid);

        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Invalid data");
        }

        this.timestamp = timestamp;
        this.chunkNum = chunkNum;
        this.data = data;
        this.hopCount = -1;
    }

    private DataMessage() {
        super(MessageType.Type.DATA_MESSAGE, null, null, null);
    }

    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = super.getSerializedLength() + (Util.SIZEOF_INT << 1) + Util.SIZEOF_INT + Util.SIZEOF_LONG;
        }
        return LENGTH + data.length;
    }

    @Override
    public void writeTo(ByteBuffer buffer) {
        super.writeTo(buffer);
        
        buffer.putInt(chunkNum);
        buffer.putInt(hopCount);
        buffer.putLong(timestamp);
        buffer.putInt(data.length);
        buffer.put(data);
    }

    @Override
    public void readBuffer(ByteBuffer buffer) {
        super.readBuffer(buffer);

        chunkNum = buffer.getInt();
        hopCount = buffer.getInt();
        timestamp = buffer.getLong();

        int length = buffer.getInt();
        data = new byte[length];
        buffer.get(data);
    }

    public static int getChunkNum(ByteBuffer buffer) {
        return buffer.getInt(buffer.position() + Util.SIZEOF_BYTE + Util.SIZEOF_INT);
    }
    
    public static int getHopCount(ByteBuffer buffer) {
        return buffer.getInt(buffer.position() + Util.SIZEOF_BYTE + (Util.SIZEOF_INT << 1));
    }
    
    public static void setHopCount(ByteBuffer buffer, int hopCount) {
        buffer.putInt(buffer.position() + Util.SIZEOF_BYTE + (Util.SIZEOF_INT << 1), hopCount);
    }
    
    public static void incrementHopCount(ByteBuffer buffer) {
        int hopCount = getHopCount(buffer);
        setHopCount(buffer, hopCount + 1);
    }

    public int getHopCount() {
        return hopCount;
    }
    
    public long getTimeStamp() {
        return timestamp;
    }

    public int getChunkNum() {
        return chunkNum;
    }

    public byte[] getData() {
        return data;
    }

    public static DataMessage parseByteBuffer(ByteBuffer buffer) {
        DataMessage msg = new DataMessage();
        msg.readBuffer(buffer);
        return msg;
    }

    public static DataMessage parseByteArray(byte[] data) {
        DataMessage msg = new DataMessage();
        msg.fromBytes(data);
        return msg;
    }
}

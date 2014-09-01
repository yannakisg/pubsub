package pubsub.forwarding;

import pubsub.bloomfilter.BloomFilter;
import java.nio.ByteBuffer;
import pubsub.BaseSerializableStruct;
import pubsub.util.Util;

/**
 *
 * @author tsilo
 * @author John Gasparis
 */
public class KeepAliveMessage extends BaseSerializableStruct {
    private double weight;
    private BloomFilter VLID;
    private static int LENGTH = -1;

    private KeepAliveMessage() {
        this(null, -1);
    }

    public KeepAliveMessage(BloomFilter vLID, double weight) {
        this.VLID = vLID;
        this.weight = weight;
    }
    
    public double getWeight() {
        return weight;
    }

    public BloomFilter getVLID() {
        return VLID;
    }

    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = VLID.length() + Util.SIZEOF_DOUBLE;
        }
        return LENGTH;
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        VLID = BloomFilter.parseByteBuffer(buff);
        weight = buff.getDouble();
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        VLID.writeTo(buff);
        buff.putDouble(weight);
    }

    public static KeepAliveMessage parseByteArray(byte[] data) {
        KeepAliveMessage message = new KeepAliveMessage();
        message.fromBytes(data);
        return message;
    }
    
    public static KeepAliveMessage parseByteBuffer(ByteBuffer buffer) {
        KeepAliveMessage message = new KeepAliveMessage();
        message.readBuffer(buffer);
        return message;
    }
}

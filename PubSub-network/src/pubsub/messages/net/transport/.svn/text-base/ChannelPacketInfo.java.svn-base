package pubsub.messages.net.transport;

import java.nio.ByteBuffer;
import pubsub.BaseSerializableStruct;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class ChannelPacketInfo extends BaseSerializableStruct {
    private int id;
    private byte[] data;

    public ChannelPacketInfo(int id, byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Invalid arguments");
        }
        this.id = id;
        this.data = data;
    }

    private ChannelPacketInfo() { }

    @Override
    public int getSerializedLength() {
        return data.length + 2 * Util.SIZEOF_INT;
    }

    public byte[] getData() {
        return data;
    }

    public int getId() {
        return id;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        buff.putInt(id);
        buff.putInt(data.length);
        buff.put(data);
    }

    @Override
    public void readBuffer(ByteBuffer buffer) {
        id = buffer.getInt();
        int length = buffer.getInt();
        data = new byte[length];
        buffer.get(data);
    }

    public static ChannelPacketInfo parseByteBuffer(ByteBuffer buffer) {
        ChannelPacketInfo info = new ChannelPacketInfo();
        info.readBuffer(buffer);
        return info;
    }
}

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
public class ChannelPacketMessage extends NetMessage {

    private ChannelPacketInfo packetInfo;
    private int seq;

    public ChannelPacketMessage(PubSubID sid, PubSubID rid, ForwardIdentifier fid, int seq, ChannelPacketInfo packetInfo) {
        super(MessageType.Type.CHANNEL_MESSAGE, sid, rid, fid);
        this.packetInfo = packetInfo;
        this.seq = seq;
    }

    private ChannelPacketMessage() {
        this(null, null, null, -1, null);
    }

    @Override
    public int getSerializedLength() {
        return super.getSerializedLength() + packetInfo.getSerializedLength() + Util.SIZEOF_INT;
    }

    @Override
    public void writeTo(ByteBuffer buffer) {
        super.writeTo(buffer);
        buffer.putInt(seq);
        packetInfo.writeTo(buffer);
    }

    @Override
    public void readBuffer(ByteBuffer buffer) {
        super.readBuffer(buffer);
        seq = buffer.getInt();
        packetInfo = ChannelPacketInfo.parseByteBuffer(buffer);
    }

    public int getSEQ() {
        return this.seq;
    }

    public ChannelPacketInfo getChannelPacketInfo() {
        return this.packetInfo;
    }

    public static ChannelPacketMessage parseByteBuffer(ByteBuffer buffer) {
        ChannelPacketMessage msg = new ChannelPacketMessage();
        msg.readBuffer(buffer);
        return msg;
    }

    public static ChannelPacketMessage parseByteArray(byte[] data) {
        ChannelPacketMessage msg = new ChannelPacketMessage();
        msg.fromBytes(data);
        return msg;
    }
}

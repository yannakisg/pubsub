package pubsub.messages.net.rva;

import java.nio.ByteBuffer;
import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.messages.RVAAnnouncement;
import pubsub.messages.MessageType;
import pubsub.util.Util;

/**
 * 
 * @autor John Gasparis
 */
public class RVA2RVANetMessage extends BaseRVANetMessage {

    public enum ForwardMessageSource {

        GW((byte) -10),
        HOST((byte) -11);
        private byte index;

        private ForwardMessageSource(byte index) {
            this.index = index;
        }

        public byte getMessageSource() {
            return this.index;
        }

        public static ForwardMessageSource findBy(byte i) {
            ForwardMessageSource msgSrc = null;

            for (ForwardMessageSource m : values()) {
                if (m.getMessageSource() == i) {
                    msgSrc = m;
                    break;
                }
            }

            return msgSrc;
        }

        public static byte findBy(ForwardMessageSource msgSrc) {
            byte b = 0;

            for (ForwardMessageSource m : values()) {
                if (m == msgSrc) {
                    return b;
                }
                b++;
            }

            return -1;
        }
    }
    private ForwardIdentifier gwToHost;
    private ForwardIdentifier hostToGW;
    private RVAAnnouncement message;
    private ForwardMessageSource msgSrcType;
    private ByteIdentifier nodeSenderID;
    private ByteIdentifier hostID;
    private static int LENGTH = -1;

    public RVA2RVANetMessage(RVAAnnouncement message, ByteIdentifier nodeID, ForwardMessageSource msgSrcType, ForwardIdentifier hostToGW, ForwardIdentifier fid) {
        super(MessageType.Type.RVA_FORWARD_NET_MESSAGE, fid);

        this.message = message;
        this.nodeSenderID = nodeID;
        this.msgSrcType = msgSrcType;
        this.hostToGW = hostToGW;

        this.gwToHost = null;
        this.hostID = null;
    }

    private RVA2RVANetMessage() {
        super(MessageType.Type.RVA_FORWARD_NET_MESSAGE, null);
        this.message = null;
    }

    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = super.getSerializedLength() + message.getSerializedLength()
                    + nodeSenderID.getSerializedLength() + Util.SIZEOF_BYTE + hostToGW.getSerializedLength();
        }
        return LENGTH + (gwToHost != null ? gwToHost.getSerializedLength() : 0) + (hostID != null ? hostID.getSerializedLength() : 0);
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        message.writeTo(buff);

        buff.put(msgSrcType.getMessageSource());
        nodeSenderID.writeTo(buff);

        hostToGW.writeTo(buff);

        if (gwToHost != null) {
            gwToHost.writeTo(buff);
        }

        if (hostID != null) {
            hostID.writeTo(buff);
        }
    }

    @Override
    public void readBuffer(ByteBuffer buffer) {
        super.readBuffer(buffer);

        message = RVAAnnouncement.parseByteBuffer(buffer);

        msgSrcType = ForwardMessageSource.findBy(buffer.get());
        nodeSenderID = ByteIdentifier.parseByteBuffer(buffer);

        hostToGW = ForwardIdentifier.parseByteBuffer(buffer);

        if (buffer.hasRemaining()) {
            gwToHost = ForwardIdentifier.parseByteBuffer(buffer);
        }

        if (buffer.hasRemaining()) {
            hostID = ByteIdentifier.parseByteBuffer(buffer);
        }
    }

    public static ForwardMessageSource getMessageSource(byte[] data, int index) {
        if (data == null || index >= data.length || index < 0) {
            throw new IllegalArgumentException();
        }

        return ForwardMessageSource.findBy(data[index]);
    }

    public ForwardMessageSource getMessageSource() {
        return this.msgSrcType;
    }

    public void setMessageSource(ForwardMessageSource msgSrcType) {
        this.msgSrcType = msgSrcType;
    }

    public ByteIdentifier getNodeSenderID() {
        return this.nodeSenderID;
    }

    public void setNodeSenderID(ByteIdentifier nodeID) {
        this.nodeSenderID = nodeID;
    }

    public RVAAnnouncement getRVAAnnouncement() {
        return this.message;
    }

    public ForwardIdentifier getGWtoHost() {
        return this.gwToHost;
    }

    public void setGWtoHost(ForwardIdentifier fid) {
        this.gwToHost = fid;
    }

    public ForwardIdentifier getHostToGW() {
        return this.hostToGW;
    }

    public void setHostID(ByteIdentifier id) {
        this.hostID = id;
    }

    public ByteIdentifier getHostID() {
        return this.hostID;
    }

    public static RVA2RVANetMessage parseByteBuffer(ByteBuffer buffer) {
        RVA2RVANetMessage msg = new RVA2RVANetMessage();
        msg.readBuffer(buffer);
        return msg;
    }
}

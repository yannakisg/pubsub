package pubsub.messages;

import java.nio.ByteBuffer;
import pubsub.BaseSerializableStruct;
import pubsub.localrendezvous.LocRCClient;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public abstract class Message extends BaseSerializableStruct {

    private MessageType.Type msgType;

    public Message(MessageType.Type msgType) {
        this.msgType = msgType;
    }

    public void setMessageType(MessageType.Type msgType) {
        this.msgType = msgType;
    }

    public MessageType.Type getMessageType() {
        return this.msgType;
    }

    public static void setMessageType(byte[] data, MessageType.Type msgType) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.put(0, msgType.getType());
    }

    public static MessageType.Type getMessageType(byte b) {
        return MessageType.Type.findBy(b);
    }

    @Override
    public int getSerializedLength() {
        return Util.SIZEOF_BYTE;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        buff.put(msgType.getType());
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        msgType = MessageType.Type.findBy(buff.get());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Message other = (Message) obj;
        if (this.msgType != other.msgType) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.msgType != null ? this.msgType.hashCode() : 0);
        return hash;
    }

    public abstract void publishMutableData(LocRCClient locRCClient, byte[] data);
}

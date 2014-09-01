package pubsub.messages.ipc.tmc;

import java.nio.ByteBuffer;
import pubsub.bloomfilter.BloomFilter;
import pubsub.messages.MessageType;

/**
 *
 * @author John Gasparis
 */
public class LIDMessage extends TMCIPCMessage {

    private BloomFilter lid;

    public LIDMessage(BloomFilter lid) {
        super(MessageType.Type.GET_LID);
        this.lid = lid;
    }

    private LIDMessage() {
        this(null);
    }

    @Override
    public int getSerializedLength() {
        return super.getSerializedLength() + (lid != null ? lid.getSerializedLength() : 0);
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);

        if (lid != null) {
            lid.writeTo(buff);
        }
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);
        if (buff.hasRemaining()) {
            lid = BloomFilter.parseByteBuffer(buff);
        }
    }

    public BloomFilter getLID() {
        return lid;
    }

    public static LIDMessage parseByteBuffer(ByteBuffer buff) {
        LIDMessage msg = new LIDMessage();
        msg.readBuffer(buff);
        return msg;
    }
}

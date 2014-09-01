package pubsub.messages.ipc.tmc;

import java.nio.ByteBuffer;
import pubsub.ForwardIdentifier;
import pubsub.messages.MessageType;

/**
 *
 * @author John Gasparis
 */
public class FIDMessage extends TMCIPCMessage {

    private ForwardIdentifier fid;
    private static int LENGTH = -1;

    public FIDMessage(MessageType.Type msgType, ForwardIdentifier fid) {
        super(msgType);

        if (msgType != MessageType.Type.GET_FID
                && msgType != MessageType.Type.GET_FID_A_B
                && msgType != MessageType.Type.GET_FID_HOST) {
            throw new IllegalArgumentException("Illegal MessageType");
        }

        this.fid = fid;
    }

    private FIDMessage() {
        super(MessageType.Type.NOTHING);
        this.fid = null;
    }

    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = super.getSerializedLength() + fid.getSerializedLength();
        }
        return LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        fid.writeTo(buff);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);
        fid = ForwardIdentifier.parseByteBuffer(buff);
    }

    public ForwardIdentifier getFID() {
        return fid;
    }

    public static FIDMessage parseByteBuffer(ByteBuffer buff) {
        FIDMessage msg = new FIDMessage();
        msg.readBuffer(buff);
        return msg;
    }
}

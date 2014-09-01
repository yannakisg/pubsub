package pubsub.messages.ipc.tmc;

import java.nio.ByteBuffer;
import pubsub.ByteIdentifier;
import pubsub.messages.MessageType;

/**
 *
 * @author John Gasparis
 */
public class NodeIDMessage extends TMCIPCMessage {

    private static NodeIDMessage instance = null;
    private ByteIdentifier myNodeID;
    private static int LENGTH = -1;

    private NodeIDMessage(ByteIdentifier myNodeID) {
        super(MessageType.Type.GET_MYNODE_ID);
        this.myNodeID = myNodeID;
    }

    private NodeIDMessage() {
        this(null);
    }

    public static NodeIDMessage getInstance(ByteIdentifier myNodeID) {
        if (instance == null) {
            instance = new NodeIDMessage(myNodeID);
        }
        return instance;
    }

    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = super.getSerializedLength() + myNodeID.getSerializedLength();
        }
        return LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        myNodeID.writeTo(buff);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);
        myNodeID = ByteIdentifier.parseByteBuffer(buff);
    }

    public ByteIdentifier getMyNodeID() {
        return myNodeID;
    }

    public static NodeIDMessage parseByteBuffer(ByteBuffer buff) {
        NodeIDMessage msg = new NodeIDMessage();
        msg.readBuffer(buff);
        return msg;
    }
}

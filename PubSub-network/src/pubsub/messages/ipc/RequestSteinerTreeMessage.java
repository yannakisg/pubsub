package pubsub.messages.ipc;

import java.nio.ByteBuffer;
import pubsub.ByteIdentifier;
import pubsub.PubSubID;
import pubsub.messages.MessageType;
import pubsub.tmc.TMCUtil;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class RequestSteinerTreeMessage extends IPCMessage {
    private ByteIdentifier[] steinerPoints;
    private static final PubSubID TMC_SID = TMCUtil.TMC_SID;
    private static final PubSubID TMC_RID = TMCUtil.TMC_RID;
    
    private RequestSteinerTreeMessage() {
        super(MessageType.Type.NOTHING, TMC_SID, TMC_RID);
    }
    
    public RequestSteinerTreeMessage(ByteIdentifier[] steinerPoints) {
        super (MessageType.Type.REQUEST_STEINER_TREE, TMC_SID, TMC_RID);
        
        if (steinerPoints ==  null || steinerPoints.length == 0) {
            throw new IllegalArgumentException("Empty or null array");
        }
        
        this.steinerPoints = steinerPoints;
    }
    
    public ByteIdentifier[] getSteinerPoints() {
        return this.steinerPoints;
    }
    
    @Override
    public int getSerializedLength() {        
        return super.getSerializedLength() + Util.SIZEOF_INT + (steinerPoints.length * steinerPoints[0].getSerializedLength());
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        
        buff.putInt(steinerPoints.length);
        for (ByteIdentifier id : steinerPoints) {
            id.writeTo(buff);
        }
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);
        int total = buff.getInt();
        
        steinerPoints = new ByteIdentifier[total];
        for (int i = 0; i < total; i++) {
            steinerPoints[i] = ByteIdentifier.parseByteBuffer(buff);
        }
    }
    
    public static RequestSteinerTreeMessage parseByteBuffer(ByteBuffer buffer) {
        RequestSteinerTreeMessage msg = new RequestSteinerTreeMessage();
        msg.readBuffer(buffer);
        return msg;
    }
}

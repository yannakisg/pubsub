package pubsub.messages.net.tmc;

import java.nio.ByteBuffer;
import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.PubSubID;
import pubsub.messages.MessageType;
import pubsub.messages.net.NetMessage;
import pubsub.tmc.TMCUtil;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public abstract class TMCNetMessage extends NetMessage {

    private static final PubSubID TMC_SID = TMCUtil.TMC_SID;
    private static final PubSubID TMC_RID = TMCUtil.TMC_RID;
    private ByteIdentifier routerID;
    
    public TMCNetMessage(MessageType.Type msgType, ForwardIdentifier fid, ByteIdentifier routerID) {
        super(msgType, TMC_SID, TMC_RID, fid);

        this.routerID = routerID;
    }

    protected TMCNetMessage() {
        super(MessageType.Type.NOTHING, TMC_SID, TMC_RID);
    }

    public ByteIdentifier getRouterID() {
        return this.routerID;
    }

    public void setRouterID(ByteIdentifier routerID) {
        this.routerID = routerID;
    }
    
    public static ByteIdentifier findRouterID(ByteBuffer buffer) {
        int prevPosition = buffer.position();
        buffer.position(prevPosition + Util.SIZEOF_BYTE + Util.SIZEOF_INT);
        ByteIdentifier routerID = ByteIdentifier.parseByteBuffer(buffer);
        buffer.position(prevPosition);
        return routerID;
    }

    @Override
    public int getSerializedLength() {
        return super.getSerializedLength() + routerID.getSerializedLength();
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        routerID.writeTo(buff);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);
        routerID = ByteIdentifier.parseByteBuffer(buff);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TMCNetMessage other = (TMCNetMessage) obj;
        if (this.routerID != other.routerID && (this.routerID == null || !this.routerID.equals(other.routerID))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.routerID != null ? this.routerID.hashCode() : 0);
        return hash;
    }
}

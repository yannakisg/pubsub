package pubsub.messages.ipc;

import java.nio.ByteBuffer;
import pubsub.ByteIdentifier;
import pubsub.PubSubID;
import pubsub.Publication;
import pubsub.localrendezvous.TimeOutLocRCClient;
import pubsub.messages.MessageType;
import pubsub.tmc.TMCUtil;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class IPCTMCInterestMessage extends IPCMessage {

    private ByteIdentifier idA;
    private ByteIdentifier idB;
    private boolean includeDest;
    private static final PubSubID TMC_SID = TMCUtil.TMC_SID;
    private static final PubSubID TMC_RID = TMCUtil.TMC_RID;

    private IPCTMCInterestMessage() {
        this(MessageType.Type.NOTHING);
    }

    public IPCTMCInterestMessage(MessageType.Type msgType) {
        super(msgType, TMC_SID, TMC_RID);
        this.idA = this.idB = null;
        this.includeDest = true;
    }

    public IPCTMCInterestMessage(MessageType.Type msgType, ByteIdentifier idA, ByteIdentifier idB, boolean includeDest) {
        super(msgType, TMC_SID, TMC_RID);
        this.idA = idA;
        this.idB = idB;
        this.includeDest = includeDest;
    }

    public IPCTMCInterestMessage(MessageType.Type msgType, ByteIdentifier idA, boolean includeDest) {
        super(msgType, TMC_SID, TMC_RID);
        this.idA = idA;
        this.idB = null;
        this.includeDest = includeDest;
    }

    public ByteIdentifier getIDA() {
        return this.idA;
    }

    public ByteIdentifier getIDB() {
        return this.idB;
    }

    public boolean includeDest() {
        return this.includeDest;
    }

    @Override
    public int getSerializedLength() {
        if (idA == null && idB == null) {
            return super.getSerializedLength() + Util.SIZEOF_BYTE;
        } else if (idA != null && idB == null) {
            return super.getSerializedLength() + idA.getSerializedLength() + Util.SIZEOF_BYTE;
        } else {
            return super.getSerializedLength() + idA.getSerializedLength() + idB.getSerializedLength() + Util.SIZEOF_BYTE;
        }
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        buff.put(includeDest ? (byte) 1 : (byte) 0);

        if (idA != null) {
            idA.writeTo(buff);

            if (idB != null) {
                idB.writeTo(buff);
            }
        }
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);

        includeDest = buff.get() == (byte) 1;

        if (buff.remaining() > 0) {
            idA = ByteIdentifier.parseByteBuffer(buff);

            if (buff.remaining() > 0) {
                idB = ByteIdentifier.parseByteBuffer(buff);
            }
        }
    }

    public static IPCTMCInterestMessage parseByteBuffer(ByteBuffer buff) {
        IPCTMCInterestMessage msg = new IPCTMCInterestMessage();
        msg.readBuffer(buff);
        return msg;
    }

    public void publish(TimeOutLocRCClient locRCClient, byte[] data) {
        Publication pub = Publication.createMutableData(TMC_SID, TMC_RID, data);
        locRCClient.publish(pub);
    }
}

package pubsub.messages.net.tmc;

import java.nio.ByteBuffer;
import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.messages.MessageType;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class InterestTopologyChunk extends TMCNetMessage {

    private int chunkNum;

    private InterestTopologyChunk() {
        super();
    }

    public InterestTopologyChunk(int chunkNum, ByteIdentifier myRouterID, ForwardIdentifier fid) {
        super(MessageType.Type.TOPOLOGY_INTEREST_CHUNK, fid, myRouterID);

        this.chunkNum = chunkNum;
    }

    @Override
    public int getSerializedLength() {
        return Util.SIZEOF_INT + super.getSerializedLength();
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);

        buff.putInt(chunkNum);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);

        chunkNum = buff.getInt();
    }

    public int getChunkNum() {
        return chunkNum;
    }

    public void setChunkNum(int chunkNum) {
        this.chunkNum = chunkNum;
    }

    public static InterestTopologyChunk parseByteBuffer(ByteBuffer buffer) {
        InterestTopologyChunk msg = new InterestTopologyChunk();
        msg.readBuffer(buffer);
        return msg;
    }

    public static InterestTopologyChunk createEmptyMessage() {
        return new InterestTopologyChunk();
    }
}

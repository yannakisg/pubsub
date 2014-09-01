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
public class TopologyMessage extends TMCNetMessage {

    
    private byte[] array;
    private int chunkNum;
    private int totalChunks;
    
    private static final int LENGTH = (Util.SIZEOF_INT << 2);
    
    private TopologyMessage() {
        super(MessageType.Type.TOPOLOGY_MESSAGE, null, null);
    }
    
    public TopologyMessage(byte[] array, int chunkNum, int totalChunks, ByteIdentifier routerID, ForwardIdentifier fid) {
        super(MessageType.Type.TOPOLOGY_MESSAGE, fid, routerID);
        
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("Invalid bytebufer");
        }
        
        this.array = array;
        this.chunkNum = chunkNum;
        this.totalChunks = totalChunks;        
    }
    
    @Override
    public int getSerializedLength() {
        return LENGTH + super.getSerializedLength() + array.length;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        
        buff.putInt(chunkNum);
        buff.putInt(totalChunks);
        buff.putInt(array.length);
        buff.put(array);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);

        chunkNum = buff.getInt();
        totalChunks = buff.getInt();
        
        int len = buff.getInt();
        this.array = new byte[len];
        buff.get(array);
    }

    public byte[] getArray() {
        return array;
    }

    public void setArray(byte[] array) {
        this.array = array;
    }

    public void setChunkNum(int chunkNum) {
        this.chunkNum = chunkNum;
    }

    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }

    public int getChunkNum() {
        return chunkNum;
    }

    public int getTotalChunks() {
        return totalChunks;
    }  
    
    public static TopologyMessage parseByteBuffer(ByteBuffer buff) {
        TopologyMessage msg = new TopologyMessage();
        msg.readBuffer(buff);
        return msg;
    }
    
    public static TopologyMessage createEmptyTopologyMsg() {
        return new TopologyMessage();
    }
}

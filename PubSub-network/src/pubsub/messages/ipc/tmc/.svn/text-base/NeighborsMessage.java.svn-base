package pubsub.messages.ipc.tmc;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.messages.MessageType;
import pubsub.tmc.TMCUtil;
import pubsub.util.FwdConfiguration;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class NeighborsMessage extends TMCIPCMessage {

    private Map<ByteIdentifier, ForwardIdentifier> neighbors;
    private static final int fidByteidentSize = Util.SIZEOF_SHORT + TMCUtil.SHA1_LENGTH + Util.SIZEOF_SHORT
            + FwdConfiguration.ZFILTER_LENGTH;

    public NeighborsMessage(Map<ByteIdentifier, ForwardIdentifier> neighbors) {
        super(MessageType.Type.GET_NEIGHBORS);
        this.neighbors = neighbors;
    }

    private NeighborsMessage() {
        this(null);
    }

    @Override
    public int getSerializedLength() {

        return (super.getSerializedLength() + Util.SIZEOF_INT + (neighbors.size() * fidByteidentSize));
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);

        buff.putInt(neighbors.size());
        for (Entry<ByteIdentifier, ForwardIdentifier> entry : neighbors.entrySet()) {
            entry.getKey().writeTo(buff);
            entry.getValue().writeTo(buff);
        }
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);

        int size = buff.getInt();
        ByteIdentifier tempID;
        ForwardIdentifier tempFID;
        neighbors = new HashMap<ByteIdentifier, ForwardIdentifier>();

        for (int i = 0; i < size; i++) {
            tempID = ByteIdentifier.parseByteBuffer(buff);

            tempFID = ForwardIdentifier.parseByteBuffer(buff);

            neighbors.put(tempID, tempFID);
        }
    }

    public Map<ByteIdentifier, ForwardIdentifier> getNeighbors() {
        return neighbors;
    }

    public static NeighborsMessage parseByteBuffer(ByteBuffer buff) {
        NeighborsMessage msg = new NeighborsMessage();
        msg.readBuffer(buff);
        return msg;
    }
}

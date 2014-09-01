package pubsub;

import java.nio.ByteBuffer;

/**
 *
 * @author tsilo
 */
public interface SerializableStruct {

    //public byte[] toBytes();

    public void fromBytes(byte[] data);

    public int getSerializedLength();

    /**
     * writes <em>getSerializedLength</em> bytes to this buffer
     * @param buff
     */
    public void writeTo(ByteBuffer buff);

    /**
     * reads <em>getSerializedLength</em> from this buffer and un-marshals itself
     * @param buff
     */
    public void readBuffer(ByteBuffer buff);
}

package pubsub;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

/**
 *
 * @author tsilo
 */
public abstract class BaseSerializableStruct implements SerializableStruct {
    private ByteBuffer buffer = null;
    
    @Override
    public void fromBytes(byte[] data) {
        buffer = ByteBuffer.wrap(data);
        readBuffer(buffer);
        buffer.flip();
    }

    public byte[] toBytes() {
        if (buffer == null) {
            buffer = ByteBuffer.allocate(getSerializedLength());
        } else {
            buffer.clear();
        }
        
        try {
            writeTo(buffer);
        } catch (BufferOverflowException ex) {
            buffer = ByteBuffer.allocate(getSerializedLength());
            writeTo(buffer);  
        } catch (IllegalArgumentException ex) {
            buffer = ByteBuffer.allocate(getSerializedLength());
            writeTo(buffer);
        }
        
        return buffer.array();
    }
}

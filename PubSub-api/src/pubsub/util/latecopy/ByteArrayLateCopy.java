package pubsub.util.latecopy;

import java.nio.ByteBuffer;

public class ByteArrayLateCopy extends LateCopy<byte[]> {

    public ByteArrayLateCopy(ByteBuffer buffer, int offset, int length) {
        super(buffer, offset, length);

    }

    @Override
    protected byte[] parseBuffer() {
        byte[] data = new byte[length];
        buffer.get(data);
        return data;
    }

    public ByteBuffer getBufferDuplicate() {
        ByteBuffer duplicate = this.buffer.duplicate();
        duplicate.position(offset);
        duplicate.limit(offset + length);
        return duplicate;
    }

    public byte getByte(int i) {
        int currentpos = buffer.position();
        byte value = buffer.get(offset + i);
        buffer.position(currentpos);

        return value;
    }
}

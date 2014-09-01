package pubsub.util.latecopy;

import java.nio.ByteBuffer;

public abstract class LateCopy<T> {

    protected final ByteBuffer buffer;
    protected final int offset;
    protected final int length;
    private T value = null;

    public LateCopy(ByteBuffer buffer, int offset, int length) {
        this.buffer = buffer;
        this.offset = offset;
        this.length = length;
    }

    public T getValue() {
        if (value == null) {
            int oldPosition = buffer.position();
            buffer.position(offset);

            value = parseBuffer();

            buffer.position(oldPosition);
        }
        return value;
    }

    protected abstract T parseBuffer();

    public int length() {
        return length;
    }

    public void copyToBuffer(ByteBuffer dest) {
        int oldPos = this.buffer.position();
        int oldlimit = this.buffer.limit();

        this.buffer.position(offset);
        this.buffer.limit(offset + length);
        
        
        try {
            dest.put(this.buffer);
        } catch (IllegalArgumentException iae) {            
        }

        this.buffer.position(oldPos);
        this.buffer.limit(oldlimit);
    }
}

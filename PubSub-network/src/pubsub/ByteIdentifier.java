package pubsub;

import java.nio.ByteBuffer;
import java.util.Arrays;
import pubsub.tmc.TMCUtil;
import pubsub.util.Util;

/**
 * An identifier represented as a byte array, up to Short.MAX_VALUE length
 * 
 * @author Tsilochr
 * 
 */
public class ByteIdentifier extends BaseSerializableStruct {

    private static final short MAX_LEN = Short.MAX_VALUE;
    private Integer hashCode = null;
    private byte[] id;

    public ByteIdentifier() {
        this.id = null;
    }

    public ByteIdentifier(byte[] id) {
        if (id.length > MAX_LEN) {
            throw new IllegalArgumentException("byte array too long. Up to "
                    + MAX_LEN + " supported");
        }
        this.id = id;
    }

    public byte[] getId() {
        return id;
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            final int prime = 31;
            hashCode = prime + Arrays.hashCode(id);
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ByteIdentifier other = (ByteIdentifier) obj;
        if (!Arrays.equals(id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int getSerializedLength() {
        short len = (short) this.id.length;
        return Util.SIZEOF_SHORT + len;
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        short len = buff.getShort();
        byte[] data = new byte[len];
        buff.get(data);
        id = data;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        short len = (short) this.id.length;
        buff.putShort(len);
        buff.put(id);
    }

    @Override
    public String toString() {
        return TMCUtil.byteArrayToString(id);
    }

    public static ByteIdentifier parseByteBuffer(ByteBuffer buffer) {
        ByteIdentifier bID = new ByteIdentifier();
        bID.readBuffer(buffer);
        return bID;
    }

    public static ByteIdentifier parseByteArray(byte[] data) {
        ByteIdentifier bID = new ByteIdentifier();
        bID.fromBytes(data);
        return bID;
    }
}

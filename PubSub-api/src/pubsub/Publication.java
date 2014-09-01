package pubsub;

import static pubsub.util.Util.checkNull;

import java.nio.ByteBuffer;
import java.util.Arrays;

import pubsub.util.Util;
import pubsub.util.latecopy.ByteArrayLateCopy;

/**
 * @author tsilo
 * @author John Gasparis
 */
public class Publication extends BasePubSubPacket {

    private byte[] data;
    private ByteArrayLateCopy dataLateCopy;

    private Publication() {
    }

    private Publication(PubSubID scopeId, PubSubID rId, ContentType type,
            byte[] data) {
        checkNull(scopeId);
        checkNull(rId);
        checkNull(type);
        checkNull(data);

        setScopeId(scopeId);
        setRendezvousId(rId);
        setContentType(type);

        this.data = data;
    }
    
    public byte[] getDuplicateDataArray() {
        if (data != null) {
            return Arrays.copyOfRange(data, 0, data.length);
        } else {
            byte[] d = this.dataLateCopy.getBufferDuplicate().array();
            return Arrays.copyOfRange(d, 0, d.length);
        }
    }

    public byte[] getDataArray() {
        if (data == null) {
            data = dataLateCopy.getValue();
        }
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getDataLength() {
        if (data != null) {
            return data.length;
        } else {
            return dataLateCopy.length();
        }
    }

    public ByteBuffer wrapData() {
        ByteBuffer buf = null;
        if (data != null) {
            buf = ByteBuffer.wrap(data);
        } else {
            buf = this.dataLateCopy.getBufferDuplicate();
        }
        return buf;
    }

    public static Publication parseByteBuffer(ByteBuffer buffer) {
        Publication publication = new Publication();
        publication.readBuffer(buffer);
        return publication;
    }

    @Override
    public int getSerializedLength() {
        int length = Util.SIZEOF_BYTE + this.scopeId.getSerializedLength()
                + this.rendezvousId.getSerializedLength();

        if (data != null) {
            length += data.length;
        } else {
            length += this.dataLateCopy.length();
        }

        return length;
    }

    @Override
    public void writeTo(ByteBuffer buffer) {
        buffer.put(cType.byteValue());
        scopeId.writeTo(buffer);
        rendezvousId.writeTo(buffer);
        if (data != null) {
            buffer.put(data);
        } else {
            dataLateCopy.copyToBuffer(buffer);
        }
    }

    @Override
    public void readBuffer(ByteBuffer buffer) {
        cType = ContentType.getType(buffer.get());
        scopeId = PubSubID.parseByteBuffer(buffer);
        rendezvousId = PubSubID.parseByteBuffer(buffer);

        this.dataLateCopy = new ByteArrayLateCopy(buffer, buffer.position(), buffer.remaining());
        this.data = null;
    }

    public static Publication createImmutableData(PubSubID scope, PubSubID rid, byte[] data) {
        return new Publication(scope, rid, ContentType.IMMUTABLE_DATA, data);
    }

    public static Publication createMutableData(PubSubID scope, PubSubID rid, byte[] data) {
        return new Publication(scope, rid, ContentType.MUTABLE_DATA, data);
    }

    public static Publication parseByteArray(byte[] data) {
        Publication publication = new Publication();
        publication.fromBytes(data);
        return publication;
    }

    public static Publication createEmpty() {
        PubSubID scId = new PubSubID(new byte[PubSubID.ID_LENGTH]);
        PubSubID rndId = new PubSubID(new byte[PubSubID.ID_LENGTH]);
        return new Publication(scId, rndId, ContentType.LOCAL, new byte[0]);
    }

    public static Publication createPublication(PubSubID scope, PubSubID rid, ContentType cType, byte[] data) {
        return new Publication(scope, rid, cType, data);
    }

    public byte getByteAt(int i) {
        if (data != null) {
            return data[i];
        } else {
            return this.dataLateCopy.getByte(i);
        }
    }
}

package pubsub;

import java.nio.ByteBuffer;
import static pubsub.util.Util.checkNull;
import java.util.Arrays;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import pubsub.util.Util;

/**
 *
 * @author tsilo
 * @author John Gasparis
 */
public class PubSubID extends BaseSerializableStruct {

    public static final int ID_LENGTH = 32;
    private byte[] id;
    private Integer hashCode = null;
    private String stringRepresentation = null;

    /**
     * @param id
     *            the id
     * @throws Nullpointer
     *             exception if id is null
     * @throws IllegalArgumentException
     *             if id length is not PSIRP_ID_LENGTH
     */
    public PubSubID(byte[] id) {
        setId(id);
    }

    private PubSubID() {
        this.id = null;
    }

    public byte[] getId() {
        return id;
    }

    /**
     * @throws NullpointerException
     *             if id is null
     * @throws IllegalArgumentException
     *             if id length is not PSIRP_ID_LENGTH
     */
    private void setId(byte[] id) {
        checkNull(id);

        if (id.length != ID_LENGTH) {
            throw new IllegalArgumentException("invalid psirp id lenght: "
                    + id.length);
        }

        this.id = id;
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
        PubSubID other = (PubSubID) obj;
        if (!Arrays.equals(id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (this.stringRepresentation == null) {
            computeString();
        }
        return this.stringRepresentation;
    }

    /**
     * Computes hexademical representation of this identifier
     */
    private void computeString() {
        char[] chars = Hex.encodeHex(id);
        this.stringRepresentation = new String(chars);
    }

    public static PubSubID create(byte i) {
        byte[] b = new byte[PubSubID.ID_LENGTH];
        Arrays.fill(b, i);
        return new PubSubID(b);
    }

    public static PubSubID fromHexString(String str) {
        Util.checkNull(str);

        String val = "";
        if (str.length() % 2 != 0) {
            val += "0";
        }
        val += str;
        byte[] decoded;
        try {
            decoded = Hex.decodeHex(val.toCharArray());
            byte[] data = resize(decoded);
            return new PubSubID(data);
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] resize(byte[] decoded) {
        byte[] retval = new byte[ID_LENGTH];
        Arrays.fill(retval, (byte) 0);

        int startpos = Math.max(0, retval.length - decoded.length);
        int length = Math.min(retval.length, decoded.length);

        System.arraycopy(decoded, 0, retval, startpos, length);
        return retval;
    }

    public static PubSubID createRandom() {
        byte[] bytes = Util.getRandomBytes(ID_LENGTH);

        return new PubSubID(bytes);
    }

    @Override
    public int getSerializedLength() {
        return ID_LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        buff.put(id);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        id = new byte[ID_LENGTH];
        buff.get(id);
    }

    public static PubSubID parseByteBuffer(ByteBuffer buff) {
        PubSubID pubsub = new PubSubID();
        pubsub.readBuffer(buff);
        return pubsub;
    }

    public static PubSubID parseByteArray(byte[] data) {
        PubSubID pubsub = new PubSubID();
        pubsub.fromBytes(data);
        return pubsub;
    }
}

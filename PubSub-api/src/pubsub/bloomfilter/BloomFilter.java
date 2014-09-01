package pubsub.bloomfilter;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import java.util.Set;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.BinaryCodec;
import org.apache.commons.codec.binary.Hex;

import pubsub.BaseSerializableStruct;
import pubsub.hashing.HashFunction;
import pubsub.hashing.HashFunctions;
import pubsub.util.FwdConfiguration;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 * @author tsilo
 */
public class BloomFilter extends BaseSerializableStruct {

    private byte[] bytes;
    private Integer hashCode = null;
    
    static final MessageDigest digestFunction;
    static {
        MessageDigest tmp;
        try {
            tmp = java.security.MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            tmp = null;
        }
        digestFunction = tmp;
    }

    public BloomFilter(byte[] data) {
        setBytes(data);
    }

    private BloomFilter() {
        this.bytes = null;
    }
    
    public static void add(BloomFilter bl, byte[] data, int bits) {
        Util.checkNull(data);
        if (data.length == 0) {
            throw new IllegalArgumentException("invalid byte array");
        }
        
        byte[] temp = new byte[data.length + 1];
        System.arraycopy(data, 0, temp, 0, data.length);
        long h;
        int pos;
        for (int x = 0; x < bits; x++) {
            temp[data.length] = (byte) (x + 1);
            h = hashValue(temp) % FwdConfiguration.ZFILTER_LENGTH_BITS;
            pos = (int) h;
            setBit(bl.bytes, Math.abs(pos), 1);
        }
    }

    public static BloomFilter hashBytes(byte[] data, int bits) {
        Util.checkNull(data);
        if (data.length == 0) {
            throw new IllegalArgumentException("invalid byte array");
        }
        byte[] bytes = hash(data, bits);
        return new BloomFilter(bytes);
    }

    private static void setBit(byte[] data, int pos, int val) {
        int bytePos = pos / 8;
        int bitPos = pos % 8;
        byte oldByte = data[bytePos];
        oldByte = (byte) (((0xFF7F >> bitPos) & oldByte) & 0x00FF);
        byte newByte = (byte) ((val << (8 - (bitPos + 1))) | oldByte);
        data[bytePos] = newByte;
    }

    private static byte[] hash(byte[] data, int nubOfBits) {
        int pos;
        byte i = 1;
        byte[] retVal = new byte[FwdConfiguration.ZFILTER_LENGTH];
        Arrays.fill(retVal, (byte) 0);

        byte[] temp = new byte[data.length + 1];
        System.arraycopy(data, 0, temp, 0, data.length);

        List<HashFunction> list = HashFunctions.getFunctions();
        int howmany = nubOfBits;//Math.max(nubOfBits, list.size());

        long h;
        for (int x = 0; x < howmany; x++) {
//            HashFunction f = list.get(x);
            temp[data.length] = i++;
           // pos = f.hash(temp) % FwdConfiguration.ZFILTER_LENGTH_BITS;
            h = hashValue(temp) % FwdConfiguration.ZFILTER_LENGTH_BITS;
            pos = (int )h;
            setBit(retVal, Math.abs(pos), 1);
        }

        return retVal;
    }
    
    
    private static long hashValue(byte[] data) {
        long h = 0;
        byte[] res;

        synchronized (digestFunction) {
            res = digestFunction.digest(data);
        }
        
        for (int i = 0; i < 4; i++) {
            h <<= 8;
            h |= ((int) res[i]) & 0xFF;
        }
        return h;
    }

    public void or(BloomFilter other) {
        checkEqualSize(other);

        int length = this.length();
        byte[] otherBytes = other.getBytes();

        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) (this.bytes[i] | otherBytes[i]);
        }
    }

    private BloomFilter OR(BloomFilter other) {
        checkEqualSize(other);

        byte[] b = new byte[this.length()];
        int length = this.length();

        for (int i = 0; i < length; i++) {
            b[i] = (byte) (this.bytes[i] | other.bytes[i]);
        }

        return new BloomFilter(b);
    }

    private BloomFilter AND(BloomFilter other) {
        checkEqualSize(other);

        byte[] b = new byte[this.length()];
        int length = this.length();

        for (int i = 0; i < length; i++) {
            b[i] = (byte) (this.bytes[i] & other.bytes[i]);
        }

        return new BloomFilter(b);
    }

    public static BloomFilter OR(BloomFilter lhs, BloomFilter rhs) {
        return lhs.OR(rhs);
    }

    public static boolean ANDf(BloomFilter lhs, BloomFilter rhs) {
        byte[] lhsBytes = lhs.bytes;
        byte[] rhsBytes = rhs.bytes;
        byte value;

        for (int i = 0; i < lhsBytes.length; i++) {
            value = (byte) (lhsBytes[i] & rhsBytes[i]);

            if (value != lhsBytes[i]) {
                return false;
            }
        }

        return true;
    }

    public static boolean ANDs(BloomFilter lhs, BloomFilter rhs) {
        return ANDf(rhs, lhs);
    }

    public static BloomFilter createZero() {

        byte[] bytes = new byte[FwdConfiguration.ZFILTER_LENGTH];
        Arrays.fill(bytes, (byte) 0);

        return new BloomFilter(bytes);
    }

    public static BloomFilter parseByteBuffer(ByteBuffer buffer) {
        BloomFilter bl = new BloomFilter();
        bl.readBuffer(buffer);
        return bl;
    }

    private void checkEqualSize(BloomFilter other) {
        if (other != null && this.length() != other.length()) {
            throw new IllegalArgumentException(
                    "cannot perfom action on zFilters of different lengths "
                    + this.length() + " " + other.length());
        }
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    private void setBytes(byte[] bytes) {
        Util.checkNull(bytes);
        this.bytes = bytes;
    }

    public static BloomFilter fromBinaryRepresentation(String id) {
        Util.checkNull(id);

        byte[] bytes = parseBinaryString(id);
        return new BloomFilter(bytes);
    }

    public static BloomFilter fromHexRepresentation(String id) throws DecoderException {
        Util.checkNull(id);

        byte[] bytes = parseHexString(id);
        return new BloomFilter(bytes);
    }

    public int length() {
        return this.bytes.length;
    }

    private static byte[] parseBinaryString(String id) {
        if (id.length() == 0 || id.length() % 8 != 0) {
            throw new IllegalArgumentException(
                    "string length not multiple of 8: " + id.length());
        }
        return BinaryCodec.fromAscii(id.toCharArray());
    }

    private static byte[] parseHexString(String id) throws DecoderException {
        if (id.length() == 0 || id.length() % 2 != 0) {
            throw new IllegalArgumentException("invalid string length : "
                    + id.length());
        }

        return Hex.decodeHex(id.toCharArray());
    }

    public static BloomFilter createRandom(int length, int bits) {
        byte[] b = Util.getRandomBytes(length);

        return BloomFilter.hashBytes(b, bits);
    }

    public String toBinaryString() {
        return new String(BinaryCodec.toAsciiChars(bytes));
    }

    @Override
    public String toString() {
        return Hex.encodeHexString(bytes);
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            final int prime = 31;
            hashCode = prime + Arrays.hashCode(bytes);
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
        BloomFilter other = (BloomFilter) obj;
        if (!Arrays.equals(bytes, other.getBytes())) {
            return false;
        }
        return true;
    }

    @Override
    public int getSerializedLength() {
        return this.bytes.length;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        buff.put(bytes);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        bytes = new byte[FwdConfiguration.ZFILTER_LENGTH];
        buff.get(bytes);
    }

    public static void main(String args[]) {
        Set<BloomFilter> set = new HashSet<BloomFilter>();
        BloomFilter bl0, bl1, bl2, bl3, vlid, vlid2, lid;
        int length = 16;
        for (int i = 0; i < 500; i++) {
            set.add(BloomFilter.createRandom(length, 5));
        }
        
        System.out.println(Math.ceil(-(Math.log(0.01) / Math.log(2))) / Math.log(2));
        System.out.println(Math.ceil(-(Math.log(0.01) / Math.log(2))));
        
        
        for (int i = 0; i < 100000; i++) {
            bl0 = BloomFilter.createRandom(length, 5);
            bl1 = BloomFilter.createRandom(length, 5);
            bl2 = BloomFilter.createRandom(length, 5);
            bl3 = BloomFilter.createRandom(length, 5);
            vlid = BloomFilter.createRandom(length, 5);
            vlid2 = BloomFilter.createRandom(length, 5);
            lid = BloomFilter.createRandom(length, 5);

            bl0.or(bl1);
            bl0.or(bl2);
            bl0.or(bl3);
            bl0.or(vlid);
            bl0.or(BloomFilter.createRandom(length, 5));
            bl0.or(BloomFilter.createRandom(length, 5));
            bl0.or(BloomFilter.createRandom(length, 5));

            if (BloomFilter.ANDf(lid, bl0)) {
                System.out.println("False Positive 0");
            }
            if (BloomFilter.ANDf(vlid2, bl0)) {
                System.out.println("False Positive 1");
            }
        }
        System.out.println("Size: " + set.size());
    }
}

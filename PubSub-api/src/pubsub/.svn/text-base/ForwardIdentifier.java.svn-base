package pubsub;

import java.nio.ByteBuffer;

import pubsub.bloomfilter.BloomFilter;
import pubsub.util.Util;

/**
 *
 * @author tsilo
 * @author John Gasparis
 */
public class ForwardIdentifier extends BaseSerializableStruct {

    private BloomFilter bloomFilter;
    private short TTL;
    private static int LENGTH = -1;

    private ForwardIdentifier() {
    }

    public ForwardIdentifier(BloomFilter bloomFilter, short tTL) {
        this.bloomFilter = bloomFilter;
        TTL = tTL;
    }

    public BloomFilter getBloomFilter() {
        return bloomFilter;
    }

    public short getTTL() {
        return TTL;
    }
    
    public static short getTTL(ByteBuffer buffer) {
        return buffer.getShort(buffer.position());
    }
    
    public static void setTTL(ByteBuffer buffer, short ttl) {
        buffer.putShort(buffer.position(), ttl);
    }
 
    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = this.bloomFilter.length() + Util.SIZEOF_SHORT;
        }
        return LENGTH;
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        this.TTL = buff.getShort();
        this.bloomFilter = BloomFilter.parseByteBuffer(buff);
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        buff.putShort(TTL);
        bloomFilter.writeTo(buff);
    }

    public short decreamentTTL() {
        return --TTL;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ForwardIdentifier other = (ForwardIdentifier) obj;
        if (this.bloomFilter != other.bloomFilter && (this.bloomFilter == null || !this.bloomFilter.equals(other.bloomFilter))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.bloomFilter != null ? this.bloomFilter.hashCode() : 0);
        return hash;
    }

    public void setBloomFilter(BloomFilter blmFlt) {
        this.bloomFilter = blmFlt;
    }

    public static ForwardIdentifier parseByteBuffer(ByteBuffer buffer) {
        ForwardIdentifier fid = new ForwardIdentifier();
        fid.readBuffer(buffer);
        return fid;
    }

    public void setTTL(short i) {
        this.TTL = i;
    }
    
    public void addOnlyPath(ForwardIdentifier fid) {
        this.bloomFilter.or(fid.bloomFilter);
    }
    
    public void addPath(ForwardIdentifier fid) {
        this.bloomFilter.or(fid.bloomFilter);
        this.TTL += fid.TTL;
    }
    
    public void addPath(ForwardIdentifier fid, short incr) {
        this.bloomFilter.or(fid.bloomFilter);
        this.TTL += incr;
    }
}

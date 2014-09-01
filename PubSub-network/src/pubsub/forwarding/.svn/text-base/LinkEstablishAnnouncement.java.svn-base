package pubsub.forwarding;

import pubsub.util.FwdConfiguration;
import java.nio.ByteBuffer;
import pubsub.BaseSerializableStruct;

import pubsub.bloomfilter.BloomFilter;
import pubsub.util.Util;

/**
 *
 * @author tsilo
 * @author John Gasparis
 */
public class LinkEstablishAnnouncement extends BaseSerializableStruct {

    private BloomFilter vlid;
    private BloomFilter lid;
    private double weight;
    private final int LENGTH = (FwdConfiguration.ZFILTER_LENGTH << 1) + Util.SIZEOF_DOUBLE;

    private LinkEstablishAnnouncement() {
        this(null, null, -1);
    }

    public LinkEstablishAnnouncement(BloomFilter lid, BloomFilter neighborVLID, double weight) {
        this.lid = lid;
        this.vlid = neighborVLID;
        this.weight = weight;
    }

    public static LinkEstablishAnnouncement parseByteBuffer(ByteBuffer buff) {
        LinkEstablishAnnouncement lsa = new LinkEstablishAnnouncement();
        lsa.readBuffer(buff);
        return lsa;
    }
    
    public double getWeight() {
        return this.weight;
    }

    public BloomFilter getVLID() {
        return this.vlid;
    }

    public BloomFilter getLID() {
        return this.lid;
    }

    @Override
    public int getSerializedLength() {
        return LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        lid.writeTo(buff);
        vlid.writeTo(buff);
        buff.putDouble(weight);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        lid = BloomFilter.parseByteBuffer(buff);
        vlid = BloomFilter.parseByteBuffer(buff);
        weight = buff.getDouble();
    }
}

package pubsub.forwarding;

import pubsub.util.FwdConfiguration;
import java.nio.ByteBuffer;
import pubsub.bloomfilter.BloomFilter;
import java.util.Arrays;
import pubsub.BaseSerializableStruct;

/**
 *
 * @author tsilo
 * @author John Gasparis
 */
public class LinkDownAnnouncement extends BaseSerializableStruct {

    private BloomFilter lid;

    private LinkDownAnnouncement() {
        this(null);
    }

    public LinkDownAnnouncement(BloomFilter lid) {
        this.lid = lid;
    }

    public byte[] toByteArray() {
        return Arrays.copyOf(lid.getBytes(), lid.length());
    }

    public static LinkDownAnnouncement parseByteBuffer(ByteBuffer buff) {
        LinkDownAnnouncement lda = new LinkDownAnnouncement();
        lda.readBuffer(buff);
        return lda;
    }

    public BloomFilter getLID() {
        return this.lid;
    }

    @Override
    public int getSerializedLength() {
        if (lid != null) {
            return lid.getSerializedLength();
        } else {
            return FwdConfiguration.ZFILTER_LENGTH;
        }
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        lid.writeTo(buff);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        lid = BloomFilter.parseByteBuffer(buff);
    }
}

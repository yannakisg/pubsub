package pubsub.messages.net.tmc;

import java.nio.ByteBuffer;
import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.bloomfilter.BloomFilter;
import pubsub.messages.MessageType;

/**
 *
 * @author John Gasparis
 */
public class LinkDownMessage extends TMCNetMessage {

    private ByteIdentifier srcNodeID;
    private BloomFilter removedLID;
    private static int LENGTH = -1;

    public LinkDownMessage(ByteIdentifier myNodeID, ByteIdentifier srcNodeID, BloomFilter removedLID, ForwardIdentifier fid) {
        super(MessageType.Type.LINK_DOWN, fid, myNodeID);
        this.srcNodeID = srcNodeID;
        this.removedLID = removedLID;
    }

    private LinkDownMessage() {
        this.srcNodeID = null;
        this.removedLID = null;
    }

    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = super.getSerializedLength() + srcNodeID.getSerializedLength() + removedLID.getSerializedLength();
        }
        return LENGTH;
    }

    public ByteIdentifier getSrcNodeID() {
        return this.srcNodeID;
    }

    public BloomFilter getRemovedLID() {
        return this.removedLID;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        srcNodeID.writeTo(buff);
        removedLID.writeTo(buff);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);

        srcNodeID = ByteIdentifier.parseByteBuffer(buff);

        removedLID = BloomFilter.parseByteBuffer(buff);
    }

    public static LinkDownMessage createNew(ByteBuffer buff) {
        LinkDownMessage ldm = new LinkDownMessage();
        ldm.readBuffer(buff);
        return ldm;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LinkDownMessage other = (LinkDownMessage) obj;
        if (this.srcNodeID != other.srcNodeID && (this.srcNodeID == null || !this.srcNodeID.equals(other.srcNodeID))) {
            return false;
        }
        if (this.removedLID != other.removedLID && (this.removedLID == null || !this.removedLID.equals(other.removedLID))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.srcNodeID != null ? this.srcNodeID.hashCode() : 0);
        hash = 83 * hash + (this.removedLID != null ? this.removedLID.hashCode() : 0);
        hash = 83 * hash + +super.hashCode();
        return hash;
    }
}

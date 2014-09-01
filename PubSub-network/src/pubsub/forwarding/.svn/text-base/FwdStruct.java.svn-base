package pubsub.forwarding;

import java.nio.ByteBuffer;
import pubsub.BaseSerializableStruct;
import pubsub.Publication;
import pubsub.ForwardIdentifier;

/**
 *
 * @author tsilo
 * @author John Gasparis
 */
public class FwdStruct extends BaseSerializableStruct {

    private ForwardIdentifier fid;
    private Publication publication;

    private FwdStruct() {
        this(null, null);
    }

    public FwdStruct(ForwardIdentifier fid, Publication p) {
        this.fid = fid;
        this.publication = p;
    }

    public ForwardIdentifier getFid() {
        return fid;
    }

    public short getTTL() {
        return fid.getTTL();
    }
    
    public static short getTTL(ByteBuffer buffer) {
        return ForwardIdentifier.getTTL(buffer);
    }
    
    public static void setTTL(ByteBuffer buffer, short ttl) {
        ForwardIdentifier.setTTL(buffer, ttl);
    }

    public short decreamentAndGet() {
        return fid.decreamentTTL();
    }

    public Publication getPublication() {
        return publication;
    }

    @Override
    public int getSerializedLength() {
        return this.fid.getSerializedLength() + this.publication.getSerializedLength();
    }

    @Override
    public void readBuffer(ByteBuffer buffer) {
        this.fid = ForwardIdentifier.parseByteBuffer(buffer);
        this.publication = Publication.parseByteBuffer(buffer);
    }

    @Override
    public void writeTo(ByteBuffer buffer) {
        this.fid.writeTo(buffer);
        this.publication.writeTo(buffer);
    }

    public static FwdStruct parseByteBuffer(ByteBuffer buffer) {
        FwdStruct fp = new FwdStruct();
        fp.readBuffer(buffer);
        return fp;
    }
}

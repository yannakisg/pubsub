package pubsub.tmc.graph;

import java.nio.ByteBuffer;

import pubsub.BaseSerializableStruct;
import pubsub.ByteIdentifier;
import pubsub.bloomfilter.BloomFilter;
import pubsub.tmc.TMCUtil;
import pubsub.tmc.TMC_Mode;
import pubsub.util.FwdConfiguration;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class NeighborNode extends BaseSerializableStruct implements Node {

    private BloomFilter vlidORlid = null;
    private ByteIdentifier id;
    private BloomFilter vlid;
    private TMC_Mode type;
    private static final int LENGTH = Util.SIZEOF_BYTE + Util.SIZEOF_SHORT
            + FwdConfiguration.ZFILTER_LENGTH + TMCUtil.SHA1_LENGTH;

    public NeighborNode(ByteIdentifier name, BloomFilter vlid, TMC_Mode type) {

        if (name == null) {
            this.id = new ByteIdentifier(TMCUtil.getRandomNodeID());
        } else {
            this.id = name;
        }

        this.vlid = vlid;
        this.type = type;
    }

    /*public NeighborNode(BloomFilter vlid, TMC_Mode type) {
        this(null, vlid, type);
    }*/

    public NeighborNode(ByteIdentifier id) {
        this(id, null, TMC_Mode.HOST);
    }

    public NeighborNode() {
        this.id = null;
        this.vlid = null;
        this.type = TMC_Mode.HOST;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass().getInterfaces() != null) {
            if (getClass().getInterfaces()[0] != obj.getClass().getInterfaces()[0]) {
                return false;
            }
        } else {
            return false;
        }

        NeighborNode other = (NeighborNode) obj;

        if (id == null) {
            if (other.getID() != null) {
                return false;
            } else {
                return this.vlid.equals(other.getVLID());
            }
        } else if (!id.equals(other.getID())) {
            return false;
        }
        return true;
    }

    @Override
    public ByteIdentifier getID() {
        return this.id;
    }

    @Override
    public BloomFilter getVLID() {
        return this.vlid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public TMC_Mode getType() {
        return this.type;
    }

    @Override
    public void setID(ByteIdentifier name) {
        this.id = name;
    }

    @Override
    public void setType(TMC_Mode mode) {
        this.type = mode;
    }

    public void setVLID(BloomFilter vlid) {
        this.vlid = vlid;
        clearVlidORLid();
    }

    @Override
    public String toString() {
        return TMCUtil.byteArrayToString(id.getId());
    }

    public static int getSerializedSize() {
        return LENGTH;
    }
    
    @Override
    public int getSerializedLength() {
        return LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        buff.put(type.getMode());

        id.writeTo(buff);

        vlid.writeTo(buff);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        type = TMC_Mode.findBy(buff.get());

        id = ByteIdentifier.parseByteBuffer(buff);

        vlid = BloomFilter.parseByteBuffer(buff);
    }

    public static NeighborNode parseByteBuffer(ByteBuffer buffer) {
        NeighborNode node = new NeighborNode();
        node.readBuffer(buffer);
        return node;
    }

    @Override
    public BloomFilter getLidORVlid(BloomFilter lid) {
        if (vlidORlid == null) {
            vlidORlid = BloomFilter.OR(vlid, lid);
        }

        return vlidORlid;
    }

    @Override
    public void clearVlidORLid() {
        vlidORlid = null;
    }
}

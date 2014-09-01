package pubsub.tmc.graph;

import java.nio.ByteBuffer;

import pubsub.ByteIdentifier;
import pubsub.bloomfilter.BloomFilter;
import pubsub.tmc.TMC_Mode;

/**
 *
 * @author John Gasparis
 */
public interface Node {

    public BloomFilter getLidORVlid(BloomFilter lid);

    public BloomFilter getVLID();

    public TMC_Mode getType();

    public void setType(TMC_Mode mode);

    @Override
    public boolean equals(Object obj);

    public ByteIdentifier getID();

    public void setID(ByteIdentifier id);

    public int getSerializedLength();

    public void readBuffer(ByteBuffer buff);

    public byte[] toBytes();

    @Override
    public String toString();

    public void clearVlidORLid();
}

package pubsub.tmc.graph;

import java.nio.ByteBuffer;
import java.util.Arrays;
import pubsub.BaseSerializableStruct;
import pubsub.bloomfilter.BloomFilter;
import pubsub.util.FwdConfiguration;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class Link extends BaseSerializableStruct {

    private BloomFilter lid;
    private double cost;
    private NeighborNode endpoint;
    private static final int LINK_LENGTH = NeighborNode.getSerializedSize() + Util.SIZEOF_DOUBLE + 
            FwdConfiguration.ZFILTER_LENGTH;

    public Link(BloomFilter lid) {
        this(lid, null);
    }

    public Link(BloomFilter lid, NeighborNode endpointNeighbor) {
        this(lid, endpointNeighbor, 1.0);
    }

    public Link(BloomFilter lid, NeighborNode endpointNeighbor, double cost) {
        this.lid = lid;
        this.cost = cost;
        this.endpoint = endpointNeighbor;
    }

    public Link(NeighborNode endpointNeighbor) {
        this(null, endpointNeighbor);
    }

    private Link() {
        this(null, null);
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
        Link other = (Link) obj;
        if (!this.lid.equals(other.getLID())) {
            return false;
        }

        return true;
    }

    public double getCost() {
        return this.cost;
    }

    public BloomFilter getLID() {
        return this.lid;
    }

    public Node getEndpoint() {
        return this.endpoint;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + lid.hashCode();
        return result;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setEndPoint(NeighborNode endpoint) {
        this.endpoint = endpoint;
        endpoint.clearVlidORLid();
    }

    public void setLID(BloomFilter lid) {
        this.lid = lid;
        if (endpoint != null) {
            endpoint.clearVlidORLid();
        }
    }
    
    public static int getSerializedSize() {
        return LINK_LENGTH;
    }
    
    @Override
    public int getSerializedLength() {
        return LINK_LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        buff.putDouble(cost);

        if (lid != null) {
            lid.writeTo(buff);
        } else {
            byte[] data = new byte[FwdConfiguration.ZFILTER_LENGTH];
            Arrays.fill(data, (byte) 0);
            buff.put(data);
        }

        /*if (endpoint instanceof MyRouterNode) {
            buff.put(((MyRouterNode) endpoint).toMinBytes());
        } else {
            buff.put(endpoint.toBytes());
        }*/
        endpoint.writeTo(buff);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        cost = buff.getDouble();

        lid = BloomFilter.parseByteBuffer(buff);

        endpoint = NeighborNode.parseByteBuffer(buff);
    }

    @Override
    public String toString() {
        int num = 0;
        String str;

        for (byte b : lid.getBytes()) {
            num += b;
        }

        str = String.valueOf(num) + " [ " + cost + " ]";
        return str;
    }

    public static Link parseByteBuffer(ByteBuffer buffer) {
        Link link = new Link();
        link.readBuffer(buffer);
        return link;
    }

    public static Link parseByteArray(byte[] data) {
        Link link = new Link();
        link.fromBytes(data);
        return link;
    }

    public BloomFilter getLidORVlid() {
        return endpoint.getLidORVlid(lid);
    }
}

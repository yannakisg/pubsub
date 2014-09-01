package pubsub.tmc.graph;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import pubsub.BaseSerializableStruct;
import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.bloomfilter.BloomFilter;
import pubsub.forwarding.FwdUtils;
import pubsub.localrendezvous.LocRCClientFactory;
import pubsub.localrendezvous.TimeOutLocRCClient;
import pubsub.tmc.TMCUtil;
import pubsub.tmc.TMC_Mode;
import pubsub.tmc.topology.WeightedAdjacencyMap;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class MyRouterNode extends BaseSerializableStruct implements Node {

    private BloomFilter vlidORlid = null;
    private ByteIdentifier id;
    private BloomFilter vlid;
    private final WeightedAdjacencyMap topology;
    private Link proxyRouterLink;
    private TMC_Mode type = TMC_Mode.ROUTER;
    private Link rvpLink;

    public MyRouterNode(byte[] name) {
        this.id = new ByteIdentifier(name);
        this.vlid = getVLID();
        this.topology = new WeightedAdjacencyMap(id, vlid);
        this.proxyRouterLink = null;
        this.rvpLink = null;
    }

    public void debugTopology() {
        topology.debug();
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
            }
        } else if (!id.equals(other.getID())) {
            return false;
        }
        return true;
    }

    @Override
    public ByteIdentifier getID() {
        return id;
    }

    public void setVLID(BloomFilter vlid) {
        this.vlid = vlid;
    }

    @Override
    public final BloomFilter getVLID() {
        if (vlid == null) {
            TimeOutLocRCClient tClient = LocRCClientFactory.createTimeOutClient(this.getClass().getSimpleName());
            Logger logger = Logger.getLogger(MyRouterNode.class);
            try {
                logger.debug("obtain VLID from FWD");
                this.vlid = FwdUtils.getFwdVLID(tClient);
                logger.debug("got VLID: " + this.vlid.toBinaryString());
            } catch (InterruptedException e) {
                throw new RuntimeException("cannot obtain vlid from FwdComp", e);
            } finally {
                tClient.close();
            }
        }
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
    public void setID(ByteIdentifier id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return TMCUtil.byteArrayToString(id.getId());
    }

    public void updateTopology(ByteIdentifier nodeID, ByteIdentifier newID, BloomFilter vlid, TMC_Mode type) {
        topology.updateNode(nodeID, newID, vlid, type);
    }

    @Override
    public int getSerializedLength() {
        return topology.getPartialLength();
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        readBuffer(id, buff);
    }

    public void readBuffer(ByteIdentifier nodeID, ByteBuffer buff) {
        topology.readPartialBuffer(nodeID, buff);
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        topology.writePartialBuffer(buff);
    }

    public byte[] toMinBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(Util.SIZEOF_BYTE + id.getSerializedLength() + vlid.getSerializedLength());

        buffer.put(TMC_Mode.ROUTER.getMode());

        id.writeTo(buffer);
        vlid.writeTo(buffer);

        return buffer.array();
    }

    public void addLink(ByteIdentifier nodeID, BloomFilter vlid, BloomFilter lid, double weight) {
        topology.addLink(nodeID, vlid, lid, weight);
    }

    public Link getLink(ByteIdentifier routerID, ByteIdentifier neighborID) {
        return topology.getLink(routerID, neighborID);
    }

    public List<Link> getNeighborsExceptFor(ByteIdentifier routerID) {
        return topology.getOutgoingRoutersExceptFor(id, routerID);
    }

    public WeightedAdjacencyMap getWeightedAdjacencyMap() {
        return this.topology;
    }

    public BloomFilter getPath(ByteIdentifier src, ByteIdentifier dest, short[] ttl, boolean includeDest) {
        return topology.getPath(src, dest, ttl, includeDest);
    }

    public Map<ByteIdentifier, ForwardIdentifier> getNeighbors(ByteIdentifier id) {
        return topology.getNeighbors(id);
    }

    public ByteIdentifier removeLink(ByteIdentifier srcID, BloomFilter lid) {
        return topology.removeLink(srcID, lid);
    }

    public BloomFilter getConnectedLinks(ByteIdentifier nodeID) {
        Map<ByteIdentifier, Link> map = topology.getTopology().get(nodeID);        
        Collection<Link> cLinks = map.values();
        BloomFilter lid = BloomFilter.createZero();
        
        if (cLinks != null) {
            synchronized (map) {
                Iterator<Link> iter = cLinks.iterator();
                Link link;
                while (iter.hasNext()) {
                    link = iter.next();
                    lid.or(link.getLID());
                }
            }
        }
        
        return lid;
    }

    @Override
    public TMC_Mode getType() {
        return type;
    }

    public void setProxyRouterLink(Link link) {
        this.proxyRouterLink = link;
    }

    public Link getProxyRouterLink() {
        return this.proxyRouterLink;
    }

    public Link getRVPLink() {
        return this.rvpLink;
    }

    public void setRVPLink(Link link) {
        this.rvpLink = link;
    }

    public BloomFilter getHostLink(ByteIdentifier id) {
        return topology.getHostLink(id);
    }

    public BloomFilter getLID(ByteIdentifier id) {
        return topology.findLID(id);
    }

    public void addLink(ByteIdentifier source, ByteIdentifier dest, BloomFilter vlid, BloomFilter lid, double cost) {
        topology.addLink(source, dest, vlid, lid, cost);
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

    @Override
    public void setType(TMC_Mode mode) {
        this.type = TMC_Mode.ROUTER;
    }
}

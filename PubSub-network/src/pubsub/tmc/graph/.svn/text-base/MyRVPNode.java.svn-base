package pubsub.tmc.graph;

import java.nio.ByteBuffer;
import java.util.List;
import org.apache.log4j.Logger;
import pubsub.BaseSerializableStruct;
import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.bloomfilter.BloomFilter;
import pubsub.forwarding.FwdUtils;
import pubsub.localrendezvous.LocRCClientFactory;
import pubsub.localrendezvous.TimeOutLocRCClient;
import pubsub.messages.net.tmc.TopologyMessage;
import pubsub.tmc.TMCUtil;
import pubsub.tmc.TMC_Mode;
import pubsub.tmc.topology.WeightedAdjacencyMap;
import pubsub.tmc.topology.steinertree.Edge;
import pubsub.tmc.topology.steinertree.MinSpanTree;
import pubsub.tmc.topology.steinertree.SteinerTree;
import pubsub.util.FwdConfiguration;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class MyRVPNode extends BaseSerializableStruct implements Node {

    private static final Logger logger = Logger.getLogger(MyRVPNode.class);
    private BloomFilter vlidORlid = null;
    private ByteIdentifier id;
    private BloomFilter vlid;
    private TMC_Mode type = TMC_Mode.RVP;
    private Link proxyRouterLink;
    private final int LENGTH = Util.SIZEOF_BYTE + Util.SIZEOF_SHORT
            + (FwdConfiguration.ZFILTER_LENGTH << 1) + (TMCUtil.SHA1_LENGTH << 1);
    private WeightedAdjacencyMap adjacencyMap;

    public MyRVPNode(ByteIdentifier id) {
        this.id = id;
        this.vlid = getVLID();
        proxyRouterLink = null;
    }

    public MyRVPNode(byte[] nodeID) {
        this(new ByteIdentifier(nodeID));
    }

    @Override
    public final BloomFilter getVLID() {
        if (vlid == null) {
            TimeOutLocRCClient tClient = LocRCClientFactory.createTimeOutClient(this.getClass().getSimpleName());
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
    public int getSerializedLength() {
        return LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        id.writeTo(buff);
        vlid.writeTo(buff);
        if (proxyRouterLink != null) {
            proxyRouterLink.writeTo(buff);
        }
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        id = ByteIdentifier.parseByteBuffer(buff);

        vlid = BloomFilter.parseByteBuffer(buff);

        if (buff.hasRemaining()) {
            proxyRouterLink = new Link(BloomFilter.createZero(), new NeighborNode());
            proxyRouterLink.readBuffer(buff);
        }
    }

    @Override
    public TMC_Mode getType() {
        return type;
    }

    @Override
    public ByteIdentifier getID() {
        return this.id;
    }

    @Override
    public void setID(ByteIdentifier id) {
        this.id = id;
    }

    public void setProxyRouterLink(Link link) {
        proxyRouterLink = link;
    }

    public Link getProxyRouterLink() {
        return proxyRouterLink;
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
        this.type = TMC_Mode.RVP;
    }

    public WeightedAdjacencyMap getAdjacencyMap() {
        return adjacencyMap;
    }

    public void setAdjacencyMap(WeightedAdjacencyMap adjacencyMap) {
        this.adjacencyMap = adjacencyMap;
    }

    public BloomFilter getPath(ByteIdentifier src, ByteIdentifier dest, short[] ttl, boolean includeDest) {
        BloomFilter path;
        if (adjacencyMap == null) {
            return BloomFilter.createZero();
        }

        if (src.equals(id)) {
            path = adjacencyMap.getPath(proxyRouterLink.getEndpoint().getID(), dest, ttl, includeDest);
            ttl[0]++;
            path.or(proxyRouterLink.getLID());
        } else if (dest.equals(id)) {
            path = adjacencyMap.getPath(src, proxyRouterLink.getEndpoint().getID(), ttl, includeDest);
            ttl[0]++;
            path.or(adjacencyMap.getLink(proxyRouterLink.getEndpoint().getID(), id).getLID());
            path.or(vlid);
        } else {
            path = adjacencyMap.getPath(src, dest, ttl, includeDest);
        }
        
        return path;
    }

    public void saveTopology(TopologyMessage[] messages, int totalSize) {
        this.adjacencyMap = new WeightedAdjacencyMap(id, vlid);

        ByteBuffer buffer = ByteBuffer.allocate(totalSize);

        for (TopologyMessage message : messages) {
            buffer.put(message.getArray());
        }

        buffer.clear();
        adjacencyMap.readBuffer(buffer);

        adjacencyMap.debug();
    }
    
    public ForwardIdentifier constructSteinerTree(ByteIdentifier[] steinerPoints) {
        ForwardIdentifier fid = new ForwardIdentifier(BloomFilter.createZero(), (short) 0);
        SteinerTree steinerTree = new SteinerTree(adjacencyMap, steinerPoints);
        
        steinerTree.createSteinerTree();
        
        MinSpanTree minSpanTree = steinerTree.getSteinerTree();
        List<Edge> edges = minSpanTree.getMinSpanEdges();
        ByteIdentifier source;
        ByteIdentifier target;
        Link link;
        short ttl = 0;
        
        for (Edge edge : edges) {
            source = edge.getSource();
            target = edge.getTarget();
            
          //  logger.debug("Source: " + source + " | Target: " + target);
            
            link = adjacencyMap.getLink(source, target);
            ttl++;
            fid.getBloomFilter().or(link.getLID());
        }
        ttl += 2;
        fid.setTTL(ttl);
        
        return fid;
    }
}

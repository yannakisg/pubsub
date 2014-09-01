package pubsub.messages.ipc;

import java.nio.ByteBuffer;
import java.util.Map;
import pubsub.ByteIdentifier;
import pubsub.messages.MessageType;
import pubsub.tmc.TMCUtil;
import pubsub.tmc.graph.Link;
import pubsub.tmc.topology.WeightedAdjacencyMap;

/**
 *
 * @author John Gasparis
 */
public class DebugTopologyMessage extends IPCMessage {

    private WeightedAdjacencyMap wAdjacencyMap;

    public DebugTopologyMessage(MessageType.Type msgType, WeightedAdjacencyMap adjacencyMap) {
        super(msgType, TMCUtil.TMC_SID, TMCUtil.TMC_DEBUG_RID);

        if (msgType != MessageType.Type.DEBUG_TOPOLOGY
                && msgType != MessageType.Type.GET_HOSTS) {
            throw new IllegalArgumentException("Illegal MessageType");
        }
        this.wAdjacencyMap = adjacencyMap;
    }

    private DebugTopologyMessage() {
        this(MessageType.Type.NOTHING, null);
    }

    @Override
    public int getSerializedLength() {
        MessageType.Type msgType = super.getMessageType();

        if (msgType == MessageType.Type.DEBUG_TOPOLOGY) {
            return super.getSerializedLength() + wAdjacencyMap.getSerializedLength();
        } else if (msgType == MessageType.Type.GET_HOSTS) {
            return super.getSerializedLength() + wAdjacencyMap.getHostsLength();
        } else {
            return super.getSerializedLength();
        }
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        MessageType.Type msgType = super.getMessageType();

        super.writeTo(buff);

        if (msgType == MessageType.Type.DEBUG_TOPOLOGY) {
            wAdjacencyMap.writeTo(buff);
        } else if (msgType == MessageType.Type.GET_HOSTS) {
            wAdjacencyMap.writeHostsBuffer(buff);
        }
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);

        wAdjacencyMap = WeightedAdjacencyMap.createEmptyAdjacencyMap();

        MessageType.Type msgType = super.getMessageType();
        if (msgType == MessageType.Type.DEBUG_TOPOLOGY) {
            wAdjacencyMap.readBuffer(buff);
        } else if (msgType == MessageType.Type.GET_HOSTS) {
            wAdjacencyMap.readHostsBuffer(buff);
        }
    }

    public Map<ByteIdentifier, Map<ByteIdentifier, Link>> getTopology() {
        return this.wAdjacencyMap.getTopology();
    }

    public Map<ByteIdentifier, Link> getHosts() {
        return this.wAdjacencyMap.getNeighborHosts();
    }

    public static DebugTopologyMessage createNew(byte[] data) {
        DebugTopologyMessage debugMsg = new DebugTopologyMessage();
        debugMsg.fromBytes(data);

        return debugMsg;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        MessageType.Type msgType = super.getMessageType();

        if (msgType == MessageType.Type.DEBUG_TOPOLOGY) {
            Map<ByteIdentifier, Map<ByteIdentifier, Link>> topology = getTopology();
            Map<ByteIdentifier, Link> map;
            str.append("Total Routers => ").append(topology.size()).append("\n");

            for (ByteIdentifier id : topology.keySet()) {
                str.append(id).append("\n");

                map = topology.get(id);

                for (Link link : map.values()) {
                    str.append("\t").append("Cost: ").append(link.getCost()).append(" Endpoint: ").append(TMCUtil.byteArrayToString(link.getEndpoint().getID().getId()));
                    str.append("\n");
                }
                str.append("\n");
            }
        } else if (msgType == MessageType.Type.GET_HOSTS) {
            Map<ByteIdentifier, Link> hosts = getHosts();

            str.append("Total Hosts => ").append(hosts.size()).append("\n");

            for (Link link : hosts.values()) {
                str.append(link.getEndpoint().getID()).append("\n");

                str.append("\t").append(link.getLID().toBinaryString()).append("\n");
            }
            str.append("\n");
        }

        return str.toString();
    }
}

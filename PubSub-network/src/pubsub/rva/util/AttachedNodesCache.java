package pubsub.rva.util;

import java.util.HashMap;
import java.util.Map;
import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;

/**
 *
 * @author John Gasparis
 */
public class AttachedNodesCache {
    private Map<ByteIdentifier, ForwardIdentifier> attachedNodes;

    public AttachedNodesCache() {
        attachedNodes = new HashMap<ByteIdentifier, ForwardIdentifier>();
    }

    public ForwardIdentifier get(ByteIdentifier nodeID) {
        return this.attachedNodes.get(nodeID);
    }

    public boolean contains(ByteIdentifier nodeID) {
        return this.contains(nodeID);
    }

    public void put(ByteIdentifier nodeID, ForwardIdentifier fid) {
        attachedNodes.put(nodeID, fid);
    }
}

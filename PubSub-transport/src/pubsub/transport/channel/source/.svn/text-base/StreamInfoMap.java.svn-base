package pubsub.transport.channel.source;

import java.util.HashMap;
import java.util.Map;
import pubsub.ForwardIdentifier;

/**
 *
 * @author John Gasparis
 */
public class StreamInfoMap {

    private Map<Integer, ForwardIdentifier> map;

    public StreamInfoMap() {
        map = new HashMap<Integer, ForwardIdentifier>();
    }

    public void put(int id, ForwardIdentifier fid) {
        ForwardIdentifier temp = fid;
        if (map.containsKey(id)) {
            temp = map.get(id);
            temp.getBloomFilter().or(fid.getBloomFilter());
        }

        map.put(id, temp);
    }

    public ForwardIdentifier get(int id) {
        return map.get(id);
    }
}

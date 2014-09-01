package pubsub.tmc.router;

import java.util.HashMap;
import java.util.Map;
import pubsub.ByteIdentifier;

/**
 *
 * @author John Gasparis
 */
public class LSAHistory {

    private Map<ByteIdentifier, Integer> historyMap = new HashMap<ByteIdentifier, Integer>();

    public void addEntry(ByteIdentifier id, int seqNum) {
        historyMap.put(id, seqNum);
    }

    public boolean isNewer(ByteIdentifier id, int msgID) {
        Integer msg = historyMap.get(id);
        
        if (msg == null || msg < msgID) {
            historyMap.put(id, msgID);
            return true;            
        } else {
            return false;
        }
    }
}

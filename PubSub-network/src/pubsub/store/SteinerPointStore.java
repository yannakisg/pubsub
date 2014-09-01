package pubsub.store;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.PubSubID;

/**
 *
 * @author John Gasparis
 */
public class SteinerPointStore {

    private HashMap<ItemEntry, Map<ByteIdentifier, Set<ByteIdentifier>>> storePubGW_SubGW_Map;
    private HashMap<ItemEntry, Map<ByteIdentifier, Set<ByteIdentifier>>> storeSubGW_SubHost_Map;
    private HashMap<ByteIdentifier, ForwardIdentifier> storeFidGWtoHosts;

    public SteinerPointStore() {
        this.storePubGW_SubGW_Map = new HashMap<ItemEntry, Map<ByteIdentifier, Set<ByteIdentifier>>>();
        this.storeSubGW_SubHost_Map = new HashMap<ItemEntry, Map<ByteIdentifier, Set<ByteIdentifier>>>();
        this.storeFidGWtoHosts = new HashMap<ByteIdentifier, ForwardIdentifier>();
    }

    public void put(PubSubID sid, PubSubID rid, ByteIdentifier publisherGW, ByteIdentifier subscriberGW, ByteIdentifier subscriberHost, ForwardIdentifier gwToHost) {
        ItemEntry entry = ItemEntry.getInstance(sid, rid);        
        Set<ByteIdentifier> set =  getSetPubGw_SubGW(entry, publisherGW);
        set.add(subscriberGW);
        
        Set<ByteIdentifier> setHost = getSetSubGw_SubHost(entry, subscriberGW);
        setHost.add(subscriberHost);
        
        storeFidGWtoHosts.put(subscriberHost, gwToHost);
    }

    public void remove(PubSubID sid, PubSubID rid, ByteIdentifier publisherGW, ByteIdentifier subscriberGW, ByteIdentifier subscriberHost) {
        ItemEntry entry = ItemEntry.getInstance(sid, rid);        
        Set<ByteIdentifier> set = getSetSubGw_SubHost(entry, subscriberGW);
        
        if (set.isEmpty()) {
            return;
        } 
        
        set.remove(subscriberHost);
        
        if (set.isEmpty()) {
            Set<ByteIdentifier> gwSet = getSetPubGw_SubGW(entry, publisherGW);
            if (!gwSet.isEmpty()) {
                gwSet.remove(subscriberGW);
            }            
        }
    }
    
    public ForwardIdentifier getGwToHost(ByteIdentifier hostID) {
        return storeFidGWtoHosts.get(hostID);
    }

    public ByteIdentifier[] getSteinerPointsGW(PubSubID sid, PubSubID rid, ByteIdentifier publisherGW) {
        ItemEntry entry = ItemEntry.getInstance(sid, rid);
        Set<ByteIdentifier> set = getSetPubGw_SubGW(entry, publisherGW);
        ByteIdentifier[] array;
        Object[] setArray = null;
        int length = 1;
        
        if (!set.isEmpty()) {
            length += set.size();
            setArray = set.toArray();
        } 
        
        array = new ByteIdentifier[length];
        array[0] = publisherGW;
        for (int i = 1; i < length; i++) {
            array[i] = (ByteIdentifier) setArray[i - 1];
        }
        
        return array;
    }
    
    public Collection<Set<ByteIdentifier>> getSteinerPointsHost(PubSubID sid, PubSubID rid) {
        ItemEntry entry = ItemEntry.getInstance(sid, rid);
        return storeSubGW_SubHost_Map.get(entry).values();
    }

    private Set<ByteIdentifier> getSetPubGw_SubGW(ItemEntry entry, ByteIdentifier publisherGW) {
        Map<ByteIdentifier, Set<ByteIdentifier>> map = storePubGW_SubGW_Map.get(entry);
        
        if (map == null) {
            map = new HashMap<ByteIdentifier, Set<ByteIdentifier>>();
            storePubGW_SubGW_Map.put(entry, map);
        }
        
        Set<ByteIdentifier> set = map.get(publisherGW);

        if (set == null) {
            set = new HashSet<ByteIdentifier>();
            map.put(publisherGW, set);
        }

        return set;
    }
    
    private Set<ByteIdentifier> getSetSubGw_SubHost(ItemEntry entry, ByteIdentifier publisherGW) {
        Map<ByteIdentifier, Set<ByteIdentifier>> map = storeSubGW_SubHost_Map.get(entry);
        
        if (map == null) {
            map = new HashMap<ByteIdentifier, Set<ByteIdentifier>>();
            storeSubGW_SubHost_Map.put(entry, map);
        }
        
        Set<ByteIdentifier> set = map.get(publisherGW);
        
        if (set == null) {
            set = new HashSet<ByteIdentifier>();
            map.put(publisherGW, set);
        }
        
        return set;
    }
}

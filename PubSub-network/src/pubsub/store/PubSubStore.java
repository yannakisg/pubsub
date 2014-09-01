package pubsub.store;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import pubsub.ContentType;
import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.PubSubID;
import pubsub.messages.RVAAnnouncement;

/**
 * Class used to store Publication information in RVAs
 *
 * @author netharis
 * @author xvas
 * @author John Gasparis
 * @author Christos Tsilopoulos
 */
public class PubSubStore {

    private final Map<ContentType, Map<ItemEntry, Set<NodeEntry>>> database = new EnumMap<ContentType, Map<ItemEntry, Set<NodeEntry>>>(ContentType.class);

    /**
     *
     * @param pub
     *            The publication.
     * @param senderID
     *            entity responsible
     *
     * @return true if the publication has been previously published, else false
     */
    public boolean addEntry(PubSubID sid, PubSubID rid, ContentType cType, RVAAnnouncement.RVPAction rvpAction,
            ByteIdentifier senderID, ByteIdentifier hostID, ForwardIdentifier gwToHost, ForwardIdentifier hostToGW, long lifeTime, ByteIdentifier procID) {
        Map<ItemEntry, Set<NodeEntry>> map = getDataBase(cType);
        ItemEntry entry = new ItemEntry(sid, rid);
        Set<NodeEntry> set = map.get(entry);
        if (set == null) {
            set = Collections.synchronizedSet(new HashSet<NodeEntry>());
            map.put(entry, set);
        }
        return set.add(new NodeEntry(procID, rvpAction, senderID, hostID, gwToHost, hostToGW, lifeTime));
    }

    public void removeEntry(PubSubID sid, PubSubID rid, ContentType cType,
            ByteIdentifier senderID, ByteIdentifier hostID) {
        Map<ItemEntry, Set<NodeEntry>> map = getDataBase(cType);
        ItemEntry entry = ItemEntry.getInstance(sid, rid);
        Set<NodeEntry> set = map.get(entry);
        if (set != null) {
            set.remove(NodeEntry.getInstance(senderID, hostID));
        }
    }

    public void removeEntries(PubSubID sid, PubSubID rid, ContentType cType,
            Set<NodeEntry> who) {
        Map<ItemEntry, Set<NodeEntry>> map = getDataBase(cType);
        ItemEntry entry = ItemEntry.getInstance(sid, rid);
        Set<NodeEntry> set = map.get(entry);
        if (set != null) {
            set.removeAll(who);
        }
    }

    /**
     * Manually synchronize on the returned sorted set 
     * when iterating over it.
     */
    public Set<NodeEntry> findEntries(PubSubID sid, PubSubID rid,
            ContentType cType) {
        Map<ItemEntry, Set<NodeEntry>> db = this.getDataBase(cType);
        Set<NodeEntry> subs = null;
        if (db != null) {
            ItemEntry entry = ItemEntry.getInstance(sid, rid);
            subs = db.get(entry);
        }

        return subs;
    }

    public NodeEntry findFirstEntry(PubSubID sid, PubSubID rid, ContentType cType) {
        Map<ItemEntry, Set<NodeEntry>> db = this.getDataBase(cType);
        Set<NodeEntry> subs = null;

        if (db != null) {
            ItemEntry entry = ItemEntry.getInstance(sid, rid);
            subs = db.get(entry);
        }
        if (subs != null) {
            synchronized (subs) {
                Iterator<NodeEntry> iter = subs.iterator();
                NodeEntry entry = null;
                if (iter.hasNext()) {
                    entry = iter.next();
                }
                return entry;
            }
        }

        return null;
    }

    public void decreamentLifeTime(ContentType type, long decreament) {
        Map<ItemEntry, Set<NodeEntry>> map = getDataBase(type);

        if (map != null) {
            synchronized (map) {
                Iterator<Set<NodeEntry>> iterValues = map.values().iterator();
                Iterator<NodeEntry> iterEntry;
                Set<NodeEntry> set;
                NodeEntry entry;
                long lifeTime;

                while (iterValues.hasNext()) {
                    set = iterValues.next();

                    synchronized (set) {
                        iterEntry = set.iterator();

                        while (iterEntry.hasNext()) {
                            entry = iterEntry.next();

                            lifeTime = entry.getLifeTime() - decreament;
                            if (lifeTime <= 0) {
                                iterEntry.remove();
                            } else {
                                entry.setLifeTime(lifeTime);
                            }
                        }
                    }
                }
            }
        }
    }

    private Map<ItemEntry, Set<NodeEntry>> getDataBase(ContentType cType) {
        Map<ItemEntry, Set<NodeEntry>> map = this.database.get(cType);
        if (map == null) {
            map = Collections.synchronizedMap(new HashMap<ItemEntry, Set<NodeEntry>>());
            this.database.put(cType, map);
        }
        return map;
    }
}

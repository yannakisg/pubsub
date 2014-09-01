package pubsub.store;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import pubsub.PubSubID;

class ItemEntry {

    private PubSubID scopeId;
    private PubSubID rId;
    private int hashValue;
    private static ItemEntry instance = null;
    private static ByteBuffer buffer = null;
    private static Lock lock = new ReentrantLock();

    public ItemEntry(PubSubID scopeId, PubSubID rId) {
        this.scopeId = scopeId;
        this.rId = rId;
        hashValue = computeHash();
    }

    private int computeHash() {
        int hashCode;

        lock.lock();

        try {
            if (buffer == null) {
                buffer = ByteBuffer.allocate(PubSubID.ID_LENGTH << 1);
            }
            buffer.put(this.scopeId.getId()).put(this.rId.getId());

            hashCode = Arrays.hashCode(buffer.array());
            buffer.clear();
        } finally {
            lock.unlock();
        }

        return hashCode;
    }

    @Override
    public int hashCode() {
        return hashValue;
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
        ItemEntry other = (ItemEntry) obj;
        if (rId == null) {
            if (other.rId != null) {
                return false;
            }
        } else if (!rId.equals(other.rId)) {
            return false;
        }
        if (scopeId == null) {
            if (other.scopeId != null) {
                return false;
            }
        } else if (!scopeId.equals(other.scopeId)) {
            return false;
        }
        return true;
    }

    public static ItemEntry getInstance(PubSubID sid, PubSubID rid) {
        if (instance == null) {
            instance = new ItemEntry(sid, rid);
        } else {
            instance.scopeId = sid;
            instance.rId = rid;
            instance.hashValue = instance.computeHash();
        }

        return instance;
    }
}

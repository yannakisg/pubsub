package pubsub.forwarding;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;
import pubsub.PubSubID;
import pubsub.Publication;
import pubsub.cache.LFUPacketCache;
import pubsub.cache.LRUPacketCache;
import pubsub.cache.MFUPacketCache;
import pubsub.cache.MRUPacketCache;
import pubsub.cache.PacketCache;
import pubsub.messages.net.transport.DataMessage;
import pubsub.util.Util;

public class CachingElement {

    private static ByteBuffer buffer = null;
    private static CacheEntryKey instance = null;
    private static Lock lock = new ReentrantLock();

    private class CacheEntryKey {

        private PubSubID scopeId;
        private PubSubID rId;
        private int chunkNum;
        private int hashCode;

        public CacheEntryKey(PubSubID scopeId, PubSubID rId, int chunkNum) {
            this.scopeId = scopeId;
            this.rId = rId;
            this.chunkNum = chunkNum;
            hashCode = computeHash();
        }

        private int computeHash() {
            lock.lock();
            int hashcode;
            try {
                if (buffer == null) {
                    buffer = ByteBuffer.allocate(scopeId.getId().length + rId.getId().length + Util.SIZEOF_INT);
                }

                buffer.put(this.scopeId.getId()).put(this.rId.getId()).putInt(this.chunkNum);

                hashcode = Arrays.hashCode(buffer.array());
                buffer.flip();
            } finally {
                lock.unlock();
            }
            return hashcode;
        }

        @Override
        public int hashCode() {
            return this.hashCode;
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
            CacheEntryKey other = (CacheEntryKey) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (chunkNum != other.chunkNum) {
                return false;
            }
            if (hashCode != other.hashCode) {
                return false;
            }
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

        private CachingElement getOuterType() {
            return CachingElement.this;
        }
    }

    private CacheEntryKey getInstance(PubSubID scope, PubSubID rid, int chunkNum) {
        if (instance == null) {
            instance = new CacheEntryKey(scope, rid, chunkNum);
        } else {
            instance.scopeId = scope;
            instance.rId = rid;
            instance.chunkNum = chunkNum;
            instance.hashCode = instance.computeHash();
        }

        return instance;
    }

    public static enum CachingPolicy {

        LRU("LRU"),
        MRU("MRU"),
        LFU("LFU"),
        MFU("MFU");
        private final String str;

        private CachingPolicy(String str) {
            this.str = str;
        }

        @Override
        public String toString() {
            return str;
        }

        public static CachingPolicy getPolicy(String str) {
            for (CachingPolicy policy : values()) {
                if (policy.toString().equals(str)) {
                    return policy;
                }
            }
            throw new IllegalArgumentException("policy " + str + " not found");
        }
    }
    private static final Logger logger = Logger.getLogger(CachingElement.class);
    private static int DEFAULT_SIZE = 5000;
    private static CachingPolicy DEFAULT_POLICY = CachingPolicy.LRU;
    public static double CACHE_PROBABILITY = 0.5d;

    public static void configureDefaultCacheSize(int size) {
        DEFAULT_SIZE = size;
    }

    public static void configureDefaultPolicy(CachingPolicy policy) {
        DEFAULT_POLICY = policy;
    }

    public static void configureCachingProbability(double val) {
        CACHE_PROBABILITY = Math.min(1, Math.max(0, val));
    }
    private PacketCache<CacheEntryKey, Publication> cache;

    public CachingElement(CachingPolicy policy, int cacheSize) {
        switch (policy) {
            case MRU:
                cache = new MRUPacketCache<CacheEntryKey, Publication>(cacheSize);
                break;
            case LFU:
                cache = new LFUPacketCache<CacheEntryKey, Publication>(cacheSize);
                break;
            case MFU:
                cache = new MFUPacketCache<CacheEntryKey, Publication>(cacheSize);
                break;
            case LRU:
            default:
                cache = new LRUPacketCache<CacheEntryKey, Publication>(cacheSize);
                break;
        }
        try {
            logger.addAppender(new FileAppender(new SimpleLayout(), "cache.log", false));
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public CachingElement(int cacheSize) {
        this(CachingPolicy.LRU, cacheSize);
    }

    public CachingElement() {
        this(DEFAULT_POLICY, DEFAULT_SIZE);
    }

    public void store(Publication pub) {

        PubSubID sid = pub.getScopeId();
        PubSubID rid = pub.getRendezvousId();
        byte[] duplicateData = pub.getDuplicateDataArray();
        ByteBuffer buf = ByteBuffer.wrap(duplicateData);
        int chunkNum = DataMessage.getChunkNum(buf);

        DataMessage.setHopCount(buf, -1);

        CacheEntryKey key = new CacheEntryKey(sid, rid, chunkNum);
        this.cache.write(key, Publication.createImmutableData(sid, rid, duplicateData));

        logger.debug(sid + "/" + rid + ":" + chunkNum + " cached");

    }

    public Publication seek(PubSubID scope, PubSubID rid, int chunkNum) {
        CacheEntryKey key = getInstance(scope, rid, chunkNum);
        Publication pub = this.cache.seek(key);

        if (pub == null) {
            logger.debug(scope + "/" + rid + ":" + chunkNum + " not found");
        } else {
            logger.debug(scope + "/" + rid + ":" + chunkNum + " found");
        }

        return pub;
    }

    public static CachingElement createDefault() {
        return new CachingElement();
    }
}

package pubsub.cache;

public class LRUPacketCache<K, V> extends BasePacketCache<K, V> {

    public LRUPacketCache(int capacity) {
        super(capacity, new CacheEntryFactory<K, V>() {

            @Override
            public CacheEntry<K, V> createCacheEntry(K key, V value) {
                return new LRUCacheEntry<K, V>(key, value);
            }
        });
    }
}

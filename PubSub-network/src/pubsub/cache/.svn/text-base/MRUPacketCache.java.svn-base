package pubsub.cache;

public class MRUPacketCache<K, V> extends BasePacketCache<K, V> {

    public MRUPacketCache(int capacity) {
        super(capacity, new CacheEntryFactory<K, V>() {

            @Override
            public CacheEntry<K, V> createCacheEntry(K key, V value) {
                return new MRUCacheEntry<K, V>(key, value);
            }
        });
    }
}

package pubsub.cache;


public class LFUPacketCache<K, V> extends BasePacketCache<K, V> {
	
	public LFUPacketCache(int capacity) {
		super(capacity, new CacheEntryFactory<K, V>() {

			@Override
			public CacheEntry<K, V> createCacheEntry(K key, V value) {
				return new LFUCacheEntry<K, V>(key, value);
			}
		});	
	}
}

package pubsub.cache;


public class MFUPacketCache<K, V> extends BasePacketCache<K, V> {
	
	public MFUPacketCache(int capacity) {
		super(capacity, new CacheEntryFactory<K, V>() {

			@Override
			public CacheEntry<K, V> createCacheEntry(K key, V value) {
				return new MFUCacheEntry<K, V>(key, value);
			}
		});	
	}
}

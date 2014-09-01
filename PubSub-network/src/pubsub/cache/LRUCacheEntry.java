package pubsub.cache;

public class LRUCacheEntry<K, V> extends CacheEntry<K, V> {

	public LRUCacheEntry(K key, V value) {
		super(key, value);
		metadata = System.currentTimeMillis();
	}

    @Override
	public int compareTo(CacheEntry<K, V> other) {
		if (metadata > other.metadata) {
			return 1;
		} else if (metadata == other.metadata) {
			return 0;
		} else
			return -1;
	}

    @Override
	public void update() {
		metadata = System.currentTimeMillis();
	}
}

package pubsub.cache;

public class MFUCacheEntry<K, V> extends CacheEntry<K, V> {

	public MFUCacheEntry(K key, V value) {
		super(key, value);
		metadata = 0;
	}

    @Override
	public int compareTo(CacheEntry<K, V> other) {
		if (metadata > other.metadata) {
			return -1;
		} else if (metadata == other.metadata) {
			return 0;
		} else
			return 1;
	}

    @Override
	public void update() {
		metadata++;
	}
}

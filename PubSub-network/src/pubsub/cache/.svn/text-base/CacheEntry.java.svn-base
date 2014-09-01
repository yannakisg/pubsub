package pubsub.cache;

public abstract class CacheEntry<K, V> implements Comparable<CacheEntry<K, V>>{
	private final K key;
	private final V value;
	protected long metadata;
	
	public CacheEntry(K key, V value){
		this.key = key;
		this.value = value;		
	}		
	
	public K getKey(){
		return this.key;
	}

	public V getValue() {
		return value;
	}
	
	public long getMetadata() {
		return metadata;
	}
	
	public abstract void update();
}

package pubsub.cache;


public interface PacketCache<K, V>{
	public void write(K key, V value);
	public V seek(K key);
}

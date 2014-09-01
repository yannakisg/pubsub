package pubsub.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public abstract class BasePacketCache<K, V> implements PacketCache<K, V> {
	private final Map<K, CacheEntry<K, V>> cacheStore;
	private final long capacity;
	private final CacheEntryFactory<K, V> cacheEntryFactory;
	
	private final ReentrantReadWriteLock lock;
	private final ReadLock readLock;
	private final WriteLock writeLock;	
	

	public BasePacketCache(int capacity, CacheEntryFactory<K, V> factory) {
		this.capacity = capacity;
		this.cacheStore = new HashMap<K, CacheEntry<K, V>>(
				capacity);
		this.cacheEntryFactory = factory;
		this.lock = new ReentrantReadWriteLock();
		this.readLock = this.lock.readLock();
		this.writeLock = this.lock.writeLock();
	}

	@Override
	public void write(K key, V value) {
		this.writeLock.lock();
		try {
			if (this.cacheStore.size() == capacity) {
				emptyOne();
			}
			CacheEntry<K, V> record = this.cacheEntryFactory.createCacheEntry(key, value);
			if (this.cacheStore.put(key, record) != null) {
				System.out.printf("CACHE COLLISION at key %s\n", key);
			}
		} finally {
			this.writeLock.unlock();
		}
	}

    @Override
	public V seek(K key) {
		this.readLock.lock();
		try {
			CacheEntry<K, V> record = this.cacheStore.get(key);
			if (record != null) {
				record.update();
				return record.getValue();
			}
			return null;
		} finally {
			this.readLock.unlock();
		}
	}

	private void emptyOne() {
		ArrayList<CacheEntry<K, V>> list = new ArrayList<CacheEntry<K, V>>(
				this.cacheStore.values());
		Collections.sort(list);
		CacheEntry<K, V> lruRecord = list.get(0);
		this.cacheStore.remove(lruRecord.getKey());
	}	
}

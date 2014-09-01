package pubsub.module;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;


//default access
class SubscriberSet {
	private final Set<Subscriber> set = new HashSet<Subscriber>();
	private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	private final ReadLock readLock = rwLock.readLock();
	private final WriteLock writeLock = rwLock.writeLock();
	
	
	public void store(Subscriber subscriber) {
		this.writeLock.lock();
		try{
			this.set.add(subscriber);
		}
		finally{
			this.writeLock.unlock();
		}		
	}


	public void remove(Subscriber subscriber) {
		this.writeLock.lock();
		try{
			this.set.remove(subscriber);
		}
		finally{
			this.writeLock.unlock();
		}		
	}


	public void getAll(Set<Subscriber> theSet) {
		this.readLock.lock();
		try{
			theSet.addAll(this.set);
		}
		finally{
			this.readLock.unlock();
		}		
	}
	
	public int size(){
		return this.set.size();
	}

	public boolean isEmpty() {		
		return this.set.isEmpty();
	}		
}

package pubsub.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import pubsub.cache.CacheEntry;
import pubsub.cache.MFUCacheEntry;

public class TestCache {
	public static void main(String[] args) throws InterruptedException {
		List<CacheEntry<Integer, Integer>> list = new ArrayList<CacheEntry<Integer,Integer>>(100);
		Integer k = Integer.valueOf(0);
		Integer v = Integer.valueOf(1);
		
		for(int i=0; i<100; i++){
			System.out.printf("add %d\n", i);
			list.add(new MFUCacheEntry<Integer, Integer>(k, v));			
		}
		
		Random random = new Random();
		for(int i=0; i<1000; i++){
			int nextInt = random.nextInt(list.size());
			list.get(nextInt).update();
		}
		
		Collections.sort(list);
		for (CacheEntry<Integer, Integer> lruCacheEntry : list) {
			System.out.printf("metadata %d\n", lruCacheEntry.getMetadata());
		}
		
	}

}

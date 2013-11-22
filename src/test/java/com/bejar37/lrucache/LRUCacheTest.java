package com.bejar37.lrucache;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class LRUCacheTest {
	private static final int MINSIZE = 1;
	
	@Test(dataProvider="lowLruVals")
	public void lruHasMinSize1(int size) {
			try {
				new LRUCache<String, Integer>(size);
				if (size < MINSIZE) {
					assertEquals(true, false, "No exception thrown");
				}
			} catch (IllegalArgumentException iae) {}
	}
	
	@Test
	public void canRetrieveCachedItem() {
		LRUCache<String, Integer> lru = new LRUCache<String, Integer>(1);
		String k = Integer.toString(10);
		Integer v1 = new Integer(1);
		lru.put(k, v1);
		assertEquals(lru.get(k), v1);
		Integer v2 = new Integer(2);
		lru.put(k, v2);
		assertEquals(lru.get(k), v2);
	}
	
	@Test(dataProvider="cacheSizeAndEntries")
	public void retrieveValuesFromCache(int cacheSize, Map<String, Integer> entries){

		LRUCache<String, Integer> lru = new LRUCache<String, Integer>(cacheSize);
		for (Entry<String, Integer> e : entries.entrySet()) {
			lru.put(e.getKey(), e.getValue());
		}
		int i = 0;
		boolean tooBig = entries.size() > cacheSize;
		for (Entry<String, Integer> e : entries.entrySet()) {
			if (tooBig && i < entries.size() - cacheSize ) {
				System.out.println(lru.get(e.getKey()));
				assert(lru.get(e.getKey()) ==  null);
			} else {
				assertEquals(lru.get(e.getKey()), e.getValue(), "Should contain key: "  + e.getKey());
			}
			i++;			
		}
	}
	

	@DataProvider(name="cacheSizeAndEntries")
	public Object[][] dataSets() {
		int numEntries = 10;
		Random gen  = new Random();
		Object[][] a = new Object[numEntries][2];
		for (int i = 0; i < numEntries; i++) {
			Map<String, Integer> list = new HashMap<String,Integer>();
			for (int j = 0; j < 10; j++) {
				for (int k = 0; k < j; k++) {
					int nextInt = gen.nextInt();
					list.put(Integer.toString(nextInt), new Integer(nextInt));
				}
			}
			a[i] = new Object[]{ i + 1, list };
		}
		return a;
		
	}
	
	@DataProvider(name="lowLruVals")
	public Object[][] lowVals() {
		return new Object[][]{{-1}, {0}, {1}, {2}};
	}

}

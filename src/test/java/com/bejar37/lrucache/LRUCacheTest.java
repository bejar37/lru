package com.bejar37.lrucache;

import java.util.*;
import java.util.Map.Entry;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class LRUCacheTest {
	
	@Test(dataProvider="lowLruVals")
	public void lruHasMinSize1(int size) {
			try {
				new LRUCache<String, Integer>(size);
				if (size < LRUCache.MINSIZE) {
					assertEquals(true, false, "Should throw an exception if the size of cache is less than min");
				}
			} catch (IllegalArgumentException iae) {}
	}
	
	@Test
	public void canRetrieveCachedItem() {
		LRUCache<String, Integer> lru = new LRUCache<String, Integer>(2);
		String k = Integer.toString(10);
		Integer v1 = new Integer(1);
		lru.put(k, v1);
		assertEquals(lru.get(k), v1, "elements in cache should be retrievable by key");
		Integer v2 = new Integer(2);
		lru.put(k, v2);
		assertEquals(lru.get(k), v2, "putting a key that already exists should update the value");
	}

    @Test
    public void getKeyShouldMarkAsUsed() {
        LRUCache<String, String> lru = new LRUCache<String, String>(2);
        lru.put("hello", "world");
        lru.put("foo", "bar");
        assertEquals(lru.get("hello"), "world", "Should retrieve correct value");
        lru.put("baz", "qux");
        assertNull(lru.get("foo"), "Key foo should have been expulsed from cache");
        assertEquals(lru.get("hello"), "world", "The original get should have updated LRU value");
        assertEquals(lru.get("baz"), "qux", "Should have correct value for key baz");

    }
	
	@Test(dataProvider="cacheSizeAndEntries")
	public void retrieveValuesFromCache(int cacheSize, Map<String, Integer> entryMap){
        List<Entry<String, Integer>> entries = new ArrayList<Entry<String, Integer>>(entryMap.entrySet());

		LRUCache<String, Integer> lru = new LRUCache<String, Integer>(cacheSize);
		for (Entry<String, Integer> e : entries) {
			lru.put(e.getKey(), e.getValue());
		}
		int i = 0;
		boolean tooBig = entries.size() > cacheSize;
		for (Entry<String, Integer> e : entries) {
			if (tooBig && i < entries.size() - cacheSize ) {
				assertNull(lru.get(e.getKey()),
                        "key: " + e.getKey() + " should have been expulsed from the cache");
			} else {
				assertEquals(lru.get(e.getKey()), e.getValue(), "Should contain key: "  + e.getKey());
			}
			i++;			
		}
	}

	@DataProvider(name="cacheSizeAndEntries")
	public Object[][] dataSets() {
		int numEntries = 100;
		Random gen  = new Random();
		Object[][] a = new Object[numEntries][2];
		for (int i = 0; i < numEntries; i++) {
			Map<String, Integer> map = new HashMap<String,Integer>();
			for (int j = 0; j < gen.nextInt(400); j++) {
                map.put(Integer.toString(gen.nextInt()), gen.nextInt());
			}
			a[i] = new Object[]{ i + 1, map };
		}
		return a;
	}
	
	@DataProvider(name="lowLruVals")
	public Object[][] lowVals() {
		return new Object[][]{{-1}, {0}, {1}, {2}};
	}

}

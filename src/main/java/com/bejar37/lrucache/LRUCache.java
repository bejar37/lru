package com.bejar37.lrucache;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class LRUCache<K, V> {
	private Map<K, ValueWrap> cache;
	private PriorityQueue<TimeAccessKey> lru;
	private final int maxSize;
	private long currentSize;

	public LRUCache(int maxSize) {
		if (maxSize < 1) {
			throw new IllegalArgumentException(
					"Minimum cache size is one element");
		}
		this.maxSize = maxSize;
		cache = new HashMap<K, ValueWrap>();
		lru = new PriorityQueue<TimeAccessKey>(maxSize / 5 > 11 ? maxSize / 5 : 11, new Comparator<TimeAccessKey>() {
			public int compare(TimeAccessKey o1, TimeAccessKey o2) {
				if (o1.getLastAccessed().before(o2.getLastAccessed())) {
					return -1;
				}
				return 1;
			}
		});
		currentSize = 0;
	}

	public synchronized void put(K key, V value) {
		if (currentSize >= maxSize) {
			removeLRUElement();
		}
		addElem(key, value);
	}

	public synchronized V get(K key) {
		V ret = null;
		if (key == null) {
			return ret;
		}
		if (cache.containsKey(key)) {
			ValueWrap val = cache.get(key);
			ret = val.getVal();
			val.getPriorityEntry().setLastAccessed(new Date());
		}
		return ret;
	}

	private void removeLRUElement() {
		K lastKey = lru.poll().getKey();
		cache.remove(lastKey);
		currentSize--;
	}
	
	private void addElem(K key, V value) {
		ValueWrap newVal = null;
		TimeAccessKey queueKey = null;
		if (cache.containsKey(key)) {
			newVal = cache.get(key);
			newVal.setVal(value);
			queueKey = newVal.getPriorityEntry();
			queueKey.setLastAccessed(new Date());
		} else {
			queueKey = new TimeAccessKey(key);
			newVal = new ValueWrap(value, queueKey);
			lru.offer(queueKey);
		}
		cache.put(key, newVal);
		currentSize++;
	}
	
	private class TimeAccessKey {
		private Date lastAccessed;
		private final K key;
		public TimeAccessKey(K key) {
			this.key = key;
			this.lastAccessed = new Date();
		}
		
		public Date getLastAccessed() {
			return lastAccessed;
		}
		
		public void setLastAccessed(Date d) {
			lastAccessed = d;
		}
		
		public K getKey() {
			return key;
		}
		
	}
	
	private class ValueWrap {
		private V val;
		private final TimeAccessKey priorityEntry;
		public ValueWrap(V val, TimeAccessKey queueNode) {
			this.val = val;
			priorityEntry = queueNode;
		}
		
		public V getVal() {
			return val;
		}
		
		public void setVal(V val) {
			this.val = val;
		}
		
		public TimeAccessKey getPriorityEntry() {
			return priorityEntry;
		}
	}

}

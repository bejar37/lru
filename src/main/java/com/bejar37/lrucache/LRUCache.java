package com.bejar37.lrucache;

import java.util.HashMap;
import java.util.Map;

public class LRUCache<K, V> {
    public final static int MINSIZE = 1;

	private Map<K, Node> cache;
    private long currentSize;

    private final Node head; //sigil
    private final Node tail; //sigil
	private final long maxSize;

	public LRUCache(long maxSize) {
		if (maxSize < MINSIZE) {
			throw new IllegalArgumentException(
					"Minimum cache size is one element");
		}

        head = new Node();
        tail = head.insertAfter(new Node());
		this.maxSize = maxSize;
		cache = new HashMap<K, Node>();
		currentSize = 0;
	}

	public synchronized void put(K key, V value) {
        if (key == null) return;
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
			Node val = cache.get(key);
			ret = val.getVal();
            head.insertAfter(val.removeSelf()); //recently used element goes to head of list
		}
		return ret;
	}

	private void removeLRUElement() {
        //this in theory should always be true, but just in case.
        if (tail.getBefore() != head && tail.getBefore() != null) {
            Node toRemove = tail.getBefore().removeSelf(); //last element in list is LRU
            K key = toRemove.getKey();
            cache.remove(key);
            currentSize--;
        }
	}
	
	private void addElem(K key, V value) {
        Node newVal;
        //make sure we support "resetting" cached value
		if (cache.containsKey(key)) {
			newVal = cache.get(key);
			newVal.removeSelf().setVal(value);
		} else {
			newVal = new Node(key, value);
		}
        head.insertAfter(newVal); //recently used element goes to head of list
		cache.put(key, newVal);
		currentSize++;
	}

    /**
     * Rudimentary double-linked list with
     * insertAfter and removeSelf operations.
     */
    private class Node {
        private final K key;
        private V val;
        private Node next;
        private Node before;

        public Node(K key, V val) {
            this.key = key;
            this.val = val;
        }

        public Node() {
            this(null, null);
        }

        /**
         * Links the node's previous node to the next node.
         * @return current object
         */
        public Node removeSelf() {
            if (before != null) {
                before.setNext(next);
            }
            if (next != null) {
                next.setBefore(before);
            }
            return this;
        }

        /**
         * Inserts node into doubly-linked list after this node.
         * @param n to insert
         * @return inserted node
         */
        public Node insertAfter(Node n) {
            n.setBefore(this);
            n.setNext(next);

            if (next != null) {
                next.setBefore(n);
            }

            next = n;

            return n;
        }

        public K getKey() {
            return key;
        }

        public V getVal() {
            return val;
        }

        public void setVal(V v) {
            this.val = v;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Node getBefore() {
            return before;
        }

        public void setBefore(Node before) {
            this.before = before;
        }

    }

}

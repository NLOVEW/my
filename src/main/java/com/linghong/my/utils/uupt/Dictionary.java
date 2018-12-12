package com.linghong.my.utils.uupt;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Dictionary<K, V> {

	Map<K, V> itemMap = new HashMap<K, V>();

	public Dictionary() {
		super();
	}

	public void add(K key, V value) {
		itemMap.put(key, value);
	}

	public boolean isEmpty() {
		return itemMap.isEmpty();
	}

	public Map<K, V> getHashMap() {
		return itemMap;
	}

	public Set<K> keySet() {
		return itemMap.keySet();
	}

	public V get(K key) {
		return itemMap.get(key);
	}

}

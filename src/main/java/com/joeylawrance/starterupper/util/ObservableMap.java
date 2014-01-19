package com.joeylawrance.starterupper.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A wrapper around a map to make it observable using ObservableMapListener.
 * 
 * @author Joey Lawrance
 *
 * @param <K> key type
 * @param <V> value type
 */
public class ObservableMap<K,V> implements Map<K, V> {
	Map<K,V> map;
	List<ObservableMapListener<K,V>> listeners = new ArrayList<ObservableMapListener<K,V>>();
	public ObservableMap(Map<K,V> map) {
		this.map = map;
	}
	private void fireAddKey(K key) {
		for (ObservableMapListener<K,V> listener : listeners) {
			listener.mapKeyAdded(this, key);
		}
	}
	private void fireChangeKeyValue(K key, V value) {
		for (ObservableMapListener<K,V> listener : listeners) {
			listener.mapKeyValueChanged(this, key, value);
		}
	}
	private void fireRemoveKey(K key, V value) {
		for (ObservableMapListener<K,V> listener : listeners) {
			listener.mapKeyRemoved(this, key, value);
		}
	}	
	public void addObservableMapListener(ObservableMapListener<K,V> listener) {
		listeners.add(listener);
	}
	public void removeObservableMapListener(ObservableMapListener<K,V> listener) {
		listeners.remove(listener);
	}

	@Override
	public void clear() {
		for (K key : this.keySet()) {
			fireRemoveKey(key, map.get(key));
		}
		map.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	@Override
	public V get(Object key) {
		return map.get(key);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public V put(K key, V value) {
		if (!this.containsKey(key))
			this.fireAddKey(key);
		map.put(key, value);
		this.fireChangeKeyValue(key, value);
		return value;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (K key : m.keySet()) {
			this.put(key, m.get(key));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove(Object key) {
		this.fireRemoveKey((K)key, map.get(key));
		return null;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}
	/**
	 * Fire a change keyvalue event for every key in the keyset.
	 */
	public void fire() {
		for (K key : keySet()) {
			this.fireChangeKeyValue(key, this.get(key));
		}
	}
}

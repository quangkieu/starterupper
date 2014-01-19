package com.joeylawrance.starterupper.util;

/**
 * Inspired by jdesktop.org's ObservableMapListener interface.
 * @author Joey Lawrance
 *
 * @param <K> key type
 * @param <V> value type
 */
public interface ObservableMapListener<K,V> {
	void mapKeyAdded(ObservableMap<K,V> map, K key);
	void mapKeyRemoved(ObservableMap<K,V> map, K key, V value);
	void mapKeyValueChanged(ObservableMap<K,V> map, K key, V value);
}

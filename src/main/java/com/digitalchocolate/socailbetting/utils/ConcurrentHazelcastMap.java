package com.digitalchocolate.socailbetting.utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class ConcurrentHazelcastMap<K, V> implements ConcurrentMap<K, V>, Serializable{
    
	private static final long serialVersionUID = 8619978452955222813L;
	private String identifier="general_socialbetting";
	private static final HazelcastInstance instance = Hazelcast.newHazelcastInstance();
	private ConcurrentMap<K,V> map;
   
	public ConcurrentHazelcastMap(String identifier)
    {
    	if(StringUtils.isNotBlank(identifier))
    	{
    		this.identifier = identifier;
    	}
    	map = instance.getMap(this.identifier);
    }

	@Override
	public int size() {
		return this.map.size();
	}

	@Override
	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.map.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return this.map.get(key);
	}

	@Override
	public V put(K key, V value) {
		return this.map.put(key, value);
	}

	@Override
	public V remove(Object key) {
		return this.map.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		this.map.putAll(m);
		
	}

	@Override
	public void clear() {
		this.map.clear();
		
	}

	@Override
	public Set<K> keySet() {
		return this.map.keySet();
	}

	@Override
	public Collection<V> values() {
		return this.map.values();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return this.map.entrySet();
	}

	@Override
	public V putIfAbsent(K key, V value) {
		return this.map.putIfAbsent(key, value);
	}

	@Override
	public boolean remove(Object key, Object value) {
		return this.map.remove(key, value);
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		return this.map.replace(key, oldValue, newValue);
	}

	@Override
	public V replace(K key, V value) {
		return this.map.replace(key, value);
	}
}

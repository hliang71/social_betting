package com.digitalchocolate.socailbetting.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import com.digitalchocolate.socailbetting.exception.SocialBettingInternalException;

public class ConcurrentRedisMap <K, V> implements ConcurrentMap<K, V>, Serializable{
	private final Logger log = Logger.getLogger(ConcurrentRedisMap.class);
	private static final long serialVersionUID = -3657115289642461905L;
	private String identifier="general_socialbetting";
	private String subIdentifer = "sub";
	private final static String SUB_ID="SETS";
	private ApplicationContext ctx;
	private RedisTemplate<String, V> redisTemplate;
	
	private Integer ttl;
	
	public ConcurrentRedisMap(String identifier, ApplicationContext ctx, Integer ttl)
    {
    	if(StringUtils.isNotBlank(identifier))
    	{
    		this.identifier = identifier;
    		this.subIdentifer = identifier+SUB_ID;
    	}
    	if(ttl != null)
    	{
    		this.ttl = ttl;
    	}
    	this.ctx = ctx;
    	this.redisTemplate = (RedisTemplate<String,V>) this.ctx.getBean("redisTemplate", RedisTemplate.class);
    	
    }
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Integer getTtl() {
		return ttl;
	}
	public void setTtl(Integer ttl) {
		this.ttl = ttl;
	}
	@Override
	public int size() {
		
		return this.redisTemplate.opsForHash().size(identifier).intValue();
	}

	@Override
	public boolean isEmpty() {
		return this.size() == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		
		return this.redisTemplate.opsForHash().hasKey(this.identifier, key);
	}

	@Override
	public boolean containsValue(Object value) {
		
		return this.redisTemplate.opsForHash().values(this.identifier).contains(value);
	}

	@Override
	public V get(Object key) {
		
		return (V)this.redisTemplate.opsForHash().get(this.identifier, key);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public V put(final K key, final V value) {
		try
		{
			 V v = (V)this.redisTemplate.execute(new SessionCallback() {
					
					@Override
					public Object execute(RedisOperations operations) throws DataAccessException 
					{
						operations.opsForHash().put(identifier, key, value);
						if(key != null)
						{
							Long currentTime = System.currentTimeMillis();
							
							Double score = (ttl == null)? currentTime.doubleValue() : (currentTime.doubleValue() + ttl*1000);
							operations.opsForZSet().add(subIdentifer, key, score);
						}
						
						return value;	
					}
				}
	        );
			
			//this.redisTemplate.opsForHash().put(this.identifier, key, value);
			return v;
		}catch(Exception e)
		{
			throw new SocialBettingInternalException("failed to add to the timer.", e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public V remove(final Object key) {
		
		this.redisTemplate.execute(new SessionCallback() {
			
			@Override
			public Object execute(RedisOperations operations) throws DataAccessException 
			{
				operations.opsForHash().delete(identifier, key);
				if(key != null)
				{
					operations.opsForZSet().remove(subIdentifer, key);
				}
				
				return key;	
			}
		});
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		this.redisTemplate.opsForHash().putAll(this.identifier, m);
		
	}

	@Override
	public void clear() {
		this.redisTemplate.delete(this.identifier);
		
	}

	@Override
	public Set<K> keySet() {
		
		return (Set<K>)this.redisTemplate.opsForHash().keys(this.identifier);
	}

	@Override
	public Collection<V> values() {
		Collection<V> result = null;
		try
		{
			Long currentTime = System.currentTimeMillis();
			Double score = currentTime.doubleValue();
			Set<String> keys =(Set<String>)this.redisTemplate.opsForZSet().rangeByScore(this.subIdentifer, 0, score);
			result = new ArrayList<V>();
			for(String key : keys)
			{
				V v = this.get(key);
				result.add(v);
			}
		}catch(Exception e)
		{
			log.error("get values threw exceptions:", e);
		}
		return result;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return ((Map<K,V>)this.redisTemplate.opsForHash().entries(this.identifier)).entrySet();
	}

	@Override
	public V putIfAbsent(K key, V value) {
		this.redisTemplate.opsForHash().putIfAbsent(this.identifier, key, value);
		return null;
	}

	@Override
	public boolean remove(Object key, Object value) {
		Object v = this.get(key)	;
		if(v.equals(value))
		{
			this.redisTemplate.opsForHash().delete(this.identifier, key);
			return true;
		}
		return false;
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		boolean success = this.remove(key, oldValue);
		if(success)
		{
			this.put(key, newValue);
			return true;
		}
		return false;
	}

	@Override
	public V replace(K key, V value) {
		Object ob = this.get(key);
		V result = null;
		if(ob != null)
		{
			this.remove(key);
			result = this.put(key, value);
		}
		return result;
	}

}

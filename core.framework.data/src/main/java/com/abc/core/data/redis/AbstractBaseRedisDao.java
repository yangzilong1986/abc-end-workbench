package com.abc.core.data.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

public abstract class AbstractBaseRedisDao<K, V> {

	@Autowired
	protected RedisTemplate<K, V> redisTemplate;

	@Autowired
	Jackson2JsonRedisSerializer jackson2JsonRedisSerializer;
	
	private volatile RedisSerializer redisSerializer;
	
	public RedisTemplate<K,V> getRedisTemplate(){
		return this.redisTemplate;
	}

	public RedisSerializer<?> getKeySerializer(){
		return this.redisTemplate.getKeySerializer();
	}
	
	public RedisSerializer<?> getValueSerializer(){
		return this.redisTemplate.getValueSerializer();
	}
	
	public RedisSerializer<?> getHashKeySerializer(){
		return this.redisTemplate.getHashKeySerializer();
	}
	
	public RedisSerializer<?> getHashValueSerializer(){
		return this.redisTemplate.getHashValueSerializer();
	}
	
	public  RedisSerializer<?> getJackson2Serializer(){
//		this.redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
		return jackson2JsonRedisSerializer;
	}
	
	public  void setRedisSerializer(RedisSerializer<K> redisSerializer){
		this.redisSerializer=redisSerializer;
	}
	
	public  RedisSerializer getRedisSerializer(){
		return redisSerializer;
	}
	
}

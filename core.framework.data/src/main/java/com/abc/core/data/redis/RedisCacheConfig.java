package com.abc.core.data.redis;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import redis.clients.jedis.JedisPoolConfig;
import org.springframework.core.io.support.ResourcePropertySource;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = { "com.abc.core" })
@PropertySource("classpath:properties/config-redis.properties")
@EnableCaching
public class RedisCacheConfig extends CachingConfigurerSupport {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheConfig.class);
	@Resource
	private Environment env;


	@Bean
	JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		int maxIdle = Integer.parseInt(env.getProperty("redis.maxIdle"));
		int maxActive = Integer.parseInt(env.getProperty("redis.maxActive"));
		int maxWait = Integer.parseInt(env.getProperty("redis.maxWait"));
		boolean testOnBorrow = Boolean.parseBoolean(env.getProperty("redis.testOnBorrow"));
		// 最大空闲数
		jedisPoolConfig.setMaxIdle(maxIdle);
		// 最大建立连接等待时间
		jedisPoolConfig.setMaxWaitMillis(maxWait);
		// 是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个
		jedisPoolConfig.setTestOnBorrow(testOnBorrow);
		return jedisPoolConfig;
	}

	@Bean
	RedisClusterConfiguration redisClusterConfiguration() {
		org.springframework.core.env.PropertySource propertySource = resourcePropertySource();
		RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(propertySource);

		return redisClusterConfiguration;
	}

	org.springframework.core.env.PropertySource resourcePropertySource() {
		String name = env.getProperty("redis.cluster.properties.name");
		String resource = env.getProperty("redis.cluster.properties.resource");
		if (null == name || name.equals("")) {
			name = "redis.properties";
		}
		if (null == resource || resource.equals("")) {
			resource = "classpath:redis.properties";
		}
		return loadPropertySource(name, resource);
	}

	public static org.springframework.core.env.PropertySource loadPropertySource(String name, String resource) {
		try {
			ResourcePropertySource resourcePropertySource = new ResourcePropertySource(name, resource);
			return resourcePropertySource;
		} catch (IOException e) {
			LOGGER.error("装载属性错误，",e.getMessage());
			return null;
		}
	}

	@Bean
	public JedisConnectionFactory redisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
		JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig);
		redisConnectionFactory.setHostName(env.getProperty("redis.hostName"));
		redisConnectionFactory.setPort(Integer.parseInt(env.getProperty("redis.port")));

		return redisConnectionFactory;
	}

	
	@Bean
	public RedisTemplate redisTemplateJson(RedisConnectionFactory cf) {
		RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
		// 如果不配置Serializer，那么存储的时候缺省使用String，如果用User类型存储，那么会提示错误
		redisTemplate.setKeySerializer(new org.springframework.data.redis.serializer.StringRedisSerializer());
		redisTemplate
				.setValueSerializer(new org.springframework.data.redis.serializer.JdkSerializationRedisSerializer());
		redisTemplate.setHashKeySerializer(new org.springframework.data.redis.serializer.StringRedisSerializer());
		redisTemplate.setHashValueSerializer(
				new org.springframework.data.redis.serializer.JdkSerializationRedisSerializer());
		redisTemplate.setConnectionFactory(cf);
		return redisTemplate;
	}

	@Primary
	@Bean
	public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory cf,
			Jackson2JsonRedisSerializer<?> jackson2JsonRedisSerializer) {
		RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
		template.setKeySerializer(new org.springframework.data.redis.serializer.StringRedisSerializer());
		template.setValueSerializer(jackson2JsonRedisSerializer);
		template.setHashKeySerializer(new org.springframework.data.redis.serializer.StringRedisSerializer());
		template.setHashValueSerializer(jackson2JsonRedisSerializer);
		template.setConnectionFactory(cf);
//		template.afterPropertiesSet();
		
		return template;
	}

	@Bean
	public CacheManager cacheManager(RedisTemplate<?, ?> redisTemplate) {
		RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
		cacheManager.setDefaultExpiration(Integer.parseInt(env.getProperty("redis.expiration")));
		return cacheManager;
	}

	@Bean
	public Jackson2JsonRedisSerializer jackson2JsonRedisSerializer() {
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		om.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		om.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);

		jackson2JsonRedisSerializer.setObjectMapper(om);
		return jackson2JsonRedisSerializer;
	}
	
	@Bean
	public KeyGenerator wiselyKeyGenerator() {
		return new KeyGenerator() {
			public Object generate(Object target, Method method, Object... params) {
				StringBuilder sb = new StringBuilder();
				String[] type=handleCashEntity(target,method);
				if(type!=null){
					sb.append(type[0]);
				}else{
					sb.append(target.getClass().getName());
					sb.append(method.getName());
				}
				for (Object obj : params) {
					if(obj instanceof String){
						sb.append(type[1]);
						sb.append(obj);
						break;
					}
					if(obj.getClass().isPrimitive()){
						//
					}else{
						
						String key=toMap(obj,type[1]) ;
						if(key!=null){
							sb.append(key);
							break;
						}
						
					}
				}
				return sb.toString();
			}
		};

	}
	
	private static String[] handleCashEntity(Object object,Method method){
		Class clazz=object.getClass();
		Annotation annotation=clazz.getAnnotation(AbcCacheEntity.class);
		String[] entity={"ABC","name"};
		
		if(annotation==null){
			annotation=method.getAnnotation(AbcCacheEntity.class);
		}
		if(annotation!=null){
			AbcCacheEntity abcCacheEntity=(AbcCacheEntity) annotation;
			entity[0]= abcCacheEntity.value();
			entity[1]= abcCacheEntity.keyName();
		}
		
		return entity;
		
	}
	public static String toMap(Object obj,String key) {
		if(obj instanceof String){
			return key+(String) obj;
		}

		BeanMap beanMap = new BeanMap(obj);
		Iterator<String> it = beanMap.keyIterator();
		while (it.hasNext()) {
			String name = it.next();
			Object value = beanMap.get(name);
			// 转换时会将类名也转换成属性，此处去掉
			if (value != null && !name.equals("class")&&name.contains(key)) {
				return key+(String) value;
			}
		}
		return null;
	}
}
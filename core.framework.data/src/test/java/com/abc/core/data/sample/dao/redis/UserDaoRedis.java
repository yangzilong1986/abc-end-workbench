package com.abc.core.data.sample.dao.redis;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Repository;

import com.abc.core.data.redis.AbstractBaseRedisDao;
import com.abc.core.data.sample.dao.UserCustomerRepository;
import com.abc.core.data.sample.domain.User;

@Repository("userDaoRedis")
public class UserDaoRedis extends AbstractBaseRedisDao<String, User> implements UserCustomerRepository {

	public User findByUsername(final String username) {
		
		 User result = redisTemplate.execute(new RedisCallback<User>() {  
	            public User doInRedis(RedisConnection connection)  
	                    throws DataAccessException {  
	                RedisSerializer<String> serializer = (RedisSerializer<String>) getKeySerializer();  
	                byte[] key = serializer.serialize(username);  
	                byte[] value = connection.get(key);  
	                if (value == null) {  
	                    return null;  
	                }  
	                RedisSerializer<User> valueSerializer = (RedisSerializer<User>) getJackson2Serializer();  
	                User user = valueSerializer.deserialize(value);  
	                return user;
	            }  
	        });  
	        return result;  
	}

	public User insertMy(final User user) {
		 redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = (RedisSerializer<String>) getKeySerializer();
				RedisSerializer<Object> valueSerializer = (RedisSerializer<Object>) getJackson2Serializer();
				byte[] key = serializer.serialize(user.getUsername());
				byte[] userByte = valueSerializer.serialize(user);
				
				return connection.setNX(key, userByte);
			}
		});
		return null;
	}

}

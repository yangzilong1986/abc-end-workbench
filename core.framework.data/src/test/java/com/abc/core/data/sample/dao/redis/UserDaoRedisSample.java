package com.abc.core.data.sample.dao.redis;

import org.springframework.stereotype.Repository;

import com.abc.core.data.redis.AbcCacheEntity;
import com.abc.core.data.sample.dao.UserCustomerRepository;
import com.abc.core.data.sample.domain.User;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

@Repository("userDaoRedisSample")
@AbcCacheEntity(value="User",keyName="username")
public class UserDaoRedisSample implements UserCustomerRepository {

	/**
	 * 查询
	 */
	@Cacheable(value = "user", keyGenerator = "wiselyKeyGenerator")
	@AbcCacheEntity(value="User",keyName="username")
	public User findByUsername(String username) {
		User user = new User();
		user.setUsername(username);
		return user;
	}
	
	 @CachePut(value="user",  keyGenerator="wiselyKeyGenerator")
	 @AbcCacheEntity("User")
	public User insertMy(User user) {
		 User cachUser = new User();
		 cachUser.setId(user.getId());
		 cachUser.setPassword(user.getPassword());
		 cachUser.setUsername(user.getUsername());
		 cachUser.setAccountNonExpired(user.isAccountNonExpired());
		 cachUser.setAccountNonLocked(user.isAccountNonLocked());
		 cachUser.setCredentialsNonExpired(user.isCredentialsNonExpired());
		 cachUser.setEnabled(user.isEnabled());
		return user;
	}

	public User saveUser(Long id) {

		User user = new User();
		user.setId(id);
		return user;
	}

	public User getUser(Long id) {
		User user = new User();
		user.setId(id);
		return user;
	}

	public void deleteUser(Long id) {
		// TODO Auto-generated method stub

	}

}

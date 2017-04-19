package com.abc.core.data.sample.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.abc.core.data.sample.dao.UserCustomerRepository;
import com.abc.core.data.sample.domain.User;
import com.abc.core.data.sample.service.UserDetailsService;

@Service("userServiceRedis")
public class UserServiceRedis implements UserDetailsService{

	@Autowired
	@Qualifier(value="userDaoRedisSample")
	UserCustomerRepository userDaoRedis;

	public User findByUsername(String username) {
		// TODO Auto-generated method stub
		return userDaoRedis.findByUsername(username);
	}

	public User findByUsernameMy(String username) {
		// TODO Auto-generated method stub
		return userDaoRedis.findByUsername(username);
	}

	public User insertMy() {
		User user=new User();
		user.setId(1L);
		user.setUsername("ABC-END-POINT-END");
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setEnabled(true);
		user.setPassword("88888");
		userDaoRedis.insertMy(user);
		return user;
	}

}

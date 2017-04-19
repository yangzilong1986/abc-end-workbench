package com.abc.core.data.sample.dao;

import com.abc.core.data.sample.domain.User;

public interface UserDao {

	public User findByUsername(String username) ;

}

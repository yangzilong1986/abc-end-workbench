package com.abc.core.data.sample.dao;

import com.abc.core.data.sample.domain.User;

public interface UserCustomerRepository {
	public User findByUsername(String username);
	public User insertMy(User user);

}

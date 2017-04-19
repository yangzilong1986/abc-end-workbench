package com.abc.core.data.sample.service;

import com.abc.core.data.sample.domain.User;


public interface UserDetailsService {
	public User findByUsername(String username) ;
	public User findByUsernameMy(String username) ;
	public User insertMy() ;
}

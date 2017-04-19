package com.abc.core.data.sample.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.abc.core.data.sample.domain.User;

public interface SecurityUserService {

	public User save(User user);
	public User findOne(Long id) ;
	public void delete(Long id);
}

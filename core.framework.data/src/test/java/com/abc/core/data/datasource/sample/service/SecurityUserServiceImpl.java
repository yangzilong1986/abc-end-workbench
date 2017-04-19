package com.abc.core.data.datasource.sample.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.abc.core.data.datasource.sample.repository.UserRepository;
import com.abc.core.data.sample.domain.User;
import com.abc.core.data.sample.service.SecurityUserService;

@Service
public class SecurityUserServiceImpl implements SecurityUserService {
	
	@Autowired
	private UserRepository userRepository;
	
	
	public User save(User user) {
		return userRepository.save(user);
	}
	public User findOne(Long id) {
		return userRepository.findOne(id);
	}
	public void delete(Long id) {
		
	}
}

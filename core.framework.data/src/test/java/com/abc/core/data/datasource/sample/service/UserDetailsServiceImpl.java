package com.abc.core.data.datasource.sample.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abc.core.data.datasource.sample.repository.RoleRepository;
import com.abc.core.data.datasource.sample.repository.UserRepository;
import com.abc.core.data.sample.domain.User;
import com.abc.core.data.sample.service.UserDetailsService;

@Service("userDetailsService")
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;
	
	public User findByUsername(String username)  {
		User user=userRepository.findOne(1L);
		return null;
	}

	public User findByUsernameMy(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	public User insertMy() {
		// TODO Auto-generated method stub
		return null;
	}
}

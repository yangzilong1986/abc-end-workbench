package com.abc.core.data.sample.service;

import com.abc.core.data.sample.domain.Role;

public interface RoleService {

	public Role findByUsername(String username) ;
	public Role findByUsernameMy(String username) ;
}

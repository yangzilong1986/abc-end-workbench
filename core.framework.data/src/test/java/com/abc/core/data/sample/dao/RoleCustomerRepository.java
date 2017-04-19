package com.abc.core.data.sample.dao;

import com.abc.core.data.sample.domain.Role;

public interface RoleCustomerRepository {
	public Role findOne(String name);
	public Role insertMy(Role role);
}

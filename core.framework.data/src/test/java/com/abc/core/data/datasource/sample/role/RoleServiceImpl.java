package com.abc.core.data.datasource.sample.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abc.core.data.datasource.AbcDataSource;
import com.abc.core.data.datasource.sample.repository.RoleRepository;
import com.abc.core.data.sample.dao.RoleCustomerRepository;
import com.abc.core.data.sample.domain.Role;
import com.abc.core.data.sample.service.RoleService;

import org.springframework.transaction.annotation.Propagation;
@Service("roleService")
//@Transactional(propagation = Propagation.REQUIRES_NEW)
@Transactional
public class RoleServiceImpl implements RoleService {

	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	RoleCustomerRepository roleDao;
	
	public Role findByUsername(String username) {
		// TODO Auto-generated method stub
		Role role=roleRepository.findOne(0L);
		return role;
	}

	public Role findByUsernameMy(String username) {
		Role role=roleDao.findOne(username);
		return role;
	}

}

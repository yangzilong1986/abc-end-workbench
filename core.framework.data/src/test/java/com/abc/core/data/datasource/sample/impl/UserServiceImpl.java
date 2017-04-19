package com.abc.core.data.datasource.sample.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abc.core.data.datasource.AbcDataSource;
import com.abc.core.data.sample.dao.RoleCustomerRepository;
import com.abc.core.data.sample.dao.UserCustomerRepository;
import com.abc.core.data.sample.dao.UserDao;
import com.abc.core.data.sample.domain.Role;
import com.abc.core.data.sample.domain.User;
import com.abc.core.data.sample.service.UserDetailsService;

@Service("myUserService")
//配置事务之后则在同一数据源中完成
@Transactional
public class UserServiceImpl implements UserDetailsService{

	@Autowired
	@Qualifier(value="myUserDaoImpl")
	UserDao myUserDaoImpl;

	@Autowired
	@Qualifier(value="userDaoMy")
	UserCustomerRepository userDao;
	
	@Autowired
	RoleCustomerRepository roleDao;
	
	//setdataSourceOne signature is User com.abc.core.data.datasource.sample.impl.UserServiceImpl.findByUsername(String)
	public User findByUsername(String username) {
		// TODO Auto-generated method stub
		User user =myUserDaoImpl.findByUsername("test");
		return user;
	}

	public User findByUsernameMy(String username) {
		User user =userDao.findByUsername("test");
		return user;
	}

//	 @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
//	 @AbcDataSource("druidDataSource")
//	 @AbcDataSource("dbcpDataSource")
	public User insertMy() {
		Role role=new Role();
		role.setName("ABC_TEST");
		role.setId(2L);
		
		
		User user=new User();
		user.setId(9L);
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setUsername("ABC");
		user.setEnabled(true);
		user.setPassword("9999");
		userDao.insertMy(user);
		roleDao.insertMy(role);
		return null;
	}
	
}

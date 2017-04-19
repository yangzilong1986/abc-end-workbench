package com.abc.core.data.datasource.mybatis.user;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abc.core.data.datasource.AbcDataSource;
import com.abc.core.data.sample.dao.UserCustomerRepository;
import com.abc.core.data.sample.domain.Role;
import com.abc.core.data.sample.domain.User;

import java.util.List;

@Repository("userDaoMy")
@AbcDataSource("dbcpDataSource")
public class UserDaoMy implements UserCustomerRepository{

	private static final String NAMESPACE = "User";
	
	@Autowired
	SqlSessionTemplate sqlSession;
	
//	@AbcDataSource("dbcpDataSource")
	public User findByUsername(String username) {
		User user=null;
		try{
			user=sqlSession.selectOne(NAMESPACE + ".loadOne", 1L);;
		}catch(Exception e){
			e.printStackTrace();
		}
		return user;
	}
	
	
	public User insertMy(User user) {
		sqlSession.insert(NAMESPACE + ".insertUser", user);
		return null;
		
	}

}

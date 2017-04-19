package com.abc.core.data.datasource.mybatis.role;

import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abc.core.data.datasource.AbcDataSource;
import com.abc.core.data.sample.dao.RoleCustomerRepository;
import com.abc.core.data.sample.domain.Role;

@Repository("roleDaoMy")
@AbcDataSource("druidDataSource")
public class RoleDaoMy   implements RoleCustomerRepository {

	private static final String NAMESPACE = "Role";
	
	@Autowired
	SqlSessionTemplate sqlSession;
	

	
	public Role findOne(String name) {
		try{
			Role role=sqlSession.selectOne(NAMESPACE + ".loadByName", name);
			return role;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}


	public Role insertMy(Role role) {
		sqlSession.insert(NAMESPACE + ".insertOrder", role);
		return null;
	}

}

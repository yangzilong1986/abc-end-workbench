package com.abc.core.data.datasource.sample.dao.user;

import com.abc.core.data.sample.dao.UserDao;
import com.abc.core.data.sample.domain.Role;
import com.abc.core.data.sample.domain.User;

import java.util.List;

import javax.annotation.Resource;  

import org.hibernate.Query;  
import org.hibernate.SessionFactory;  
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;  

@Repository("myUserDaoImpl")
public class UserDaoImpl implements UserDao{

    @Resource  
    protected SessionFactory sessionFactory;  

	
	public User findByUsername(String username) {
		String hql = "from User";  
		Query query = sessionFactory.getCurrentSession().createQuery(hql);  
		List<User> list=query.list();  
		if(list==null||list.isEmpty()){
			return null;
		}
		return list.get(0);
	}

}

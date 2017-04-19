package com.abc.core.data.datasource.sample.test;

import java.util.List;  

import javax.annotation.Resource;  
  
import org.junit.Test;  
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ContextConfiguration;  
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import com.abc.core.data.App;
import com.abc.core.data.config.DataSourceConfig;
import com.abc.core.data.datasource.sample.repository.RoleRepository;
import com.abc.core.data.sample.domain.Role;
import com.abc.core.data.sample.domain.User;
import com.abc.core.data.sample.service.RoleService;
import com.abc.core.data.sample.service.UserDetailsService;

//@RunWith(SpringRunner.class)
////@ContextConfiguration(locations ="classpath:config/data-spring.xml")
//@ContextConfiguration(
//			loader = AnnotationConfigContextLoader.class, 
//			classes = { DataSourceConfig.class })
//@SpringBootTest
//@SpringBootApplication
//@Transactional
public class DynamicDataSourceTest {
	@Autowired(required=true)
	@Qualifier(value="myUserService")
	UserDetailsService userDetailsService;//userDetailsService
	
	@Autowired 
	RoleService roleService;
	
	
	
	@Test
	public void dnamicDataSourceTest() throws Exception {
		roleService.findByUsername("002S");
		userDetailsService.findByUsername("001");
	}
	
	@Test
	public void dnamicDataSourceMy() throws Exception {
		userDetailsService.insertMy();
		Role role=roleService.findByUsernameMy("ROLE_ADMIN");
		User user=userDetailsService.findByUsernameMy("test");
		System.out.println("User is");
		System.out.println(user);
	
		System.out.println("Role is");
		System.out.println(role);
		System.out.println("Over is");
		//one dbcp: restfulsecurity 
		//id=1, username=test, password=123
		//two druid:restsec 有值
		
	}

}

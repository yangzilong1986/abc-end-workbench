package com.abc.core.data.sample.aop;
import java.util.List;  

import javax.annotation.Resource;  
  
import org.junit.Test;  
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ContextConfiguration;  
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import com.abc.core.data.sample.aop.service.AccountService;
import com.abc.core.data.sample.aop.service.IUserService;
import com.abc.core.data.sample.aop.service.Sample;
import com.abc.core.data.sample.domain.Account;



@RunWith(SpringRunner.class)
//@ContextConfiguration(locations ="classpath:config/data-spring.xml")
@ContextConfiguration
@SpringBootTest
@EnableAspectJAutoProxy

public class AOPTest {
	
	@Autowired
	IUserService userService;

	@Autowired
	AccountService accountService;
	
	@Autowired
	Sample sample;
	
	@Test
	public void testSample(){
		Account account =new Account();
		account.setName("myAbc");
		sample.sampleGenericMethod(account);;
	}
	@Test
	public void testUserService() {
		userService.add();
	}

	@Test
	public void testAccountService() {
		Account account =new Account();
		account.setName("myAbc");
		accountService.add(account);
	}
}

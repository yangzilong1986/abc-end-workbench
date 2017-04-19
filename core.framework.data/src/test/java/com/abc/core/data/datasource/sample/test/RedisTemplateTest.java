package com.abc.core.data.datasource.sample.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import com.abc.core.data.redis.RedisCacheConfig;
import com.abc.core.data.sample.domain.Role;
import com.abc.core.data.sample.domain.User;
import com.abc.core.data.sample.service.RoleService;
import com.abc.core.data.sample.service.UserDetailsService;

@RunWith(SpringRunner.class)

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { RedisCacheConfig.class })
@SpringBootTest
@SpringBootApplication
public class RedisTemplateTest {

	@Autowired(required = true)
	@Qualifier(value = "userServiceRedis")
	UserDetailsService userServiceRedis;

	@Test
	public void redisUserDetailsService() {
		try {
			System.out.println("User redisUserDetailsService");
			userServiceRedis.insertMy();
			User user = userServiceRedis.findByUsername("ABC-END-POINT-END");
			System.out.println("User is");
			System.out.println(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

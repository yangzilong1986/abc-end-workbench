package com.abc.core.data.redis;

import java.lang.reflect.Field;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//@Aspect
//@Component("cacheKeyAspect")
public class CacheKeyAspect {
	private static final Logger LOGGER = LoggerFactory.getLogger(CacheKeyAspect.class);
	private static final String PONIT_EXPRESS_SAVLE = "execution(* com.abc.core..*.*(..)) "
			+ "  ";

	@Before(value = PONIT_EXPRESS_SAVLE + "  && @annotation(abcCacheEntity)")
	public void configDataSourceForMethod(JoinPoint jp ,AbcCacheEntity abcCacheEntity) {

		String code = abcCacheEntity.value();
		
		LOGGER.info("code is:",code);
		
	}

}

package com.abc.core.data.datasource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.ibatis.mapping.Environment;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.DruidDataSource;  
  
@Aspect
@Component("dataSourceInterceptor")
public class DataSourceInterceptor  implements Ordered{  
//	(execution(* com.abc.core.data.sample.aop.service.impl..*.*(..))) 
	private static final String PONIT_EXPRESS="execution(* com.abc.core.data.datasource.sample.service.*.*(..))";
	private static final String PONIT_EXPRESS_SAVLE="execution(* com.abc.core.data.datasource.sample.role.*.*(..))";

	private static final int DEFAULT_MAX_RETRIES = 2;
	private int maxRetries = DEFAULT_MAX_RETRIES;
	private int order = 1;
	
	@Autowired
	ApplicationContext context;
	
	@Pointcut(PONIT_EXPRESS)
	public void checkService() {
	}
	
	@Before("checkService()")
    public void setdataSourceOne(JoinPoint jp) {  
		Signature signature=jp.getSignature();
		System.err.println("setdataSourceOne signature is "+signature.toString());
    	DataSourceContextHolder.setDataSourceType("dataSourceOne");  
    }  
	
	@Pointcut(PONIT_EXPRESS_SAVLE)
	public void savleDataSource() {
	}
	
	@Before("savleDataSource()")
    public void setdataSourceTwo(JoinPoint jp) { 
		Signature signature=jp.getSignature();
		System.err.println("setdataSourceTwo signature is "+signature.toString());
       
    	DataSourceContextHolder.setDataSourceType("dataSourceTwo");  
    }  
	
	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}


}

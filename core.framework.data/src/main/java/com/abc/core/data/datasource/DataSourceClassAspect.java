package com.abc.core.data.datasource;

import java.lang.reflect.Field;

import org.apache.ibatis.mapping.Environment;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@Component("dataSourceClassAspect")
public class DataSourceClassAspect implements Ordered{
	private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceClassAspect.class);
	private static final String PONIT_EXPRESS_SAVLE = "execution(* com.abc.core..*.*(..)) "
			+ "  ";
	
	private static final int DEFAULT_MAX_RETRIES = 2;
	private int maxRetries = DEFAULT_MAX_RETRIES;
	private int order = 2;

	@Autowired
	ApplicationContext context;

	@Before(value = PONIT_EXPRESS_SAVLE + " && bean(*)")
	public void configDataSourceForMethod(JoinPoint jp) {
		Signature signature=jp.getSignature();
		Class declaringType=signature.getDeclaringType();
		AbcDataSource abcDataSource=(AbcDataSource) declaringType.getAnnotation(AbcDataSource.class);
		if(abcDataSource==null){
			return;
		}
		System.err.println("setdataSourceTwo signature is "+signature.toString());
		String code = abcDataSource.value();

		Object dataSource = context.getBean(code);// 获取数据源

		try {
			// 修改jdbcTemplate的数据源
			// JdbcTemplate jdbcTemplate =
			// (JdbcTemplate)context.getBean(JdbcTemplate.class);
			// jdbcTemplate.setDataSource(dataSource);
			// 修改MyBatis的数据源
			SqlSessionFactoryBean sqlSessionFactoryBean = (SqlSessionFactoryBean) context
					.getBean(SqlSessionFactoryBean.class);
			Environment environment = sqlSessionFactoryBean.getObject().getConfiguration().getEnvironment();
			Field dataSourceField = environment.getClass().getDeclaredField("dataSource");
			dataSourceField.setAccessible(true);// 跳过检查
			dataSourceField.set(environment, dataSource);// 修改mybatis的数据源
		} catch (Exception e) {
			LOGGER.error("多数据源设置错误，",e.getMessage());
		}

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

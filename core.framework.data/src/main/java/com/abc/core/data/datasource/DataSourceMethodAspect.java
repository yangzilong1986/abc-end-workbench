package com.abc.core.data.datasource;

import java.lang.reflect.Field;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import org.apache.ibatis.mapping.Environment;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
@Component("dataSourceAspect")
public class DataSourceMethodAspect implements Ordered {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceMethodAspect.class);
	private static final String PONIT_EXPRESS_SAVLE = "execution(* com.abc.core..*.*(..)) "
			+ "  ";

	private static final int DEFAULT_MAX_RETRIES = 2;
	private int order = 2;

	@Autowired
	ApplicationContext context;

	@Before(value = PONIT_EXPRESS_SAVLE + "  && @annotation(abcDataSource)")
	public void configDataSourceForMethod(JoinPoint jp,AbcDataSource abcDataSource) {

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
	}

	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

}

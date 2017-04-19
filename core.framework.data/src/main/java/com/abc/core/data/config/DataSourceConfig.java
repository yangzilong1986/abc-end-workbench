package com.abc.core.data.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;

import com.abc.core.data.datasource.DynamicDataSource;
import com.alibaba.druid.pool.DruidDataSource;
import javax.annotation.Resource;
import javax.sql.DataSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true)  
// 扫描组件 com.abc.core.data.datasource.sample
@ComponentScan(basePackages = { "com.abc.core" })
//@EnableJpaRepositories(basePackages = {"com.abc.core"})
@PropertySource("classpath:properties/config.properties")
//@ImportResource("classpath:config/data-spring.xml")
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
public class DataSourceConfig {

	@Resource
	private Environment env;
	@Value("${hiberante.jap.package:'com.abc.core.data.sample.domain'}")
	private String japPackage;
	
	/**
	 * <!-- 数据源配置1 -->  
	 <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean" 
		lazy-init="false">
		<property name="dataSource" ref="dynmicDataSource" />
		<property name="mapperLocations" value="classpath*:/mapper/*Mapper.xml" />
		<property name="failFast" value="true" />
	</bean>

	 */
	
	@Bean
	SqlSessionFactoryBean sqlSessionFactoryBean(DynamicDataSource dynmicDataSource){
		SqlSessionFactoryBean sqlSessionFactoryBean=new SqlSessionFactoryBean();
		String localionPattern="classpath*:/mapper/*Mapper.xml";
		org.springframework.core.io.Resource[] mapperLocations=getResources(localionPattern);
		sqlSessionFactoryBean.setMapperLocations(mapperLocations);
		sqlSessionFactoryBean.setDataSource(dynmicDataSource);
		sqlSessionFactoryBean.setFailFast(false);
		return sqlSessionFactoryBean;
	}
	
	@Bean
	SqlSessionTemplate sqlSessionTemplate(SqlSessionFactoryBean sqlSessionFactoryBean){
		SqlSessionFactory sqlSessionFactory=null;
		try {
			sqlSessionFactory = sqlSessionFactoryBean.getObject();
		} catch (Exception e) {
		}
		SqlSessionTemplate sqlSessionTemplate=new SqlSessionTemplate(sqlSessionFactory);
		return sqlSessionTemplate;
	}
	
	org.springframework.core.io.Resource[] getResources(String locationPattern){
		ResourcePatternResolver resourceLoader=new PathMatchingResourcePatternResolver();
		try {
			org.springframework.core.io.Resource[] rs=resourceLoader.getResources(locationPattern) ;
			return rs;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Bean(name = "dynmicDataSource", value = "dynmicDataSource")
	DynamicDataSource dynmicDataSource(BasicDataSource dbcpDataSource,
			DruidDataSource druidDataSource){
		Map targetDataSources=new HashMap<String,DataSource>();
		//dbcp: restfulsecurity
		//druid:restsec
		targetDataSources.put("dataSourceOne", dbcpDataSource);
		targetDataSources.put("dataSourceTwo", druidDataSource);
		BasicDataSource baisicTest=dbcpTestDataSource();
		//targetDataSources.put("dataSourceTest", baisicTest);
		DynamicDataSource dynmicDataSource=new DynamicDataSource();
		dynmicDataSource.setTargetDataSources(targetDataSources);
		//缺省为drup
		dynmicDataSource.setDefaultTargetDataSource(druidDataSource);
		return dynmicDataSource;
	}

	
	BasicDataSource dbcpTestDataSource() {
		BasicDataSource dbcpDataSource = new BasicDataSource();
		dbcpDataSource.setDriverClassName(env.getProperty("druid.test.driverClass"));
		dbcpDataSource.setUrl(env.getProperty("dbcp.test.jdbcUrl"));
		dbcpDataSource.setUsername(env.getProperty("dbcp.test.user"));
		dbcpDataSource.setPassword(env.getProperty("dbcp.test.password"));
		dbcpDataSource.setMaxActive(Integer.parseInt(env.getProperty("dbcp.test.maxActive")));
		dbcpDataSource.setMaxIdle(Integer.parseInt(env.getProperty("dbcp.test.maxIdle")));
		return dbcpDataSource;
	}
	

	@Bean(name = "dbcpDataSource", value = "dbcpDataSource")
	BasicDataSource dbcpDataSource() {
		BasicDataSource dbcpDataSource = new BasicDataSource();
		dbcpDataSource.setDriverClassName(env.getProperty("druid.driverClass"));
		dbcpDataSource.setUrl(env.getProperty("dbcp.jdbcUrl"));
		dbcpDataSource.setUsername(env.getProperty("dbcp.user"));
		dbcpDataSource.setPassword(env.getProperty("dbcp.password"));
		dbcpDataSource.setMaxActive(Integer.parseInt(env.getProperty("dbcp.maxActive")));
		dbcpDataSource.setMaxIdle(Integer.parseInt(env.getProperty("dbcp.maxIdle")));
		return dbcpDataSource;
	}


	@Bean
	DruidDataSource druidDataSource() {
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setDriverClassName(env.getProperty("druid.driverClass"));
		druidDataSource.setUrl(env.getProperty("druid.jdbcUrl"));
		druidDataSource.setUsername(env.getProperty("druid.user"));
		druidDataSource.setPassword(env.getProperty("druid.password"));

		return druidDataSource;
	}

	@Primary
	@Bean(name = "dataSource", value = "dataSource")
	public DataSource dataSource() {// 自动配置
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getProperty("c3p0.driverClass"));
		dataSource.setUrl(env.getProperty("c3p0.jdbcUrl"));
		dataSource.setUsername(env.getProperty("c3p0.user"));
		dataSource.setPassword(env.getProperty("c3p0.password"));
		return dataSource;
	}

	@Bean
	HibernateJpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setGenerateDdl(true);
		jpaVendorAdapter.setShowSql(true);
		return jpaVendorAdapter;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
			JpaVendorAdapter jpaVendorAdapter,DynamicDataSource dynmicDataSource) {

		Properties jpaProperties = new Properties();
		//jpaProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");// validate,create,create-drop
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(dynmicDataSource);
		emf.setPackagesToScan("com.abc.core.data.sample.domain");
		emf.setJpaVendorAdapter(jpaVendorAdapter);
		emf.setJpaProperties(jpaProperties);
		return emf;
	}
	
	
	@Bean
	LocalSessionFactoryBean localSessionFactoryBean(DynamicDataSource dynmicDataSource){
		LocalSessionFactoryBean local=new LocalSessionFactoryBean();
		Properties hbProperties = new Properties();
		hbProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		local.setHibernateProperties(hbProperties);
		local.setDataSource(dynmicDataSource);
		local.setPackagesToScan("com.abc.core.data.datasource.sample.domain");
		return local;
	}
	

	
	@Bean
	public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
		return transactionManager;
	}

	@Primary
	@Bean
	public PlatformTransactionManager txManager(DynamicDataSource dataSource) {
		//
		return new DataSourceTransactionManager(dataSource);
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate(DynamicDataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}


}

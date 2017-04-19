package com.abc.core.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.abc.core.security.repository.ReaderRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private ReaderRepository readerRepository;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().// 要求登录者有READER角色
				antMatchers("/").access("hasRole('READER')").antMatchers("/**").permitAll().and()// 设置登录表单的路径
				.formLogin().loginPage("/login").failureUrl("/login?error=true");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(new UserDetailsService() {//定义自定义UserDetailsService
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				return readerRepository.findOne(username);
			}
		});
	}

}

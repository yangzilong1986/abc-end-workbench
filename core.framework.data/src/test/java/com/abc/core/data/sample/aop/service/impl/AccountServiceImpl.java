package com.abc.core.data.sample.aop.service.impl;

import org.springframework.stereotype.Service;

import com.abc.core.data.sample.aop.service.AccountService;
import com.abc.core.data.sample.domain.Account;
import com.abc.core.data.sample.domain.Order;

@Service
public class AccountServiceImpl implements AccountService {

	

	public void add(Account account) {
		System.out.println("add ");
		System.out.println(account);
	}

	public void add(Account account, Order order) {
		System.out.println("add ");
		System.out.println(account);
		System.out.println(order);
	}

	public boolean delete(Account account) {
		System.out.println("delete ");
		System.out.println(account);
		// TODO Auto-generated method stub
		return false;
	}

	public Order edit(Order order) {
		System.out.println("edit ");
		System.out.println(order);
		Order od=new Order();
		od.setItem("returnItem");
		od.setNumber("返回了30");
		return od;
	}

	public Account edit(Account account) {
		System.out.println("edit ");
		System.out.println(account);
		Account acc=new Account();
		acc.setAddress("北京");
		acc.setName("ABC");
		return acc;
	}

	public Account load(String name) {
		Account acc=new Account();
		acc.setAddress("北京");
		acc.setName("ABC");
		return acc;
	}

}

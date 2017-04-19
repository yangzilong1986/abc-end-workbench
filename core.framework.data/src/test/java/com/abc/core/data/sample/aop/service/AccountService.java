package com.abc.core.data.sample.aop.service;

import com.abc.core.data.sample.domain.Account;
import com.abc.core.data.sample.domain.Order;

public interface AccountService {
	void add(Account account);

	void add(Account account, Order order);

	public boolean delete(Account account);

	public Order edit(Order order);

	public Account edit(Account account);
	
	public Account load(String name);

}

package com.abc.core.data.sample.aop.service.impl;

import java.util.Collection;

import org.springframework.stereotype.Service;

import com.abc.core.data.sample.aop.aspect.Auditable;
import com.abc.core.data.sample.aop.service.Sample;
import com.abc.core.data.sample.domain.Account;

@Service
public class SampleImpl implements Sample<Account> {

	@Auditable
	public void sampleGenericMethod(Account param) {
		System.out.println(param);
		
	}

	public void sampleGenericCollectionMethod(Collection<Account> param) {
		// TODO Auto-generated method stub
		
	}

}

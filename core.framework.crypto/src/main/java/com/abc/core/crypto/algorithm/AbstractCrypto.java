package com.abc.core.crypto.algorithm;

import com.abc.core.crypto.CryptoException;

public abstract class AbstractCrypto implements Crypto{

	@Override
	public String encrypt(String plainText, String key) {
		throw new CryptoException("此方法没有实现");
	}

	@Override
	public String encrypt(String plainText) {
		throw new CryptoException("此方法没有实现");
	}

	@Override
	public String decrypt(String plainText, String key) {
		throw new CryptoException("此方法没有实现");
	}

	@Override
	public String decrypt(String plainText) {
		throw new CryptoException("此方法没有实现");
	}

	

}

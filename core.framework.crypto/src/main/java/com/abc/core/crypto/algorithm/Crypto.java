package com.abc.core.crypto.algorithm;

public interface Crypto {
	
	public String encrypt(String plainText, String key);
	public String encrypt(String plainText);
	public String decrypt(String plainText, String key);
	public String decrypt(String plainText);
	
}

package com.abc.core.crypto;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.abc.core.crypto.algorithm.AesBytesEncryptor;
import com.abc.core.crypto.algorithm.Crypto;

//import org.springframework.security.rsa.crypto.RsaAlgorithm;
import java.security.SecureRandom;

import org.junit.Test;
public class StandardTextEncoderTest {
	String plain="111--0000@";
	
	@Test
	public void encoderTest() {
		String random="salt";
		SecureRandom sr=new SecureRandom(random.getBytes());
		PasswordEncoder encoder=new BCryptPasswordEncoder(10,sr);
		String test=encoder.encode(plain);
		boolean is=encoder.matches(plain, test);
		System.out.println("is?"+is);
	}
	
//	@Test
//	public void rsa256(){
//		BytesEncryptor be=new RsaSecretEncryptor(RsaAlgorithm.DEFAULT,"salt-abc");
//		String test=be.encrypt(plain.getBytes()).toString();
//		System.out.println("test >"+test);
//		
//	}
	
	@Test
	public void aes256(){
		StringBuffer salt=new StringBuffer();
		salt.append("salt");
		char[] saltHex=Hex.encode(salt.toString().getBytes());
		Crypto be=new AesBytesEncryptor(plain, new String(saltHex));
		String test=be.encrypt("abc立即11111111111");
		System.out.println("test >"+test);
		
		String testDe=be.decrypt(test);
		System.out.println("test >"+testDe);
		
	}

}

package com.abc.core.crypto.algorithm;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import com.abc.core.crypto.AbcBase64;
import com.abc.core.crypto.CryptoException;

public class DESCrypto extends AbstractCrypto{

	  public String encrypt(String plainText, String key)  {
	        Cipher cipher = null;
	        String encryptStr = null;
	        try {
	            byte[] iv = {1, 2, 3, 4, 5, 6, 7, 8};
	            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
	            cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
	            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
	            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
	            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
	            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
	            byte[] encryptData = cipher.doFinal(plainText.getBytes());
	            encryptStr = AbcBase64.encodeBase64Byte2Str(encryptData);
	        } catch (Exception e) {
	        	throw new CryptoException("DES加密失败",e.getCause());
	        }
	        return encryptStr;

	    }

	  
	    public String decrypt(String plainText, String key) {
	        Cipher cipher = null;
	        String decryptStr = null;
	        try {
	            byte[] iv = {1, 2, 3, 4, 5, 6, 7, 8};
	            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
	            cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
	            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
	            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
	            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
	            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
	            byte[] encryptData = AbcBase64.decodeBase64(plainText);
	            byte[] decryptData = cipher.doFinal(encryptData);
	            decryptStr = new String(decryptData);
	        } catch (Exception e) {
	        	throw new CryptoException("DES解密失败",e.getCause());
	        }
	        return decryptStr;

	    }


}

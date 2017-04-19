package com.abc.core.crypto.algorithm;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;


import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import com.abc.core.crypto.AbcBase64;
import com.abc.core.crypto.CryptoException;

public class RCACrypto extends AbstractCrypto {

	private RSAPrivateKey privateKey;
	private RSAPublicKey publicKey;

	public RSAPublicKey getPublicKey() {
		return publicKey;
	}

	public RCACrypto(String privateKeyStr, String publicKeyStr) {
		try {
			this.setPrivateKey(privateKeyStr);
			this.setPublicKey(publicKeyStr);
		} catch (Exception e) {
			throw new RuntimeException("秘钥文件处理失败", e.getCause());
		}
	}

	public void setPublicKey(String publicKey) throws Exception {
	        try {
	            byte[] buffer = AbcBase64.decodeBase64(publicKey);
	            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
	            this.publicKey = (RSAPublicKey)keyFactory.generatePublic(keySpec);
	        }
	        catch (NoSuchAlgorithmException e) {
	            throw new Exception("无此加密算法");
	        } catch (InvalidKeySpecException e) {
	            e.printStackTrace();
	            throw new Exception("私钥非法");
	        } catch (IOException e) {
	            e.printStackTrace();
	            throw new Exception("私钥字符串读取出错");
	        }
	    }

	    public RSAPrivateKey getPrivateKey() {
	        return privateKey;
	    }

	    public void setPrivateKey(String privateKey) throws  Exception {
	        try {
	            byte[] buffer = AbcBase64.decodeBase64(privateKey);
	            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
	            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	            this.privateKey = (RSAPrivateKey)keyFactory.generatePrivate(keySpec);
	        }
	        catch (NoSuchAlgorithmException e) {
	            throw new Exception("无此加密算法",e.getCause());
	        } catch (InvalidKeySpecException e) {
	            //e.printStackTrace();
	            throw new Exception("私钥非法",e.getCause());
	        } catch (IOException e) {
	            //e.printStackTrace();
	            throw new Exception("私钥字符串读取出错",e.getCause());
	        }
	    }

		@Override
		public String encrypt(String data) {
	        if (this.publicKey == null) {
	            throw new CryptoException("请先设置公钥");
	        }
	        Cipher cipher = null;
	        String encryptStr = null;
	        try {
	            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	            cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
	            //byte[] textData = text.getBytes("utf-8");
	            byte[] encryptData = cipher.doFinal(data.getBytes("utf-8"));
	            //encryptStr =  new BASE64Encoder().encodeBuffer(encryptData);
	            encryptStr = AbcBase64.encodeBase64Byte2Str(encryptData);
	        } catch (NoSuchAlgorithmException e) {
	        	 throw new CryptoException("无此加密算法",e.getCause());
	        } catch (NoSuchPaddingException e) {
	            //e.printStackTrace();
	        	throw new CryptoException("PKCS1Padding非法",e.getCause());
	        } catch (InvalidKeyException e) {
	        	throw new CryptoException("私钥非法",e.getCause());
	        }catch(Exception e){
	        	throw new CryptoException("加密失败",e.getCause());
	        }
	        return encryptStr;
	    }
		
		@Override
	    public String decrypt(String text) {
	        if (this.privateKey == null) {
	            throw new CryptoException("请先设置私钥");
	        }
	        Cipher cipher = null;
	        String decryptStr = null;
	        try {
	            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	            cipher.init(Cipher.DECRYPT_MODE, this.privateKey);
	            //byte[] encryptData = new BASE64Decoder().decodeBuffer(text);
	            byte[] encryptData = AbcBase64.decodeBase64(text);
	            byte[] decryptData = cipher.doFinal(encryptData);
	            decryptStr =  new String(decryptData);
	        } catch (NoSuchAlgorithmException e) {
	       	 	throw new CryptoException("无此解密算法",e.getCause());
	       } catch (NoSuchPaddingException e) {
	    	   throw new CryptoException("PKCS1Padding非法",e.getCause());
	       } catch (InvalidKeyException e) {
	       		throw new CryptoException("私钥非法",e.getCause());
	       }catch (Exception e) {
	       		throw new CryptoException("解密失败",e.getCause());
	       }
	        return decryptStr;

	    }

	  
}

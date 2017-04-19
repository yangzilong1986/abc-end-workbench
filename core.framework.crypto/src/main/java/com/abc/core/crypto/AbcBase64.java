package com.abc.core.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;

/**
 * Base64转换器，提供Base64转换的基本操作
 *
 */
public class AbcBase64 {

	
	/**
	 * Encodes binary data using the base64 algorithm but does not chunk the output.
	 * @param binaryData
	 * @return
	 */
	public static byte[] encodeBase64(byte[] binaryData) throws Exception{
		return Base64.encodeBase64(binaryData);
	}
	
	/**
	 * Encodes string data using the base64 algorithm but does not chunk the output.
	 * @param binaryData
	 * @return
	 */
	public static byte[] encodeBase64(String strData) throws Exception{
		return Base64.encodeBase64(strData.getBytes("utf-8"));
	}
	
	/**
	 * 将二进制数组转为base64字符串
	 * @param binaryData
	 * @return
	 * @throws Exception
	 */
	public static String encodeBase64Byte2Str(byte[] binaryData) throws Exception{
		byte[] b = Base64.encodeBase64(binaryData);
		return new String(b, "utf-8");
	}
	
	/**
	 * 将数据字符串转换为Base64字符串 
	 * @param strData
	 * @return
	 * @throws Exception
	 */
	public static String encodeBase64Str2Str(String strData) throws Exception{
		byte[] b = Base64.encodeBase64(strData.getBytes("utf-8"));
		return new String(b, "utf-8");
	}
	
	/**
	 * Decodes a Base64 String into octets.
	 * 
	 * @param base64String
	 * @return
	 */
	public static byte[] decodeBase64(String base64String) throws Exception{
		return Base64.decodeBase64(base64String);
	}
	
	/**
	 * 将base64字符串转换为数据字符串
	 * @param base64Str
	 * @return
	 */
	public static String decodeBase64Str2Str(String base64String) throws Exception{
		byte[] b = Base64.decodeBase64(base64String);
		return new String(b, "utf-8");
	}
	
	public static void main(String[] args) throws Exception {
		File file = new File("G:\\icon-01-30.jpg");
		InputStream is = new FileInputStream(file);
		byte[] b = new byte[(int)file.length()];
		is.read(b);
		String str = encodeBase64Byte2Str(b);
		System.out.println(str);
	}
	
}

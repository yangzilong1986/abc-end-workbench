package com.abc.core.crypto.algorithm;
import static org.springframework.security.crypto.util.EncodingUtils.concatenate;
import static org.springframework.security.crypto.util.EncodingUtils.subArray;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;

import com.abc.core.crypto.AbcBase64;
import com.abc.core.crypto.CryptoException;


public class AesBytesEncryptor extends AbstractCrypto{

	private final SecretKey secretKey;

	private final Cipher encryptor;

	private final Cipher decryptor;

	private final BytesKeyGenerator ivGenerator;

	private CipherAlgorithm alg;

	private static final String AES_CBC_ALGORITHM = "AES/CBC/PKCS5Padding";

	private static final String AES_GCM_ALGORITHM = "AES/GCM/NoPadding";

	public enum CipherAlgorithm {

		CBC(AES_CBC_ALGORITHM, NULL_IV_GENERATOR), GCM(AES_GCM_ALGORITHM, KeyGenerators
				.secureRandom(16));

		private BytesKeyGenerator ivGenerator;
		private String name;

		private CipherAlgorithm(String name, BytesKeyGenerator ivGenerator) {
			this.name = name;
			this.ivGenerator = ivGenerator;
		}

		@Override
		public String toString() {
			return this.name;
		}

		public AlgorithmParameterSpec getParameterSpec(byte[] iv) {
			return this == CBC ? new IvParameterSpec(iv) : new GCMParameterSpec(128, iv);
		}

		public Cipher createCipher() {
			return CipherUtils.newCipher(this.toString());
		}

		public BytesKeyGenerator defaultIvGenerator() {
			return this.ivGenerator;
		}
	}

	public AesBytesEncryptor(String password, CharSequence salt) {
		this(password, salt, null);
	}

	public AesBytesEncryptor(String password, CharSequence salt,
			BytesKeyGenerator ivGenerator) {
		this(password, salt, ivGenerator, CipherAlgorithm.CBC);
	}

	public AesBytesEncryptor(String password, CharSequence salt,
			BytesKeyGenerator ivGenerator, CipherAlgorithm alg) {
		//256有限制
		PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), Hex.decode(salt),
				1024, 128);
		SecretKey secretKey = CipherUtils.newSecretKey("PBKDF2WithHmacSHA1", keySpec);
		this.secretKey = new SecretKeySpec(secretKey.getEncoded(), "AES");
		this.alg = alg;
		this.encryptor = alg.createCipher();
		this.decryptor = alg.createCipher();
		this.ivGenerator = ivGenerator != null ? ivGenerator : alg.defaultIvGenerator();
	}
	
	private static boolean isHex(String input) {
		try {
			Hex.decode(input);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	@Override
	public String encrypt(String plainText) {
		byte[] bytes=plainText.getBytes();
		synchronized (this.encryptor) {
			byte[] iv = this.ivGenerator.generateKey();
			CipherUtils.initCipher(this.encryptor, Cipher.ENCRYPT_MODE, this.secretKey,
					this.alg.getParameterSpec(iv));
			byte[] encrypted = CipherUtils.doFinal(this.encryptor, bytes);
			byte[] conEncrypted= this.ivGenerator != NULL_IV_GENERATOR ? concatenate(iv, encrypted)
					: encrypted;
			try{
				return AbcBase64.encodeBase64Byte2Str(conEncrypted);
			}catch(Exception e){
				throw new CryptoException("AES加密失败",e.getCause());
			}
		}
	}

	@Override
	public String decrypt(String plainText) {

		
		synchronized (this.decryptor) {
			byte[] encryptedBytes;
			try {
				encryptedBytes = AbcBase64.decodeBase64(plainText);
			} catch (Exception e) {
				throw new CryptoException("AES解密失败",e.getCause());
			}
			byte[] iv = iv(encryptedBytes);
			CipherUtils.initCipher(this.decryptor, Cipher.DECRYPT_MODE, this.secretKey,
					this.alg.getParameterSpec(iv));
			byte[] bytes= CipherUtils.doFinal(
					this.decryptor,
					this.ivGenerator != NULL_IV_GENERATOR ? encrypted(encryptedBytes,
							iv.length) : encryptedBytes);
			return new String(bytes);
		}
	}

	// internal helpers

	private byte[] iv(byte[] encrypted) {
		return this.ivGenerator != NULL_IV_GENERATOR ? subArray(encrypted, 0,
				this.ivGenerator.getKeyLength()) : NULL_IV_GENERATOR.generateKey();
	}

	private byte[] encrypted(byte[] encryptedBytes, int ivLength) {
		return subArray(encryptedBytes, ivLength, encryptedBytes.length);
	}

	private static final BytesKeyGenerator NULL_IV_GENERATOR = new BytesKeyGenerator() {

		private final byte[] VALUE = new byte[16];

		@Override
		public int getKeyLength() {
			return this.VALUE.length;
		}

		@Override
		public byte[] generateKey() {
			return this.VALUE;
		}

	};

}

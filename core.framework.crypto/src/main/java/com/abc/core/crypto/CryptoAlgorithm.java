package com.abc.core.crypto;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.abc.core.crypto.algorithm.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoAlgorithm {
	private final static Logger logger = LoggerFactory.getLogger(CryptoAlgorithm.class);

	public static enum CryptoType {
		DES, RSA, ASE
	}

	private String rsa_public_key;
	private String rsa_private_key;

	public void setKeyFile(String publicKeyfileName, String privateKeyfileName) {
		this.rsa_public_key = publicKeyfileName;
		this.rsa_private_key = privateKeyfileName;
	}

	RCACrypto instanceRCACrypto() {
		String pubKeyStr = getKeyFromFile(rsa_public_key);

		String privateKeyStr = getKeyFromFile(rsa_private_key);

		return new RCACrypto(privateKeyStr, pubKeyStr);

	}

	public Crypto getEncrypto(CryptoType type) {
		Crypto crypto = null;
		switch (type) {
		case DES:
			crypto = new DESCrypto();
			break;
		case RSA:
			crypto = instanceRCACrypto();
			break;
		case ASE:
			break;
		default:
			crypto = new DESCrypto();
		}
		return crypto;
	}

	private String getKeyFromFile(String filePath) {
		String key = null;
		BufferedReader bufferedReader = null;
		try {
			// URL url = this.getClass().getResource(filePath);
			bufferedReader = new BufferedReader(new FileReader(filePath));
			List<String> list = new ArrayList<String>();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				list.add(line);
			}
			StringBuilder stringBuilder = new StringBuilder();
			// 去掉第一行和最后一行
			for (int i = 1; i < list.size() - 1; i++) {
				stringBuilder.append(list.get(i)).append("\r");
			}
			key = stringBuilder.toString();
		} catch (FileNotFoundException e) {
			logger.error("{}", e);
		} catch (IOException e) {
			logger.error("{}", e);
		} finally {
			if (null != bufferedReader) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return key;
	}

}

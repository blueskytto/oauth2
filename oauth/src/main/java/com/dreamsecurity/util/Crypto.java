package com.dreamsecurity.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;

@Component
public class Crypto {

	public byte[] digest(String msg, String algorithm) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digester = MessageDigest.getInstance(algorithm);
		digester.update(msg.getBytes("UTF-8"));

		return digester.digest();
	}

	public String bytesToHex(byte[] hashInBytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < hashInBytes.length; i++) {
			String hex = Integer.toHexString(0xff & hashInBytes[i]);
			if (hex.length() == 1)
				sb.append('0');
			sb.append(hex);
		}
		return sb.toString();
	}
}

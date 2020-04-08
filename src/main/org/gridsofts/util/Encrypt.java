/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 字符串加密类，实现了一些常用的加密方式。
 * 
 * @author zholey
 * @version 2.0
 */
public class Encrypt implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 使用AES算法对数据进行加密运算
	 * 
	 * @param key
	 * @param data
	 * @param charsetName
	 * @return
	 */
	public static String aesEncode(String key, String data, String charsetName) {

		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(key.getBytes(charsetName)));
			SecretKey secretKey = kgen.generateKey();

			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey.getEncoded(), "AES"));

			return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(charsetName)));
		} catch (Throwable ex) {
		}

		return null;
	}

	/**
	 * 使用AES算法对数据进行解密运算
	 * 
	 * @param key
	 * @param encryptedData
	 * @param charsetName
	 * @return
	 */
	public static String aesDecode(String key, String encryptedData, String charsetName) {

		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(key.getBytes(charsetName)));
			SecretKey secretKey = kgen.generateKey();

			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey.getEncoded(), "AES"));

			return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)), charsetName);
		} catch (Throwable ex) {
		}

		return null;
	}

	/**
	 * 使用DES算法对数据进行加密运算
	 * 
	 * @param key
	 * @param data
	 * @param charsetName
	 * @return
	 */
	public static String desEncode(String key, String data, String charsetName) {

		try {
			DESKeySpec desKey = new DESKeySpec(key.getBytes(charsetName));
			SecretKeyFactory secKeyFactory = SecretKeyFactory.getInstance("DES");
			Key secKey = secKeyFactory.generateSecret(desKey);

			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, secKey, new SecureRandom());

			return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(charsetName)));
		} catch (Throwable ex) {
		}

		return null;
	}

	/**
	 * 使用DES算法对数据进行解密运算
	 * 
	 * @param key
	 * @param encryptedData
	 * @param charsetName
	 * @return
	 */
	public static String desDecode(String key, String encryptedData, String charsetName) {

		try {
			DESKeySpec desKey = new DESKeySpec(key.getBytes(charsetName));
			SecretKeyFactory secKeyFactory = SecretKeyFactory.getInstance("DES");
			Key secKey = secKeyFactory.generateSecret(desKey);

			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, secKey, new SecureRandom());

			return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)), charsetName);
		} catch (Throwable ex) {
		}

		return null;
	}

	/**
	 * 将指定字符串进行SHA1加密后返回。
	 * 
	 * @param source
	 *            指定的需要加密的字符串
	 * @return 加密后的字符串
	 */
	public static String sha1(String source) {
		return digest("SHA1", source, "UTF-8", "");
	}

	/**
	 * 将指定文件进行SHA1加密后返回。
	 * 
	 * @param source
	 *            指定的需要加密的文件内容
	 * @return 加密后的字符串
	 */
	public static String sha1(byte[] bytes) {
		return digest("SHA1", bytes);
	}

	/**
	 * 将指定字符串进行SHA1加密后返回。
	 * 
	 * @param source
	 *            指定的需要加密的字符串
	 * @param key
	 * @return 加密后的字符串
	 */
	public static String sha1(String source, String key) {
		return digest("SHA1", source, "UTF-8", key);
	}

	/**
	 * 将指定字符串进行SHA1加密后返回。
	 * 
	 * @param source
	 *            指定的需要加密的字符串
	 * @param charsetName
	 * @param key
	 * @return 加密后的字符串
	 */
	public static String sha1(String source, String charsetName, String key) {
		return digest("SHA1", source, charsetName, key);
	}

	/**
	 * 将指定字符串进行MD5加密后返回。
	 * 
	 * @param str
	 *            指定的需要加密的字符串
	 * @return 加密后的字符串
	 */
	public static String md5(String source) {
		return digest("MD5", source, "UTF-8", "");
	}
	
	/**
	 * 将指定文件进行MD5加密后返回。
	 * 
	 * @param bytes
	 *            指定的需要加密的文件内容
	 * @return 加密后的字符串
	 */
	public static String md5(byte[] bytes) {
		return digest("MD5", bytes);
	}

	/**
	 * 将指定字符串进行MD5加密后返回。
	 * 
	 * @param str
	 *            指定的需要加密的字符串
	 * @param key
	 * @return 加密后的字符串
	 */
	public static String md5(String source, String key) {
		return digest("MD5", source, "UTF-8", key);
	}

	/**
	 * 将指定字符串进行MD5加密后返回。
	 * 
	 * @param str
	 *            指定的需要加密的字符串
	 * @param charsetName
	 * @param key
	 * @return 加密后的字符串
	 */
	public static String md5(String source, String charsetName, String key) {
		return digest("MD5", source, charsetName, key);
	}

	/**
	 * 对给定的数据进行摘要计算
	 * 
	 * @param algorithm
	 * @param source
	 * @param charsetName
	 * @param key
	 * @return
	 */
	private static String digest(String algorithm, String source, String charsetName, String key) {

		if (source != null) {
			try {
				return digest(algorithm, (source + key).getBytes(charsetName));
			} catch (UnsupportedEncodingException e) {
			}
		}

		return null;
	}
	
	/**
	 * 对给定的数据进行摘要计算
	 * 
	 * @param algorithm
	 * @param sourceBytes
	 * @return
	 */
	private static String digest(String algorithm, byte[] bytes) {

		if (bytes != null) {
			try {
				MessageDigest md = MessageDigest.getInstance(algorithm);
				return toHexString(md.digest(bytes));

			} catch (NoSuchAlgorithmException e) {
			}
		}

		return null;
	}

	/**
	 * 将数值转换为16进制字符。
	 * 
	 * @param hash
	 *            待转换的数值数组
	 * @return 16进制字符串
	 */
	private static String toHexString(byte[] hash) {
		StringBuffer hexString = new StringBuffer();

		for (int i = 0; i < hash.length; i++) {
			if ((0xFF & hash[i]) < 0x10) {
				hexString.append("0" + Integer.toHexString((0xFF & hash[i])));
			} else {
				hexString.append(Integer.toHexString(0xFF & hash[i]));
			}
		}

		return hexString.toString();
	}
}

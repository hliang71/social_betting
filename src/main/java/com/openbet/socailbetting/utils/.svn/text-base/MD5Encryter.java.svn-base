package com.openbet.socailbetting.utils;

import java.math.BigInteger;
import java.security.MessageDigest;

import org.apache.log4j.Logger;
import org.springframework.util.DigestUtils;

public class MD5Encryter {
	private static final Logger log = Logger.getLogger(MD5Encryter.class);

	public static String encryptToHex32(String plaintext)
	{
		String hashtext = null;
		try
		{
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(plaintext.getBytes());
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1,digest);
			hashtext = bigInt.toString(16);
			while(hashtext.length() < 32 ){
			  hashtext = "0"+hashtext;
			}
		}
		catch(Exception e)
		{
			log.error("failed to encrypt the text", e);
		}
		return hashtext;
	}
	
	public static String encryptToHex(String plaintext)
	{
		byte[] bytes = plaintext.getBytes();
		String result = DigestUtils.md5DigestAsHex(bytes);
		return result;
	}
}

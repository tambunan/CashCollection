package com.teravin.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.teravin.util.Util;

public class Crypto {
	// TODO : get key from keystore
	public SecretKey getTMK(){
		SecretKey key = new SecretKeySpec(Util.hexStringToByteArray("B3D0012AD54ABC25"),"DES");
		
		return key;
	}
	
	public byte[] encrypt(byte[] clear) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey key = keyFactory.generateSecret((KeySpec) getTMK());

		Cipher cipher = Cipher.getInstance("DES"); // cipher is not thread safe
		cipher.init(Cipher.ENCRYPT_MODE, key);
		
		return cipher.doFinal(clear);
	}
	
	public byte[] encrypt(byte[] clear, byte[] encKey) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		DESKeySpec keySpec = new DESKeySpec(encKey);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey key = keyFactory.generateSecret(keySpec);

		Cipher cipher = Cipher.getInstance("DES"); // cipher is not thread safe
		cipher.init(Cipher.ENCRYPT_MODE, key);
		
		return cipher.doFinal(clear);
	}
	
}

package fr.virgile62150.sxm_downloader.java.API;

import java.io.FileNotFoundException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
	
	static Crypto instance = null;
	
    private static final String cipherTransformation = "AES/CBC/PKCS5Padding";
    private static final String aesEncryptionAlgorithm = "AES";
	
	
	private Crypto() {
		
	}
	
	public static Crypto getInstance() {
		if (instance == null)
			instance = new Crypto();
		return instance;
	}
	
	public byte[] Decrypt(byte[] src, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, FileNotFoundException  {
		return decryptWithManagedIV(src, key);
	}
	
	
	private byte[] decryptWithManagedIV(byte[] cipherText, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, FileNotFoundException {
	        byte[] initialVector = Arrays.copyOfRange(cipherText,0,16);
	        byte[] trimmedCipherText = Arrays.copyOfRange(cipherText,16,cipherText.length); 
	        return decrypt(trimmedCipherText, key, initialVector);
	    }

    private byte[] decrypt(byte[] cipherText, byte[] key, byte[] initialVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, FileNotFoundException{
        Cipher cipher = Cipher.getInstance(cipherTransformation);
        SecretKeySpec secretKeySpecy = new SecretKeySpec(key, aesEncryptionAlgorithm);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
        cipherText = cipher.doFinal(cipherText);
        
        return cipherText;
    }
	
	
}

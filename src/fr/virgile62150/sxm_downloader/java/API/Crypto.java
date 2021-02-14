package fr.virgile62150.sxm_downloader.java.API;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    private static final String KEY = "ZZHHYYTTUUHHGGRR";
    private static final String IV = "";

    public static void main(String[] args) {
        try {
            test_encrypt_decrypt();
        } catch (Exception e) {
        	e.printStackTrace();
            //System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void test_encrypt_decrypt() throws Exception {
        // encrypt "in.txt" -> "out.txt"
    	//test_write_to_file("in.txt");
       /* String s = readFile("data/Lie_aes128.aac");
        String res = encrypt("mykey", IV, s);
        PrintWriter writer = new PrintWriter("out.txt", "UTF-8");
        writer.print(res);
        writer.close();
        */
    
        // decrypt "out.txt" -> "out2.txt"
        byte[] s = readFile("data/Lie_aes128.aac");
        System.out.println(s);
        String res = decrypt("mykey", IV, s);
        PrintWriter writer = new PrintWriter("data/out2.aac", "UTF-8");
        writer.print(res);
        writer.close();
    }

    public static String encrypt(String key, String iv, String msg) throws Exception {
        byte[] bytesOfKey = key.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] keyBytes = md.digest(bytesOfKey);

        //final byte[] ivBytes = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        final byte[] resultBytes = cipher.doFinal(msg.getBytes());
        return Base64.getMimeEncoder().encodeToString(resultBytes);
    }

    public static void test_decrypt() throws Exception {
        byte[] s = readFile("in.txt");
        String res = decrypt(KEY, IV, s);
        System.out.println("res:\n" + res);
    }
    
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String decrypt(String key, String iv, byte[] encrypted) throws Exception {
        byte[] bytesOfKey = {(byte) 0x83,(byte) 0xab,0x52,0x44,(byte) 0xa8,(byte) 0xdb,(byte) 0xA2,(byte) 0x95,0x73,0x2F,(byte) 0xca,0x4B,0x58,(byte) 0xec,(byte) 0xe2,(byte) 0xf5};
        System.out.println(bytesOfKey.length);
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] keyBytes = bytesOfKey;

        final byte[] ivBytes = iv.getBytes();

        final byte[] encryptedBytes = encrypted;

        byte[] iv1 = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
        
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec,new IvParameterSpec(iv1));

        final byte[] resultBytes = cipher.doFinal(encryptedBytes);
        return new String(resultBytes);
    }

    public static byte[] readFile(String filename) throws Exception {
        /*BufferedReader br = new BufferedReader(new FileReader(filename));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            return everything;
        } finally {
            br.close();
        }*/
    	
    	InputStream is = new FileInputStream(new File(filename));
    	ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    	
    	int nRead;
    	byte[] data = new byte[70000000];

    	while ((nRead = is.read(data, 0, data.length)) != -1) {
    	  buffer.write(data, 0, nRead);
    	}

    	return buffer.toByteArray();
        
        //return "";
    }

    public static void test_read_file() throws Exception {
        byte[] s = readFile("in.txt");
        System.out.println("in.txt:\n" + s);
    }

    public static void encrypt(String key, File inputFile, File outputFile) throws Exception {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }

    public static void decrypt(String key, File inputFile, File outputFile) throws Exception {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }

    static void doCrypto(int cipherMode, String key, File inputFile,
                          File outputFile) throws Exception {
        Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(cipherMode, secretKey);

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(outputBytes);

        inputStream.close();
        outputStream.close();
    }

    public static void test_write_to_file(String filename) throws Exception {
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        writer.println("The first line");
        writer.println("The second line");
        writer.close();
    }
}

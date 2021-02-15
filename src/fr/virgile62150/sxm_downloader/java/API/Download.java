package fr.virgile62150.sxm_downloader.java.API;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import fr.virgile62150.sxm_downloader.java.obj.Music;

public class Download {
	private String aes_key = "";
	private byte[] aes_key_bytes;
	private ArrayList<String> urls = new ArrayList<>();
	private Music mus = null;
	private String api_url;
	private String temp_filename;
	
	private ArrayList<byte[]> files = new ArrayList<>();
	
	public Download(String aes_key, ArrayList<String> urls, Music m, String api_url) {
		this.aes_key = aes_key;
		this.urls = urls;
		mus = m;
		this.api_url = api_url;
	}
	
	public Download(String aes_key, ArrayList<String> urls, Music m, String api_url, byte[] aes_key_bytes) {
		this.aes_key = aes_key;
		this.aes_key_bytes = aes_key_bytes;
		this.urls = urls;
		mus = m;
		this.api_url = api_url;
	}
	
	public String download_file() throws MalformedURLException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		CheckInstall();
		String filename = mus.getArtist()+" - "+mus.getTitle()+"_aes128.aac";
		filename = filename.replace("/","");
		temp_filename = filename;
		
		int current = 1;
		int totalbyte_rx = 0;
		// création du fichier temporaire
		OutputStream os = new FileOutputStream("data/"+filename);
		
		for(String part : urls) {
			System.out.println("Downloading "+current+" / "+urls.size());
			
			byte[] buffer = new byte[8192];
			int len;
			InputStream data_from_url = HTTP_Request.binaryRequest2(api_url+part, API.getInstance().cookieString());
			while ((len = data_from_url.read(buffer)) > 0) {
		        os.write(buffer, 0, len);
		        totalbyte_rx+= len;
			}
			
			
			System.out.println("File "+current+" : OK");
			current++;
		}
		os.close();
		System.out.println("Total data recieved : "+totalbyte_rx+" bytes => "+totalbyte_rx/8+" Bytes");
		System.out.println("AES Key: "+aes_key);
		
		return DecryptFile(filename);
	}
	
	private String DecryptFile(String filename) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		// Ouverture du fichier créé
		byte[] file = Files.readAllBytes(new File("data/"+filename).toPath());
		
		byte[] returned = Crypto.getInstance().Decrypt(file, aes_key_bytes);
		
		OutputStream os = new FileOutputStream("data/"+filename.replace("_aes128", ""));
		os.write(returned);
		os.close();
		
		
		return filename;
	}
	
	public boolean CleanTempFile() {
		File temp = new File("data/"+temp_filename);
		if (temp.exists()) {
			return temp.delete();
		} else {
			return false;
		}
	}
	
	private void CheckInstall() throws FileNotFoundException {
		File f = new File("data");
		if (f.exists()) {
			if(f.isDirectory()) {
				return;
			} else {
				throw new FileNotFoundException();
			}
		} else {
			f.mkdir();
		}
	}
}

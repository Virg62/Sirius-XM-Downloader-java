package fr.virgile62150.sxm_downloader.java.API;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

import fr.virgile62150.sxm_downloader.java.obj.Music;

public class Download {
	private String aes_key = "";
	private ArrayList<String> urls = new ArrayList<>();
	private Music mus = null;
	private String api_url;
	
	private ArrayList<byte[]> files = new ArrayList<>();
	
	public Download(String aes_key, ArrayList<String> urls, Music m, String api_url) {
		this.aes_key = aes_key;
		this.urls = urls;
		mus = m;
		this.api_url = api_url;
	}
	
	public String download_file() throws MalformedURLException, IOException {
		CheckInstall();
		String filename = mus.getArtist()+" - "+mus.getTitle()+"_aes128.aac";
		filename = filename.replace("/","");
		int current = 1;
		int totalbyte_rx = 0;
		// création du fichier temporaire
		OutputStream os = new FileOutputStream("data/"+filename);
		
		for(String part : urls) {
			System.out.println("Downloading "+current+" / "+urls.size()+" : "+api_url+part);
			
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
		return filename;
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

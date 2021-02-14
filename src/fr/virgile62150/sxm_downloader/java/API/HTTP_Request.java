package fr.virgile62150.sxm_downloader.java.API;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class HTTP_Request {
	public static HashMap<String, String> Request(String url, String cookies, boolean POST, String posted) throws MalformedURLException, IOException {
		HashMap<String, String> returned = new HashMap<>();
		
		URL cur_url = new URL(url);
		HttpURLConnection con = (HttpURLConnection) cur_url.openConnection();
		con.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36");
		if (POST) {
			con.setRequestMethod("POST");
			con.setRequestProperty("accept", "application/json, text/plain, */*");
			con.setRequestProperty("Content-Type", "application/json");
			con.setDoOutput(true);
			DataOutputStream dos = new DataOutputStream(con.getOutputStream());
			dos.writeBytes(posted);
			dos.close();
		} else {
			con.setRequestMethod("GET");
		}
		if (cookies.length() != 0) {
			con.setRequestProperty("Cookie", cookies);
		}
		
		con.connect();
		
		for (Entry<String, List<String>> header : con.getHeaderFields().entrySet()) {
		     //System.out.println(header.getKey() + "=" + header.getValue());
		     if (header.getKey() != null && header.getKey().equals("Set-Cookie")) {
		    	 //System.out.println(header.getValue());
		    	 for (String cookie : header.getValue()) {
		    		 if(cookie.contains("JSESSIONID")) {
		    			 returned.put("JSESSIONID", cookie);
		    			 System.out.println("jsessid found");
		    		 } else if (cookie.contains("__tracks__")) {
		    			 returned.put("__tracks__", cookie);
		    			 System.out.println("tracks found");
		    		 } else if (cookie.contains("SXM-DATA")) {
		    			 returned.put("SXM-DATA", cookie);
		    			 System.out.println("sxm-data found");
		    		 }
		    	 }
		     }
		 }
		
		
		
		InputStream input = con.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String line;
		StringBuilder answer = new StringBuilder("");
		while ((line = reader.readLine()) != null ) {
			answer.append(line);
		}
		returned.put("HTTP", answer.toString());
		reader.close(); input.close(); con.disconnect();
		
		return returned;
	}
}

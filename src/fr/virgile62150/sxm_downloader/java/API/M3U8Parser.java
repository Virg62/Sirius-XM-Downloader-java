package fr.virgile62150.sxm_downloader.java.API;

import java.util.ArrayList;

public class M3U8Parser {
	
	private static M3U8Parser instance = null;
	
	private final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	
	private M3U8Parser() {
		
	}
	
	public static M3U8Parser getInstance() {
		if (instance == null)
			instance = new M3U8Parser();
		return instance;
	}
	
	public ArrayList<String> master_parse(String m3u8) {
		ArrayList<String> fluxes = new ArrayList<>();
		String[] lines = m3u8.split("\n");
		
		for (String line : lines) {
			if (!line.startsWith("#") && !line.equals("") && !line.equals(" ")) {
				fluxes.add(line);
			}
		}
		
		return fluxes;
	}
	
	
	public String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars);
	}
}

package fr.virgile62150.sxm_downloader.java.API;

public class API {
	
	private StringBuilder cookies = new StringBuilder("");
	
	private static API instance;
	
	private API() {
		
	}
	
	public static API getInstance() {
		if (instance == null)
			instance = new API();
		return instance;
	}
	
	public void init() {
		
	}
	
	
}

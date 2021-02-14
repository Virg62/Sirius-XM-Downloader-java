package fr.virgile62150.sxm_downloader.java;

import java.io.IOException;

import fr.virgile62150.sxm_downloader.java.API.API;

public class Main {
	public static void main(String[] args) {
		try {
			API.getInstance().init();
			API.getInstance().getChannels();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

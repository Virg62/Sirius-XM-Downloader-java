package fr.virgile62150.sxm_downloader.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import fr.virgile62150.sxm_downloader.java.API.API;
import fr.virgile62150.sxm_downloader.java.obj.Music;
import fr.virgile62150.sxm_downloader.java.obj.Radio;

public class ConsoleMode {
	public ConsoleMode() {
		try {
			System.out.println("Initialisation en cours ... Veuillez patienter ...");
			API.getInstance().init();
			System.out.println("Récupération de la liste des radios...");
			
			ArrayList<Radio> radio_list = API.getInstance().getChannels();
			
			for (Radio r : radio_list) {
				System.out.println(r.getId()+" : "+r.getName());
			}
			
			
			
			Radio chosen = null;
			String chosen_one = "";
			boolean found = false;
			
			// OVERRIDE
			//found = true;
			//chosen = radio_list.get(0);
			
			
			
			while (!found) {
				System.out.println("Entrez l'identifiant de votre radio");
				chosen_one = getConsoleInput();
				for (Radio r : radio_list) {
					if (r.getId().equals(chosen_one)) {
						found = true;
						chosen = r;
					}
				}
			}
			
			System.out.println("Radio Choisie : "+chosen.getName());
			
			ArrayList<Music> music_list = API.getInstance().getTrackList(chosen.getId());
			
			for (Music m : music_list) {
				System.out.println(m.getUUID()+" : "+m.getArtist()+" - "+m.getTitle());
			}
			
			found = false;
			Music chosen_m = null;
			
			// OVERRIDE
			//found = true;
			//chosen_m = music_list.get(0);
			
			while (!found) {
				System.out.println("Entrez l'identifiant de la musique");
				chosen_one = getConsoleInput();
				for (Music m : music_list) {
					if (m.getUUID().equals(chosen_one)) {
						found = true;
						chosen_m = m;
					}
				}
			}
			
			System.out.println("Musique choisie : "+chosen_m.getArtist()+" - "+chosen_m.getTitle());
			
			API.getInstance().getSegmentList(chosen_m);
			API.getInstance().getFile();
			
		} catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
	}
	
	private String getConsoleInput() throws IOException {
		BufferedReader reader = new BufferedReader( 
	    new InputStreamReader(System.in)); 
		return reader.readLine();
	}
}

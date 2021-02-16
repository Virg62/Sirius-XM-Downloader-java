package fr.virgile62150.sxm_downloader.java.API;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

import fr.virgile62150.sxm_downloader.java.jwt.Frame;
import fr.virgile62150.sxm_downloader.java.obj.Music;
import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.InputFormatException;

public class Download {
	private String aes_key = "";
	private byte[] aes_key_bytes;
	private ArrayList<String> urls = new ArrayList<>();
	private Music mus = null;
	private String api_url;
	private String temp_filename;
	private File path = new File("data/");
	private ArrayList<byte[]> crypted_parts = new ArrayList<>();
	private ArrayList<byte[]> decrypted_parts = new ArrayList<>();
	
	
	//private ArrayList<byte[]> files = new ArrayList<>();
	
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
		filename = filename.replace("/"," ");
		filename = filename.replace("?","");
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
	
	public String download_file_swing() throws MalformedURLException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, CannotReadException, TagException, ReadOnlyFileException, InvalidAudioFrameException, CannotWriteException {
		CheckInstall();
		String filename = mus.getArtist()+" - "+mus.getTitle()+"_aes128.aac";
		filename = filename.replace("/","");
		temp_filename = filename;
		
		int current = 1;
		int totalbyte_rx = 0;
		// création du fichier temporaire
		OutputStream os = new FileOutputStream("data/"+filename);
		
		for(String part : urls) {
			// ON AFFICHE SUR LE CLIENT
			//System.out.println("Downloading "+current+" / "+urls.size());
			float percent = (float)((float)current/(float)urls.size());
			
			
			
			byte[] buffer = new byte[8192];
			int len;
			InputStream data_from_url = HTTP_Request.binaryRequest2(api_url+part, API.getInstance().cookieString());
			while ((len = data_from_url.read(buffer)) > 0) {
		        os.write(buffer, 0, len);
		        totalbyte_rx+= len;
			}
			
			
			System.out.println("File "+current+" : OK");
			Frame.getInstance().getPanel().setPBPercentage(percent);
			current++;
		}
		os.close();
		System.out.println("Total data recieved : "+totalbyte_rx+" Bytes => "+(float)totalbyte_rx/(float)1000+" kBytes");
		System.out.println("AES Key: "+aes_key);
		
		String fn = DecryptFile(filename);
		TagFile(fn);
		return fn;
	}
	
	
	public String download_file_swing_2() throws MalformedURLException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, CannotReadException, TagException, ReadOnlyFileException, InvalidAudioFrameException, CannotWriteException, IllegalArgumentException, InputFormatException, EncoderException, UnsupportedAudioFileException {
		CheckInstall();
		System.out.println("Going to download : "+mus);
		String filename = mus.getArtist()+" - "+mus.getTitle()+"_aes128.m4a";
		filename = filename.replace("/","");
		temp_filename = filename;
		
		int current = 1;
		int totalbyte_rx = 0;
		// création du fichier temporaire
		//OutputStream os = new FileOutputStream("data/"+filename);
		
		for(String part : urls) {
			// ON AFFICHE SUR LE CLIENT
			//System.out.println("Downloading "+current+" / "+urls.size());
			float percent = (float)((float)current/(float)urls.size());
			
			byte[] buffer = new byte[8192];
			int len;
			InputStream data_from_url = HTTP_Request.binaryRequest2(api_url+part, API.getInstance().cookieString());
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			data_from_url.transferTo(baos);
			crypted_parts.add(baos.toByteArray());
			baos.close();
						
			System.out.println("File "+current+" : OK");
			if (percent == 1) {
				Frame.getInstance().getPanel().setPBPercentage((float) 0.99);
			} else {
				Frame.getInstance().getPanel().setPBPercentage(percent);
			}
			
			current++;
		}
		Frame.getInstance().getPanel().setBarIndeterminate(true);
		Frame.getInstance().getPanel().setStatusBarMsg("Déchiffrage des données du titre...");
		//os.close();
		System.out.println("Total data recieved : "+totalbyte_rx+" Bytes => "+(float)totalbyte_rx/(float)1000+" kBytes");
		System.out.println("AES Key: "+aes_key);
		
		//String fn = DecryptFile(filename);
		//
		DecryptByteArrays();
		if (this.decrypted_parts.size() == 0) throw new IOException("Impossible de déchiffrer les parties du aac");
		Frame.getInstance().getPanel().setStatusBarMsg("Conversion du fichier...");
		aacPartsToOneWav(filename);
		Frame.getInstance().getPanel().setStatusBarMsg("Ajout des métadonnées au titre");
		TagFile(filename.replace("_aes128", ""));
		Frame.getInstance().getPanel().setPBPercentage(1);
		Frame.getInstance().getPanel().setStatusBarMsg("Téléchargement terminé !");
		return filename;
	}
	
	private String DecryptFile(String filename) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		// Ouverture du fichier créé
		byte[] file = Files.readAllBytes(new File("data/"+filename).toPath());
		
		byte[] returned = Crypto.getInstance().Decrypt(file, aes_key_bytes);
		
		OutputStream os = new FileOutputStream(path.getPath()+"/"+filename.replace("_aes128", ""));
		os.write(returned);
		os.close();
		
		
		return filename;
	}
	
	private void aacPartsToOneWav(String filename) throws IOException, IllegalArgumentException, InputFormatException, EncoderException, UnsupportedAudioFileException {
		AudioAttributes audio = new AudioAttributes();
		audio.setCodec("pcm_s16le");
	    audio.setBitRate(16);
	    audio.setChannels(2);
	    audio.setSamplingRate(44100);
	    
	    EncodingAttributes attrs = new EncodingAttributes();
	    attrs.setFormat("wav");
	    attrs.setAudioAttributes(audio);
	    Encoder encoder = new Encoder();
	    ArrayList<String> tmp_aac = new ArrayList<>();
	    int count = 0;
	    // On écris tout les fichiers 
	    String fileuuid = "data/"+UUID.randomUUID();
	    
	    for (byte[] aac : decrypted_parts) {
	    	FileOutputStream fos = new FileOutputStream(fileuuid+"_"+count+".aac");
	    	fos.write(aac);
	    	fos.close();
	    	count+=1;
	    }
	    
	    ArrayList<AudioInputStream> array_ais = new ArrayList<>();
	    long length=0;
	    for (int i=0; i<count; i++) {
	    	File src = new File(fileuuid+"_"+i+".aac");
	    	File out = new File(fileuuid+"_"+i+".wav");
	    	encoder.encode(src, out, attrs);
	    	src.delete();
	    	AudioInputStream ais_current = AudioSystem.getAudioInputStream(out);
	    	length+=ais_current.getFrameLength();
	    	array_ais.add(ais_current);
	    }
	    
	    File out = new File(fileuuid+".wav");
	    
	    AudioInputStream appendedFiles = new AudioInputStream(new SequenceInputStream(Collections.enumeration(array_ais)), array_ais.get(0).getFormat(), length);
	    AudioSystem.write(appendedFiles, AudioFileFormat.Type.WAVE, out);
	        
	    for (AudioInputStream ais : array_ais) {
	    	try {
	    		ais.close();
	    	} catch(IOException e) {}
	    }
	    
	    
	    // delete
	    for (int i=0; i<count; i++) {
	    	File wav_part = new File(fileuuid+"_"+i+".wav");
	    	wav_part.delete();
	    }
	    
	    
	    AudioAttributes audio_aac = new AudioAttributes();
	    audio_aac.setCodec("libfaac");
	    audio_aac.setBitRate(256000);
	    audio_aac.setSamplingRate(44100);
	    audio_aac.setChannels(2);
	    EncodingAttributes attr_aac = new EncodingAttributes();
	    attr_aac.setFormat("aac");
	    attr_aac.setAudioAttributes(audio_aac);
	    Encoder enc_aac = new Encoder();
	    File newfile = new File(path.getPath()+"/"+filename.replace("_aes128",""));
	    enc_aac.encode(out, newfile, attr_aac);
	    out.delete();
	    
	}
	
	private void DecryptByteArrays() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, FileNotFoundException {
		for (byte[] part : crypted_parts) {
			decrypted_parts.add(Crypto.getInstance().Decrypt(part, aes_key_bytes));
		}
		
	}
	
	private void TagFile(String filename) throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException, CannotWriteException {
		AudioFile f = AudioFileIO.read(new File(path.getPath()+"/"+filename));
		Tag tag = f.getTag();
		tag.setField(FieldKey.ARTIST, mus.getArtist());
		tag.setField(FieldKey.TITLE, mus.getTitle());
		
		// Artwork
		Artwork cover = null;
		InputStream is = HTTP_Request.binaryRequest2(mus.getArtworkUrl().replace("%AIC_Image%", API.AIC_Image), API.getInstance().cookieString());
		// on écrit dans un fichier
		File artwork_jpg = new File("data/"+mus.getUUID()+".jpg");
		
		FileOutputStream fos = new FileOutputStream(artwork_jpg);
		is.transferTo(fos);
		fos.close();
		is.close();
		
		// url artwork : mus.getArtworkUrl().replace("%AIC_Image%", API.AIC_Image)
		cover = ArtworkFactory.createArtworkFromFile(artwork_jpg);
		tag.setField(cover);
		
		
		f.commit();
		artwork_jpg.delete();
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

	public void setPath(File path) {
		this.path = path;
	}
}

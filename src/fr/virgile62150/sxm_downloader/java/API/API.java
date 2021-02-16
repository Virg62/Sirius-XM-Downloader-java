package fr.virgile62150.sxm_downloader.java.API;


import java.io.IOException;
import java.net.MalformedURLException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import fr.virgile62150.sxm_downloader.java.jwt.Frame;
import fr.virgile62150.sxm_downloader.java.obj.Music;
import fr.virgile62150.sxm_downloader.java.obj.Radio;

public class API implements Runnable {
	
	private static final String INIT_URL = "http://player.siriusxm.com/rest/v2/experience/modules/resume?adsEligible=true&OAtrial=true";
	private static final String CHAN_URL = "http://player.siriusxm.com/rest/v4/experience/carousels?result-template=everest%7Cweb&page-name=channels_all&function=onlyAdditionalChannels&cacheBuster=1613250514060";
	private static final String SNGS_URL = "https://player.siriusxm.com/rest/v4/aic/tune?channelGuid=";
	private static final String AIC_Image = "https://siriusxm-priprodart.akamaized.net";
	private static final String AIC_Primary_HLS = "https://priprodtracks.mountain.siriusxm.com";
	private ArrayList<String> segs;
	private String without_m3u8;
	private Music m;
	
	private HashMap<String, String> cookies = new HashMap<>();
	
	private static API instance;
	
	private API() {
		//CookieManager manager = new CookieManager();
		//CookieHandler.setDefault(manager);
	}
	
	public static API getInstance() {
		if (instance == null)
			instance = new API();
		return instance;
	}
	
	public void init() throws MalformedURLException, IOException {
		
		String data_to_post = "{\"moduleList\":{\"modules\":[{\"moduleRequest\":{\"resultTemplate\":\"web\",\"deviceInfo\":{\"appRegion\":\"US\",\"language\":\"en\",\"browser\":\"Chrome\",\"browserVersion\":\"88.0.4324.150\",\"clientCapabilities\":[\"enhancedEDP\",\"seededRadio\",\"tabSortOrder\",\"zones\",\"cpColorBackground\",\"additionalVideo\",\"podcast\",\"irisPodcast\"],\"clientDeviceId\":null,\"clientDeviceType\":\"web\",\"deviceModel\":\"EverestWebClient\",\"osVersion\":\"Windows\",\"platform\":\"Web\",\"player\":\"html5\",\"sxmAppVersion\":\"5.35.3800\",\"sxmGitHashNumber\":\"96fc08e\",\"sxmTeamCityBuildNumber\":\"3800\",\"isChromeBrowser\":true,\"isMobile\":false,\"isNative\":false,\"supportsAddlChannels\":true,\"supportsVideoSdkAnalytics\":true}}}]}}";
		
		HashMap<String, String> rep = HTTP_Request.Request(INIT_URL, "", true,data_to_post);
		
		/*for (Map.Entry<String, String> entry : rep.entrySet()) {
			//System.out.println(entry.getKey()+" "+entry.getValue());
		}
		*/
	
		String jsess_cookie = rep.get("JSESSIONID");
		String jsess_val = jsess_cookie.split("=")[1].split(";")[0];
		
		String sxm_dt_cookie = rep.get("SXM-DATA");
		String sxm_dt_val = sxm_dt_cookie.split("=")[1].split(";")[0];
		
		
		cookies.put("JSESSIONID", jsess_val);
		cookies.put("SXM-DATA", sxm_dt_val);
	
	}
	
	public ArrayList<Radio> getChannels() throws MalformedURLException, IOException {
		HashMap<String, String> rep = HTTP_Request.Request(CHAN_URL, cookieString(), false, "");
		
		String http_string = "";
		
		for (Map.Entry<String, String> entry : rep.entrySet()) {
			if (!entry.getKey().equals("HTTP"))
				System.out.println(entry.getKey()+" "+entry.getValue());
			else
				http_string = entry.getValue();
		}
		
		// Json parsing
		Object obj = JSONValue.parse(http_string);
		JSONObject jobj = (JSONObject) obj;
		
		// Path to station list : 
		//JSONObject j_1 = ((JSONObject) ((JSONArray)((JSONObject) ((JSONObject) ((JSONObject) jobj.get("ModuleListResponse")).get("moduleList")).get("modules")).get(0)).get("moduleResponse")).get();
		// ModuleListResponse.moduleList.modules[0].moduleResponse.carousel[0].carouselTiles
		JSONArray sta_list = (JSONArray)((JSONObject)((JSONArray)((JSONObject)((JSONObject)((JSONArray)((JSONObject) ((JSONObject) jobj.get("ModuleListResponse")).get("moduleList")).get("modules")).get(0)).get("moduleResponse")).get("carousel")).get(0)).get("carouselTiles");
		
		ArrayList<Radio> radio_list = new ArrayList<>();
		
		for (Object o : sta_list) {
			JSONObject jobj_r = (JSONObject) o;
			radio_list.add(new Radio(jobj_r));
		}
		
		return radio_list;
	}
	
	public ArrayList<Music> getTrackList(String radio_id) throws MalformedURLException, IOException {
		HashMap<String, String> rep = HTTP_Request.Request(SNGS_URL+radio_id, cookieString(), false, "");
		
		String tracks_cookie = rep.get("__tracks__");
		String tracks_val = tracks_cookie.split("__tracks__=")[1].split(";")[0];
		cookies.put("__tracks__", tracks_val);
		
		JSONObject jobj = (JSONObject) JSONValue.parse(rep.get("HTTP"));
		
		JSONArray mus_list = (JSONArray)((JSONObject)((JSONObject)((JSONObject)((JSONObject)((JSONArray)((JSONObject)((JSONObject) jobj.get("ModuleListResponse")).get("moduleList")).get("modules")).get(0)).get("moduleResponse")).get("additionalChannelData")).get("clipList")).get("clips"); 
		
		ArrayList<Music> music_list = new ArrayList<>();
		
		for (Object mus : mus_list) {
			music_list.add(new Music((JSONObject) mus));
		}
		
		return music_list;
	}
	
	public void choseMusic(Music m) {
		this.m = m;
	}
	
	
	public void getSegmentList(Music m) throws MalformedURLException, IOException {
		String song_url = m.getUrl();
		song_url = song_url.replace("%AIC_Primary_HLS%", AIC_Primary_HLS);
		HashMap<String, String> rep = HTTP_Request.Request(song_url, cookieString(), false, "");
		this.m = m;
		
		ArrayList<String> fluxes = M3U8Parser.getInstance().master_parse(rep.get("HTTP"));
		
		// Par défaut, on prend la qualité la plus élevée... donc 256k, qui est le premier chx
		String quality_url = fluxes.get(0);
		
		String[] url_elts = song_url.split("/");
		
		StringBuilder newUrl = new StringBuilder();
		
		for (int i = 0; i<url_elts.length-1; i++) {
			newUrl.append(url_elts[i]+"/");
		}
		
		 
		
		newUrl.append(quality_url);
		
		String[] wait = newUrl.toString().split("/");
		without_m3u8 = newUrl.toString().replace(wait[wait.length-1], "");
		
		rep = HTTP_Request.Request(newUrl.toString(), cookieString(), false, "");
		
		segs = M3U8Parser.getInstance().master_parse(rep.get("HTTP"));
		
	}
	
	public void getFile() throws MalformedURLException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		String without_m3u8_4aes=without_m3u8.replace("https://priprodtracks.mountain.siriusxm.com/", "https://player.siriusxm.com/rest/streaming/");
		
		Download d = new Download(AES_download(without_m3u8_4aes+"key/4"), segs, m, without_m3u8, (AES_download_hex(without_m3u8_4aes+"key/4")));
		d.download_file();
		d.CleanTempFile();
	}
	
	public void getFile_swing() {
		try {
			getSegmentList(m);
			String without_m3u8_4aes=without_m3u8.replace("https://priprodtracks.mountain.siriusxm.com/", "https://player.siriusxm.com/rest/streaming/");
			
			Download d = new Download(AES_download(without_m3u8_4aes+"key/4"), segs, m, without_m3u8, (AES_download_hex(without_m3u8_4aes+"key/4")));
			d.download_file_swing();
			d.CleanTempFile();
		} catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
			Frame.getInstance().getPanel().showErrorDialog(e);
			Frame.getInstance().getPanel().setPBPercentage(1);
		}
	}
	
	public String AES_download(String url) throws MalformedURLException, IOException {
		HashMap<String, Object> rep = HTTP_Request.binaryRequest(url, cookieString());
		byte[] key = (byte[]) rep.get("HTTP");
		return M3U8Parser.getInstance().bytesToHex(key);
	}
	
	public byte[] AES_download_hex(String url) throws MalformedURLException, IOException {
		HashMap<String, Object> rep = HTTP_Request.binaryRequest(url, cookieString());
		byte[] key = (byte[]) rep.get("HTTP");
		return key;
	}
	
	protected String cookieString() {
		StringBuilder stb = new StringBuilder("");
		
		for (Map.Entry<String, String> entry : cookies.entrySet()) {
			stb.append(entry.getKey()+"="+entry.getValue()+"; ");
		}
		return stb.toString();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		getFile_swing();
	}
	
}

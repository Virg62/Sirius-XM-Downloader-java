package fr.virgile62150.sxm_downloader.java.API;


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import fr.virgile62150.sxm_downloader.java.obj.Music;
import fr.virgile62150.sxm_downloader.java.obj.Radio;

public class API {
	
	private static final String INIT_URL = "http://player.siriusxm.com/rest/v2/experience/modules/resume?adsEligible=true&OAtrial=true";
	private static final String CHAN_URL = "http://player.siriusxm.com/rest/v4/experience/carousels?result-template=everest%7Cweb&page-name=channels_all&function=onlyAdditionalChannels&cacheBuster=1613250514060";
	private static final String SNGS_URL = "https://player.siriusxm.com/rest/v4/aic/tune?channelGuid=";
	
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
		
		for (Map.Entry<String, String> entry : rep.entrySet()) {
			//System.out.println(entry.getKey()+" "+entry.getValue());
		}
		
	
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
		String tracks_val = tracks_cookie.split("=")[1].split(";")[0];
		cookies.put("__tracks__", tracks_val);
		
		JSONObject jobj = (JSONObject) JSONValue.parse(rep.get("HTTP"));
		
		JSONArray mus_list = (JSONArray)((JSONObject)((JSONObject)((JSONObject)((JSONObject)((JSONArray)((JSONObject)((JSONObject) jobj.get("ModuleListResponse")).get("moduleList")).get("modules")).get(0)).get("moduleResponse")).get("additionalChannelData")).get("clipList")).get("clips"); 
		
		ArrayList<Music> music_list = new ArrayList<>();
		
		for (Object mus : mus_list) {
			music_list.add(new Music((JSONObject) mus));
		}
		
		return music_list;
	}
	
	private String cookieString() {
		StringBuilder stb = new StringBuilder("");
		
		for (Map.Entry<String, String> entry : cookies.entrySet()) {
			stb.append(entry.getKey()+"="+entry.getValue()+"; ");
		}
		return stb.toString();
	}
	
}

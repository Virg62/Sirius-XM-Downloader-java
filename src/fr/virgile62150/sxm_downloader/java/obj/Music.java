package fr.virgile62150.sxm_downloader.java.obj;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Music {
	
	private String title = "", artist = "", url = "", artwork = "", uuid = "";
	
	public Music(JSONObject track) {
		title = (String) track.get("title");
		artist = (String) track.get("artistName");
		artwork = (String) track.get("clipImageUrl");
		uuid = (String) track.get("assetGuid");
		url = (String)((JSONObject)((JSONArray)((JSONObject) track.get("contentUrlList")).get("contentUrls")).get(0)).get("url");
	}

	public String getTitle() {
		return title;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getArtworkUrl() {
		return artwork;
	}
	
	public String getUUID() {
		return uuid;
	}
	
	public String toString() {
		return this.uuid +" : "+this.artist+" - "+this.title+"  | "+this.artwork;
	}
}

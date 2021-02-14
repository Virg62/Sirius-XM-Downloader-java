package fr.virgile62150.sxm_downloader.java.obj;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Radio {

	String title = "", id = "", imgurl = "";
	
	public Radio(JSONObject obj) {
		// Récupére le nom
		for (Object elt : (JSONArray) obj.get("tileAssetInfo")) {
			
			if (((String) (((JSONObject) elt).get("assetInfoKey"))).equals("channelName")) {
				title = ((String) (((JSONObject) elt).get("assetInfoValue")));
			} else if (((String) (((JSONObject) elt).get("assetInfoKey"))).equals("channelGuid")) {
				id = ((String) (((JSONObject) elt).get("assetInfoValue")));
			}
		}
		
		imgurl = (String)((JSONObject)((JSONArray)((JSONObject) (obj.get("tileMarkup"))).get("tileImage")).get(0)).get("imageLink");
	}
	
	public String getName() {
		return title;
	}
	
	public String getId() {
		return id;
	}
	
	public String getLogoUrl() {
		return imgurl;
	}
}

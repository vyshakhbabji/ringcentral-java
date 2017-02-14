package bot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.okhttp.Response;

import platform.Platform;

public class Groups {

	static String getGroupsById;
	static String getGroups;
	static JsonToHashMap toMap = new JsonToHashMap();
	
	Platform platform;
	
	public Groups(Platform platform) {
		super();
		this.platform = platform;
	}

	
	public static String getGetGroupsById() {
		return getGroupsById;
	}

	public static void setGetGroupsById(String getGroupsById) {
		Groups.getGroupsById = getGroupsById;
	}
	

	/*
	 * Get All Groups
	 * members array will have groupID
	 * make get("members") to get list of members over hashmap
	 */
	public static String getGetGroups() {
		return getGroups;
	}

	public static void setGetGroups(String getGroups) {
		Groups.getGroups = new JSONObject(getGroups).getJSONArray("records").getJSONObject(0).toString();
	}

	public static Map<String, Object> groupsToMap(){
		
		JSONObject jobj =  new JSONObject(getGroups);
		return toMap.jsonToMap(jobj);
	}

	public void printGroupsData(){
		Map<String,Object> map = groupsToMap();
		map.forEach((k,v) -> System.out.println(k + "=" + v+"\n")); 
	}
	
	public static Map<String, Object> groupsByIdToMap(){
		JSONObject jobj =  new JSONObject(getGroupsById);
		return toMap.jsonToMap(jobj);
	}
	
	public void printGroupsByIdData(){
		Map<String,Object> map = groupsByIdToMap();
		map.forEach((k,v) -> System.out.println(k + "=" + v)); 
	}
	
	public Map<String,Object> getAllGroups(String pageToken, String recordCount){
		JsonToHashMap toMap = new JsonToHashMap();
		JSONObject jobj=new JSONObject();
		try {
			String append = "";
			append+= pageToken.equals("") ?"pageToken=":"pageToken="+pageToken;
			append+="&";
			append+=recordCount.equals("")?"recordCount=":"recordCount="+recordCount;
			Response res  = platform.sendRequest("get", "/restapi/v1.0/glip/groups?"+append, null, null);
		    setGetGroups(res.body().string());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return groupsToMap();
	}

	public Map<String,Object> getGroupsById(String groupId){
		JsonToHashMap toMap = new JsonToHashMap();
		JSONObject jobj=new JSONObject();
		try {
			Response res  = platform.sendRequest("get", "/restapi/v1.0/glip/groups/"+groupId, null, null);
		    setGetGroupsById(res.body().string());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return groupsToMap();
	}

}

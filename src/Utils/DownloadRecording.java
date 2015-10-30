package Utils;

import http.APIResponse;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import platform.Platform;

public class DownloadRecording {
	
	Platform platform ;
	String path;
	
	public DownloadRecording(Platform platform, String path){
		this.platform=platform;
		this.path= path;
	}
	

	public void downloadRecording( JSONObject responseObject) throws IOException, InterruptedException {

		System.out.println("downloading");
		JSONArray jsonMainArr = responseObject.getJSONArray("records"); 
		HashMap<String, String> hm = new HashMap<String, String>();

		System.out.println("Downloading started...");
		for( int i=0 ; i< jsonMainArr.length(); i++ ){ 
			JSONObject  obj = (JSONObject) jsonMainArr.get(i);
			if (obj.has("recording")){
				JSONObject recording = (JSONObject) obj.get("recording");
				String key = path+obj.getString("id")+"_"+recording.get("id")+".mp3";
				String value = recording.get("uri").toString().replaceAll("https://platform.devtest.ringcentral.com", "")+"/content";
				hm.put(key, value);
			}	
		}
		saveRecordings(hm);
	}

	public void saveRecordings(HashMap<String, String> mp) throws IOException, InterruptedException {
		File file = new File(path+Calendar.getInstance().getTime().toString());
		file.getParentFile().mkdirs();

		PrintWriter out = new PrintWriter(file);
		out.println("Files downloaded in this session: "+Calendar.getInstance().getTime().toString());
		Iterator<Entry<String, String>> it = mp.entrySet().iterator();
		int i=0;

		while (it.hasNext()) {
			Entry<String, String> pair = it.next();
			String filename = pair.getKey().toString();
			String url = pair.getValue().toString();
			APIResponse response = platform.sendRequest("get", url, null, null);
			
			System.out.println("CONTENT  TYPE IS: "+response.headers());
			
			write(response.body().bytes(),filename);
			Thread.sleep(8000);
			System.out.println("File " +filename+ "downloaded.");
			i++;
			out.println(i+". "+filename);
			it.remove(); 
		}
		System.out.println("Download completed!");
		out.println("All files downloaded successfully. Number of Recordings are "+i);
		
		out.close();
		
	}

	static void write(byte[] aInput, String aOutputFileName){
		System.out.println("Writing binary file...");
		try {
			OutputStream output = null;
			try {
				output = new BufferedOutputStream(new FileOutputStream(aOutputFileName));
				output.write(aInput);
			}
			finally {
				output.close();
			}
		}
		catch(FileNotFoundException ex){
			System.out.println("File not found.");
		}
		catch(IOException ex){
			System.out.println(ex);
		}
	}

}

package platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.Consts;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Platform {

	public enum ContentTypeSelection {
		FORM_TYPE_MARKDOWN("application/x-www-form-urlencoded"), JSON_TYPE_MARKDOWN(
				"application/json"), MULTIPART_TYPE_MARKDOWN(
						"multipart/mixed; boundary=Boundary_1_14413901_1361871080888");

		private String value;

		private ContentTypeSelection(String contentType) {
			this.value = contentType;
		}

	}

	public enum Server {

		PRODUCTION(
				"https://platform.ringcentral.com"), SANDBOX("https://platform.devtest.ringcentral.com");
		private String value;

		private Server(String url) {
			this.value = url;
		}
	}

	public String accessToken;
	public String appKey;
	public String appSecret;
	Auth auth;
	final String authURL = "/restapi/oauth/token";

	public Server server;

	public Platform(String appKey, String appSecret, Server server) {
		super();
		this.appKey = appKey;
		this.appSecret = appSecret;
		this.server = server;
		this.auth = new Auth();
	}

	public void authCall(List<NameValuePair> body) {

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(server.value + authURL);
		post.setHeader(HttpHeaders.AUTHORIZATION, "Basic "
				+ encodeAPICredentialsToBase64());
		post.setHeader(HttpHeaders.CONTENT_TYPE,
				ContentTypeSelection.FORM_TYPE_MARKDOWN.value);
		post.setEntity(new UrlEncodedFormEntity(body, Consts.UTF_8));

		HttpResponse response = null;
		try {
			response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				// System.out.println(result.toString());
				Gson gson = new Gson();
				Type HashMapType = new TypeToken<HashMap<String, String>>() {
				}.getType();
				HashMap<String, String> authData = gson.fromJson(
						result.toString(), HashMapType);
				setAuth(auth, authData);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void authorize(String username, String extension, String password) {

		List<NameValuePair> body = new ArrayList<NameValuePair>();
		body.add(new BasicNameValuePair("username", username));
		body.add(new BasicNameValuePair("password", password));
		body.add(new BasicNameValuePair("extension", extension));
		body.add(new BasicNameValuePair("grant_type", "password"));

		authCall(body);

	}

	public String encodeAPICredentialsToBase64() {
		String apiCredentials = appKey + ":" + appSecret;
		byte[] message = apiCredentials.getBytes();
		return DatatypeConverter.printBase64Binary(message);
	}

	public String getAuthHeader(){
		return this.auth.getTokenType() + " " + this.getAccessToken();
	}

	public String getAccessToken() {
		return auth.getAccessToken();
	}

	public Auth getAuth() {
		return auth;
	}

	public void setAuth(Auth auth, HashMap<String, String> authData) {
		this.auth.setData(authData);
	}


	//apiCall("post", "/call-log", "{body}", header)

	//Additionals



	public HttpResponse apiCall(String method, String apiURL, LinkedHashMap<String, String> body, HashMap<String, String> headerMap) throws IOException {

		//this.isAuthorized();

		String URL = server.value+apiURL;

		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response=null;
		if(headerMap==null){
			headerMap= new HashMap<String, String>();
		}

		headerMap.put("Authorization", getAuthHeader());
		if(method.equalsIgnoreCase("get")){
			HttpGet httpget = new  HttpGet(URL);
			for (Entry<String, String> entry : headerMap.entrySet()) {
				httpget.addHeader(entry.getKey(), entry.getValue());
			}
			response = client.execute(httpget);
		}

		return response;
	}



	//
	//BufferedReader in = null;
	//
	//String output =  "";
	//try {
	//
	//	StringBuilder data = new StringBuilder();
	//	byte[] byteArray = data.toString().getBytes("UTF-8");
	//
	//	URL requests = new URL(urll);
	//	httpConn = (HttpsURLConnection) requests.openConnection();
	//	httpConn.setRequestMethod("GET");
	//	httpConn.setRequestProperty(
	//			"Authorization",
	//			"Bearer "+getAccessToken().toString());
	//	httpConn.setDoOutput(true);
	//	System.out.println("Here");
	//	InputStreamReader reader = new InputStreamReader(
	//			httpConn.getInputStream());
	//	in = new BufferedReader(reader);
	//	System.out.println("Done");
	//	StringBuffer content = new StringBuffer();
	//	String line;
	//	while ((line = in.readLine()) != null) {
	//		content.append(line + "\n");
	//	}
	//	in.close();
	//
	//	String json = content.toString();
	//	Date date = new Date();
	//	output = "--------------"+date+"-------------------\n";
	//	output = output+ json;
	//	System.out.println("presence : "+ json);
	//
	//} catch (java.io.IOException e) {
	//	output = output+ e.getMessage();
	//	System.out.println(e.getMessage());
	//
	//} finally {
	//	if (in != null)
	//		in.close();
	//	if (httpConn != null)
	//		httpConn.disconnect();
	//}
	//
	//output=output+"\n --------------------------------------------\n\n\n";
	//
	//
	//}
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//











	//					
	//					
	//					
	//					
	//			OkHttpClient client = new OkHttpClient();
	//			//Check if the Platform is authorized, and add the authorization header
	//			this.isAuthorized();
	//			headerMap.put("Authorization", this.getAuthHeader());
	//			//Generate the proper url to be passed into the request
	//			HashMap<String, String> options = new HashMap<>();
	//			options.put("addServer", "true");
	//			String apiUrl = apiURL(url, options);
	//
	//			Request.Builder requestBuilder = new Request.Builder();
	//			//Add all the headers to the Request.Builder from the headerMap
	//			for (Map.Entry<String, String> entry : headerMap.entrySet()) {
	//				requestBuilder.addHeader(entry.getKey(), entry.getValue());
	//			}
	//			Request request = null;
	//			if (method.toUpperCase().equals("GET")) {
	//				request = requestBuilder
	//						.url(apiUrl)
	//						.build();
	//			} else if (method.toUpperCase().equals("DELETE")) {
	//				request = requestBuilder
	//						.url(apiUrl)
	//						.delete()
	//						.build();
	//			} else {
	//				//For POST and PUT requests, find and set what MediaType the body is
	//				MediaType mediaType;
	//				if (headerMap.containsValue("application/json")) {
	//					mediaType = MediaType.parse(ContentTypeSelection.JSON_TYPE_MARKDOWN.toString());
	//				} else if (headerMap.containsValue("multipart/mixed")) {
	//					mediaType = MediaType.parse(ContentTypeSelection.MULTIPART_TYPE_MARKDOWN.toString());
	//				} else {
	//					mediaType =MediaType.parse(ContentTypeSelection.FORM_TYPE_MARKDOWN.toString());
	//				}
	//				String bodyString = getBodyString(body, mediaType);
	//				if (method.toUpperCase().equals("POST")) {
	//					request = requestBuilder
	//							.url(apiUrl)
	//							.post(RequestBody.create(mediaType, bodyString))
	//							.build();
	//				} else if (method.toUpperCase().equals("PUT")) {
	//					request = requestBuilder
	//							.url(apiUrl)
	//							.put(RequestBody.create(mediaType, bodyString))
	//							.build();
	//				}
	//			}
	//			//Make OKHttp request call, that returns response to the callback
	//			client.newCall(request).enqueue(callback);
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
}



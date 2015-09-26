package platform;

import http.APIResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;

import okio.Buffer;

import org.apache.http.Consts;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.internal.http.HeaderParser;

public class Platform {

	public enum ContentTypeSelection {
		FORM_TYPE_MARKDOWN("application/x-www-form-urlencoded"), 
		JSON_TYPE_MARKDOWN("application/json"), 
		MULTIPART_TYPE_MARKDOWN(
				"multipart/mixed; boundary=Boundary_1_14413901_1361871080888");
		public MediaType value;
		private ContentTypeSelection(String contentType) {
			this.value = MediaType.parse(contentType);
		}
	}

	public enum Server {
		PRODUCTION(
				"https://platform.ringcentral.com"), 
				SANDBOX("https://platform.devtest.ringcentral.com");
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
	private  HeaderParser headers  ;
	public Server server;
	Response response;
	Request request;
	
	StackTraceElement l = new Exception().getStackTrace()[0];
	


	public Platform(String appKey, String appSecret, Server server) {
		super();
		this.appKey = appKey;
		this.appSecret = appSecret;
		this.server = server;
		this.auth = new Auth();
	}

	protected String createBodyString(HashMap<String, String> body, ContentTypeSelection type) {
		String bodyString = "";
		MediaType mediaType = type.value;
		try {
			StringBuilder data = new StringBuilder();
			int count = 0;
			if (!(mediaType == ContentTypeSelection.FORM_TYPE_MARKDOWN.value)) {
				data.append("{ ");
			}
			for (HashMap.Entry<String, String> entry : body.entrySet()) {
				if (mediaType ==ContentTypeSelection.FORM_TYPE_MARKDOWN.value) {
					if (count != 0) {
						data.append("&");
					}
					data.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
					count++;
				} else {
					if (count != 0) {
						data.append(", ");
					}
					data.append(entry.getKey());
					data.append(": ");
					data.append(entry.getValue());
					count++;
				}
			}
			if (!(mediaType ==ContentTypeSelection.FORM_TYPE_MARKDOWN.value)) {
				data.append(" }");
			}
			bodyString = data.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return bodyString;
	}

	protected String bodyToString(final Request request){
		try {
			final Request copy = request.newBuilder().build();
			final Buffer buffer = new Buffer();
			copy.body().writeTo(buffer);
			System.out.println(copy.header("Authorization"));

			System.out.println(copy.header("Content-Type"));
			return buffer.readUtf8();
		} catch (final IOException e) {
			return "did not work";
		}
	}


	public void authCall(HashMap<String, String> body) {

		String URL = server.value + authURL;
		OkHttpClient client = new OkHttpClient();
		Request.Builder requestBuilder = new Request.Builder();
		request =  requestBuilder
				.url(URL).
				addHeader(HttpHeaders.AUTHORIZATION, "Basic "+encodeAPICredentialsToBase64()).
				addHeader(HttpHeaders.CONTENT_TYPE,ContentTypeSelection.FORM_TYPE_MARKDOWN.value.toString()).
				post(RequestBody.create(ContentTypeSelection.FORM_TYPE_MARKDOWN.value, createBodyString(body, ContentTypeSelection.FORM_TYPE_MARKDOWN))).
				build();

		System.out.println("Check Body of Request: "+bodyToString(request));

		try {
			response = client.newCall(request).execute();
			if (response.isSuccessful()) 
				setAuth(auth, response);
			else
				System.out.println("Authorization not successful");
			//throw new IOException();
		} catch (IOException e) {
			System.err.print("Failed Authorization. IOException occured in Class:  " + this.getClass().getName() + ": " + e.getMessage() +l.getClassName()+"/"+l.getMethodName()+":"+l.getLineNumber());
		}
	}


	public void authorize(String username, String extension, String password) {

		HashMap<String,String> body = new HashMap<String, String>();
		body.put("username", username);
		body.put("password", password);
		body.put("extension", extension);
		body.put("grant_type", "password");

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

	public void setAuth(Auth auth, Response response) {

		BufferedReader rd;
		HashMap<String, String> data= new HashMap<String, String>();
		try {
			rd = new BufferedReader(new InputStreamReader(
					response.body().byteStream()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			// System.out.println(result.toString());
			Gson gson = new Gson();
			Type HashMapType = new TypeToken<HashMap<String, String>>() {
			}.getType();
			data= gson.fromJson(
					result.toString(), HashMapType);
		} catch (IOException e) {
			System.err.print("Failed Authorization. IOException occured in Class:  " + this.getClass().getName() + ": " + e.getMessage()+l.getClassName()+"/"+l.getMethodName()+":"+l.getLineNumber());
		}
		this.auth.setData(data);

	}

	public Builder requestBuilder( HashMap<String, String> hm) {

		if(hm==null){
			hm= new HashMap<String, String>();
		}
		hm.put("Authorization", getAuthHeader());

		Builder requestBuilder = new Request.Builder();		
		for (Entry<String, String> entry : hm.entrySet()) {
			requestBuilder.addHeader(entry.getKey(), entry.getValue());	
		}
		return requestBuilder;
	}

	public APIResponse apiCall(String method, String apiURL, RequestBody body, HashMap<String, String> headerMap) throws IOException {

		//this.isAuthorized();
		String URL = server.value+apiURL;
		OkHttpClient client = new OkHttpClient();
		try{
			System.out.println(getAuthHeader());
			if(method.equalsIgnoreCase("get")){
				request = requestBuilder(headerMap).url(URL).build();
			}
			else if (method.equalsIgnoreCase("delete")){
				request = requestBuilder(headerMap).url(URL).delete().build();
			}
			else{
				if(method.equalsIgnoreCase("post")){
					request=requestBuilder(headerMap).url(URL).post(body).build();
				}
				else if(method.equalsIgnoreCase("put")){
					request = requestBuilder(headerMap).url(URL).put(body).build();
				}
			}

		} catch (Exception e) {
			System.err.print("Failed APICall. Exception occured in Class:  " + this.getClass().getName() + ": " + e.getMessage()+l.getClassName()+"/"+l.getMethodName()+":"+l.getLineNumber());
		}
		response = client.newCall(request).execute();
		return new APIResponse(request,response);	
	}
}



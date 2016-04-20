package platform;

import http.ApiException;
import http.ApiResponse;
import http.Client;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.HttpHeaders;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class Platform {

	public enum ContentTypeSelection {
		FORM_TYPE_MARKDOWN("application/x-www-form-urlencoded"), JSON_TYPE_MARKDOWN(
				"application/json"), MULTIPART_TYPE_MARKDOWN("multipart/mixed;");
		protected MediaType value;

		private ContentTypeSelection(String contentType) {
			this.value = MediaType.parse(contentType);
		}
	}
	
	private static final String USER_AGENT = "JAVA "
			+ System.getProperty("java.version") + "/RCJAVASDK";
	
	static double getVersion() {
		String version = System.getProperty("java.version");
		int pos = version.indexOf('.');
		pos = version.indexOf('.', pos + 1);
		return Double.parseDouble(version.substring(0, pos));
	}

	public enum Server {
		PRODUCTION("https://platform.ringcentral.com"), SANDBOX(
				"https://api.devtest.ringcentral.com");
		private String value;

		Server(String url) {
			this.value = url;
		}
	}

	protected final int ACCESS_TOKEN_TTL = 3600;
	protected String appKey;
	protected String appSecret;
	protected Auth auth;
	protected Client client;
	private Object lock = new Object();
	protected final int REFRESH_TOKEN_TTL = 604800;
	protected Request request;
	Response response;
	String account = "~";
	
	
	final String REVOKE_ENDPOINT_URL = "/restapi/oauth/revoke";

	protected Server server;

	final String TOKEN_ENDPOINT_URL = "/restapi/oauth/token";

	public Platform(Client client, String appKey, String appSecret,
			Server server) {
		super();
		this.appKey = appKey;
		this.appSecret = appSecret;
		this.server = server;
		this.auth = new Auth();
		this.client = client;
	}

	public String apiKey() {
		return Credentials.basic(appKey, appSecret);
	}

	public Auth auth() {
		return auth;
	}

	protected String authHeader() {
		return this.auth.tokenType() + " " + this.auth.access_token;
	}

	protected boolean ensureAuthentication() {

		if (this.auth.accessTokenValid()) {
			return true;
		} else {
			synchronized (lock) {
				try {
					this.refresh();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			return true;
		}
	}

	protected RequestBody formBody(HashMap<String, String> body) {
		FormEncodingBuilder formBody = new FormEncodingBuilder();
		for (HashMap.Entry<String, String> entry : body.entrySet())
			formBody.add(entry.getKey(), entry.getValue());
		return formBody.build();
	}

	public Builder inflateRequest(HashMap<String, String> hm) {
		// add user-agent
		if (hm == null) {
			hm = new HashMap<String, String>();
			if (ensureAuthentication())
				hm.put("Authorization", authHeader());
		}
		Builder requestBuilder = new Request.Builder();
		for (Entry<String, String> entry : hm.entrySet())
			requestBuilder.addHeader(entry.getKey(), entry.getValue());
		
		 requestBuilder.addHeader("User-Agent", USER_AGENT);
		
		return requestBuilder;
	}

	public HashMap<String, String> jsonToHashMap() throws IOException {
		if (response.isSuccessful()) {
			Gson gson = new Gson();
			Type HashMapType = new TypeToken<HashMap<String, String>>() {
			}.getType();
			String responseString = response.body().string();
			System.out.println(responseString);
			return gson.fromJson(responseString, HashMapType);
		} else {
			System.out.println("Error Message: " + "HTTP Status Code "
					+ response.code() + " " + response.message() +"/n"+response.body().string());
			return new HashMap<>();
		}
	}

	public boolean loggedIn() throws Exception {
		return this.auth.accessTokenValid();
	}

	public Response login(String userName, String extension, String password)
			throws IOException {

		HashMap<String, String> body = new HashMap<String, String>();
		body.put("username", userName);
		body.put("password", password);
		body.put("extension", extension);
		body.put("grant_type", "password");
	
		this.response = requestToken(TOKEN_ENDPOINT_URL, body);
		return response;
	}

	public Response logout() throws IOException {
		HashMap<String, String> body = new HashMap<String, String>();
		body.put("access_token", this.auth.access_token);
		this.response = requestToken(REVOKE_ENDPOINT_URL, body);
		this.auth.reset();
		return response;
	}

	public Response refresh() throws Exception {

		if (!this.auth.refreshTokenValid()) {
			throw new IOException("Refresh Token has Expired");
		} else {
			HashMap<String, String> body = new HashMap<String, String>();
			body.put("grant_type", "refresh_token");
			body.put("refresh_token", this.auth.refreshToken());
			return requestToken(TOKEN_ENDPOINT_URL, body);
		}

	}


	


	protected Response requestToken(String endpoint,
			HashMap<String, String> body) throws IOException {

		final String URL = server.value + endpoint;
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", apiKey());
		headers.put("Content-Type",
				ContentTypeSelection.FORM_TYPE_MARKDOWN.value.toString());
		request = inflateRequest(headers).url(URL).post(formBody(body)).build();
		this.response = client.loadResponse(request);
		setAuth();
		return response;

	}
	
	
	
	public String apiURL(String url){
        String builtUrl = "";
        boolean has_http = url.contains("http://") || url.contains("https://");
        if(!has_http){
            builtUrl += server.value;
        }
        if(!(url.contains("/restapi")) && !has_http){
            builtUrl += "/restapi" + "/" + "v1.0";
        }
        if(url.contains("/account/")){
            builtUrl = builtUrl.replace("/account/" + "~", "/account/" + this.account);
        }
        builtUrl += url;
        System.out.println("BUILDING URL CHECK: "+builtUrl);
        return builtUrl;
    }

	
	

	public Response sendRequest(String method, String apiURL, RequestBody body,
			HashMap<String, String> headerMap) {
		final String URL = apiURL(apiURL);
		try {
			ensureAuthentication();
			request = client.createRequest(method, URL, body,
					inflateRequest(headerMap));
			response = client.loadResponse(request);
		} catch (Exception e) {
			System.err.print("Failed APICall. Exception occured in Class:"
					+ this.getClass().getName() + "\n");
			e.printStackTrace();
		}
		return response;
	}

	public void setAuth() throws IOException {
		this.auth.setData(jsonToHashMap());
	}

}

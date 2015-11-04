package platform;

import http.APIResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map.Entry;

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
		public MediaType value;

		private ContentTypeSelection(String contentType) {
			this.value = MediaType.parse(contentType);
		}
	}

	public enum Server {
		PRODUCTION("https://platform.ringcentral.com"), SANDBOX(
				"https://platform.devtest.ringcentral.com");
		private String value;

		private Server(String url) {
			this.value = url;
		}
	}

	protected final int ACCESS_TOKEN_TTL = 3600;
	protected String accessToken;

	protected String appKey;

	protected String appSecret;
	protected Auth auth;
	StackTraceElement l = new Exception().getStackTrace()[0];

	protected final int REFRESH_TOKEN_TTL = 604800;

	Request request;
	Response response;

	final String REVOKE_ENDPOINT_URL = "/restapi/oauth/revoke";
	protected Server server;

	final String TOKEN_ENDPOINT_URL = "/restapi/oauth/token";

	public Platform(String appKey, String appSecret, Server server) {
		super();
		this.appKey = appKey;
		this.appSecret = appSecret;
		this.server = server;
		this.auth = new Auth();
	}

	public String apiKey() {
		return Credentials.basic(appKey, appSecret);
	}

	public Response authCall(String endpoint, HashMap<String, String> body) {

		String URL = server.value + endpoint;
		OkHttpClient client = new OkHttpClient();

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(HttpHeaders.AUTHORIZATION, apiKey());
		headers.put(HttpHeaders.CONTENT_TYPE,
				ContentTypeSelection.FORM_TYPE_MARKDOWN.value.toString());

		

		try {
			request = requestBuilder(headers).url(URL).post(formBody(body)).build();
			response = client.newCall(request).execute();
			if (response.isSuccessful() && endpoint.equals(TOKEN_ENDPOINT_URL)) {
				setAuth(response);
			} else
				System.err.println("Authorization not successful");
		} catch (IOException e) {
				System.err.println(e.getStackTrace());
		}
		return response;
	}

	protected String authHeader() {
		return this.auth.tokenType() + " " + this.getAccessToken();
	}

	protected boolean ensureAuthentication() throws IOException {
		if (!this.auth.accessTokenValid()) {
			this.refresh();
			return false;
		} else
			return true;
	}

	RequestBody formBody(HashMap<String, String> body) {
		FormEncodingBuilder formBody = new FormEncodingBuilder();
		for (HashMap.Entry<String, String> entry : body.entrySet()) {
			formBody.add(entry.getKey(), entry.getValue());
		}
		return formBody.build();
	}

	public String getAccessToken() {
		return auth.accessToken();
	}

	public Auth getAuth() {
		return auth;
	}

	public boolean loggedIn() throws Exception {

		try {
			return this.auth.accessTokenValid()
					|| this.refresh().isSuccessful();
		} catch (Exception e) {
			throw e;
		}
	}

	public Response login(String userName, String extension, String password) {

		HashMap<String, String> body = new HashMap<String, String>();
		body.put("username", userName);
		body.put("password", password);
		body.put("extension", extension);
		body.put("grant_type", "password");

		return authCall(TOKEN_ENDPOINT_URL, body);
	}

	public void logout() {
		HashMap<String, String> body = new HashMap<String, String>();
		body.put("access_token", this.getAccessToken());
		this.authCall(REVOKE_ENDPOINT_URL, body);
		this.auth.reset();
	}

	public Response refresh() throws IOException {
		if (!this.auth.refreshTokenValid()) {
				throw new IOException("Refresh Token Expired");
		}
		HashMap<String, String> body = new HashMap<String, String>();
		body.put("grant_type", "refresh_token");
		body.put("refresh_token", this.auth.getRefreshToken());
		return authCall(TOKEN_ENDPOINT_URL, body);
	}

	protected Builder requestBuilder(HashMap<String, String> hm) throws IOException {

		if (hm == null) {
			hm = new HashMap<String, String>();
			if (ensureAuthentication())
				hm.put("Authorization", authHeader());
		}
		Builder requestBuilder = new Request.Builder();
		for (Entry<String, String> entry : hm.entrySet()) {
			requestBuilder.addHeader(entry.getKey(), entry.getValue());
		}
		return requestBuilder;
	}

	public APIResponse sendRequest(String method, String apiURL,
			RequestBody body, HashMap<String, String> headerMap) {



		try {
			ensureAuthentication();
			String URL = server.value + apiURL;
			OkHttpClient client = new OkHttpClient();
			if (method.equalsIgnoreCase("get")) {
				request = requestBuilder(headerMap).url(URL).build();
			} else if (method.equalsIgnoreCase("delete")) {
				request = requestBuilder(headerMap).url(URL).delete().build();
			} else {
				if (method.equalsIgnoreCase("post")) {
					request = requestBuilder(headerMap).url(URL).post(body)
							.build();
				} else if (method.equalsIgnoreCase("put")) {
					request = requestBuilder(headerMap).url(URL).put(body)
							.build();
				}
			}
			response = client.newCall(request).execute();
			return new APIResponse(response);

		} catch (Exception e) {
			System.err.print("Failed APICall. Exception occured in Class:"
					+ this.getClass().getName() + "\n" + e.getMessage()
					+ l.getClassName() + "/" + l.getMethodName() + ":"
					+ l.getLineNumber());
		}
		return null;

	}

	protected void setAuth(Response response) throws IOException {

		String result = response.body().string();
		HashMap<String, String> data = new HashMap<String, String>();
		try {
			Gson gson = new Gson();
			Type HashMapType = new TypeToken<HashMap<String, String>>() {
			}.getType();
			data = gson.fromJson(result, HashMapType);
			this.auth.setData(data);
		} catch (Exception e) {
			throw new IOException(
					"Failed Authorization. Exception occured in Class:  "
							+ this.getClass().getName() + ": " +e.getStackTrace());
		}
		
	}

}

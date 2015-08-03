package platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import javax.xml.bind.DatatypeConverter;

import org.apache.http.Consts;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Platform {

	public String accessToken;

	public String appKey;
	public String appSecret;

	Auth auth;

	final String authURL = "/restapi/oauth/token";
	public String RC_PRODUCTION = "https://platform.ringcentral.com";

	public String RC_SANDBOX = "https://platform.devtest.ringcentral.com";
	public String server;

	public Platform(String appKey, String appSecret, String server) {
		super();
		this.appKey = appKey;
		this.appSecret = appSecret;
		this.server = server.toString().equals("RC_PRODUCTION") ? RC_PRODUCTION
				: RC_SANDBOX;
		this.auth = new Auth();

	}

	public void authCall(List<NameValuePair> body) {

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(this.server + authURL);
		post.setHeader(HttpHeaders.AUTHORIZATION, "Basic "
				+ encodeAPICredentialsToBase64());
		post.setHeader(HttpHeaders.CONTENT_TYPE,
				"application/x-www-form-urlencoded");
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
				System.out.println(result.toString());
				Gson gson = new Gson();
				Type HashMapType = new TypeToken<HashMap<String, String>>() {
				}.getType();
				HashMap<String, String> authData = gson.fromJson(result.toString(),
						HashMapType);
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

	public String getAccessToken() {
		return auth.getAccessToken();
	}

	public Auth getAuth() {
		return auth;
	}

	public void setAuth(Auth auth, HashMap<String, String> authData) {
		this.auth.setData(authData);
	}

}
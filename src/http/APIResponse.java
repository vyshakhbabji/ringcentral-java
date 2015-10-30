package http;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

public class APIResponse {

	protected Request request;
	protected Response response;

	public APIResponse(Response response) {
		this.request = response.request();
		this.response = response;
	}

	public ResponseBody body() {
		return this.response.body();
	}

	@SuppressWarnings("finally")
	public String error() {
		if (this.response == null || this.ok()) {
			return null;
		}

		String message = "HTTP" + this.response().code();

		JSONObject data;

		try {
			data = this.json();
			if (data.getString("message") != null)
				message = message + data.getString("message");
			else if (data.getString("error_description") != null)
				message = message + data.getString("error_description");
			else if (data.getString("description") != null)
				message = message + data.getString("description");

		} catch (JSONException e) {
			message = message + "JSONException occured in Class:  "
					+ this.getClass().getName() + ": " + e.getMessage();
			System.err.print("JSONException occured in Class:  "
					+ this.getClass().getName() + ": " + e.getMessage());
		} finally {
			return message;
		}
	}

	protected String getContentType() {
		return this.response.headers().get("Content-Type");
	}

	protected boolean isContentType(String contentType) {
		return this.response().body().contentType().toString()
				.equalsIgnoreCase(contentType);
	}

	public JSONObject json() {
		JSONObject jObject = new JSONObject();
		try {
			jObject = new JSONObject(response.body().string());
			throw new IOException();
		} catch (JSONException e) {
			System.err
					.print("JSONException occured while converting the HTTP response to JSON in Class:  "
							+ this.getClass().getName() + ": " + e.getMessage());
		} catch (IOException e) {
			System.err
					.print("IOException occured while converting the HTTP response to JSON in Class:  "
							+ this.getClass().getName() + ": " + e.getMessage());
		}
		return jObject;
	}

	public boolean ok() {
		int status = this.response.code();
		return (status >= 200 && status < 300);
	}

	public ResponseBody raw() {
		return this.body();
	}

	public Request request() {
		return this.request;
	}

	public Response response() {
		return this.response;
	}

	public String text() throws IOException {
		String responseAsText = "";
		try {
			responseAsText = response.body().string();
			return responseAsText;
		} catch (IOException e) {
			throw e;
		}

	}
	
	public String headers(){
		return response.headers().toString();
	}

	// todo: multipart def
	// todo: break_into_parts
}
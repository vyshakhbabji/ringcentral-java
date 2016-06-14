package http;

/*
 * Copyright (c) 2015 RingCentral, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

public class ApiResponse {

	protected Request request;
	protected Response response;

	public ApiResponse(Response response) {
		this.request = response.request();
		this.response = response;
	}

	public ResponseBody body() {
		return this.response.body();
	}

	public String error() {

		String message = "";
		if (statusCode() >= 300) {
			message = "HTTP error code: " + statusCode() + "\n";

			try {
				JSONObject data = new JSONObject(this.response.body().string());
				if (data.getString("message") != null)
					message = message + data.getString("message");
				else if (data.getString("error_description") != null)
					message = message + data.getString("error_description");
				else if (data.getString("description") != null)
					message = message + data.getString("description");

			} catch (JSONException | IOException e) {
				message = message + "JSONException occured in Class:  "
						+ this.getClass().getName() + "\n" + e.getMessage();
			}
		}

		return message;
	}

	protected String getContentType() {
		return this.response.headers().get("Content-Type");
	}

	protected boolean isContentType(String contentType) {
		return getContentType().toString().equalsIgnoreCase(contentType);
	}

	public JsonElement json() {
		JsonElement jObject = new JsonObject();
		try {
			JsonParser parser = new JsonParser();
			jObject = parser.parse(response.body().string());
			return jObject;
		} catch (JSONException e) {
			System.err
					.print("JSONException occured while converting the HTTP response to JSON in Class:  "
							+ e.getStackTrace());
		} catch (IOException e) {
			System.err
					.print("IOException occured while converting the HTTP response to JSON in Class:  "
							+ this.getClass().getName()
							+ ": "
							+ e.getStackTrace());
		}
		return jObject;
	}

	public boolean ok() {
		return (statusCode() >= 200 && statusCode() < 300);
	}

	public Request request() {
		return this.request;
	}

	public Headers requestHeaders() {
		return request.headers();
	}

	public Response response() {
		return this.response;
	}

	public Headers responseHeaders() {
		return response.headers();
	}

	public int statusCode() {
		return this.response.code();
	}

	public String text() throws IOException {
		return body().string();

	}
}
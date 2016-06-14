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
import java.util.HashMap;
import java.util.Map;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

/**
 * Created by vyshakh.babji on 11/9/15.
 */

public class Client {

	OkHttpClient client;

	public Client() {
		client = new OkHttpClient();
	}

	public Request createRequest(String method, String URL, RequestBody body,
			Builder header) {

		Request.Builder request = new Request.Builder();

		if (header == null)
			header = new Builder();

		if (method.equalsIgnoreCase("get")) {
			request = header.url(URL);
		} else if (method.equalsIgnoreCase("delete")) {
			request = header.url(URL).delete();

		} else {
			if (method.equalsIgnoreCase("post")) {
				request = header.url(URL).post(body);

			} else if (method.equalsIgnoreCase("put")) {
				request = header.url(URL).put(body);
			}
		}
		return request.build();
	}

	public Request createRequest(String method, String URL, RequestBody body,
			HashMap<String, String> headers) {

		Request.Builder builder = new Request.Builder();

		if (headers == null)
			headers = new HashMap<String, String>();

		for (Map.Entry<String, String> entry : headers.entrySet())
			builder.addHeader(entry.getKey(), entry.getValue());

		// TODO Add default headers here, e.g. JSON-related stuff

		if (method.equalsIgnoreCase("get")) {
			builder = builder.url(URL);
		} else if (method.equalsIgnoreCase("delete")) {
			builder = builder.url(URL).delete();
		} else {
			if (method.equalsIgnoreCase("post")) {
				builder = builder.url(URL).post(body);

			} else if (method.equalsIgnoreCase("put")) {
				builder = builder.url(URL).put(body);
			} else
				throw new ApiException(
						method
								+ " Method not Allowed. Please Refer API Documentation. See\n"
								+ "     * <a href =\"https://developer.ringcentral.com/api-docs/latest/index.html#!#Resources.html\">Server Endpoint</a> for more information. ");
		}

		return builder.build();
	}

	public Headers getRequestHeader(Request request) {
		return request.headers();
	}

	public Response loadResponse(Request request) throws IOException {
		return send(request).execute();
	}

	public Call send(final Request request) {
		return client.newCall(request);

	}

}

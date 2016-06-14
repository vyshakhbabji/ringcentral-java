package utils;

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

import org.json.JSONObject;

import platform.Platform;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class RingOut {

	static int i = 1;
	String callerID;
	String fromNumber;
	Platform platform;

	String toNumber;

	public RingOut(Platform platform, String fromNumber, String toNumber,
			String callerID, int counter) {
		this.platform = platform;
		this.fromNumber = fromNumber;
		this.toNumber = toNumber;
		if (callerID == null || callerID.equals(""))
			this.callerID = "";
		else
			this.callerID = callerID;
	}

	JSONObject createJsonNumberPair(String number) {
		JSONObject pair = new JSONObject();
		pair.put("phoneNumber", number);
		return pair;
	}

	public String createRingOut() throws Exception {

		JSONObject jbody = jRingOutBody();
		RequestBody body = RequestBody.create(MediaType
				.parse("application/json"), jbody.toString().getBytes());
		Response response = platform.sendRequest("post",
				"/restapi/v1.0/account/~/extension/~/ringout", body, null);
		System.out.println("\nMaking a Ringout (" + i + ")...");
		if (response.code() == 200) {
			String str = response.body().string();
			System.out.println(str);
			JSONObject jResJsonObject = new JSONObject(str);
			String ringoutID = jResJsonObject.get("id").toString();
			return ringoutID;
		} else {
			System.out.println(response.code());
			System.out.println(response.body().string());
			throw new Exception("Ringout not working as Expected!!");
		}
	}

	JSONObject jRingOutBody() {

		JSONObject jbody = new JSONObject();
		jbody.put("to", createJsonNumberPair(fromNumber));
		jbody.put("from", createJsonNumberPair(toNumber));
		jbody.put("callerId", createJsonNumberPair(callerID));
		jbody.put("playPrompt", "false");
		return jbody;
	}
}

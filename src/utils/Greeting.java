
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

import java.io.File;
import java.io.IOException;

import platform.Platform;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class Greeting {

	File file =  new File(
			"/Users/vyshakh.babji/Desktop/file.wav");
	Platform platform;

	public Greeting(Platform platform) {
		this.platform = platform;
//		this.file = file;
	}

	public Response createGreeting( MediaType faxContentType) throws IOException {

//		String payload = "{\r\n \"type\": \"Announcement\",\r\n \"answeringRule\": { \"id\": \"business-hours-rule\" }\r\n }";
		String payload = "{\r\n \"type\": \"ConnectingAudio\",\r\n \"answeringRule\": { \"id\": \"29514005\" }\r\n }";

		System.out.println("Payload :" + payload);

		RequestBody body = RequestBody.create(
				MediaType.parse("application/json"), payload.getBytes());
		RequestBody requestBody = new MultipartBuilder()
				.type(MultipartBuilder.MIXED)
				.addPart(body)
				.addFormDataPart("image", file.getName(),
						RequestBody.create(faxContentType, file)).build();
		

		Response response = platform.sendRequest("post",
			"/restapi/v1.0/account/~/extension/~/greeting", requestBody, null);

		return response;

	}

	void send() {
		Greeting fax = new Greeting(platform);

		try {

			Response response = fax.createGreeting(MediaType.parse("audio/wav"));
			
			
			System.out.println(response.request().headers());

			System.out.println(response.code());
			
			// System.out.println(response.());
			// System.out.println(response.headers());
			System.out.println(response.body().string());

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}

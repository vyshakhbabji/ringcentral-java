package utils;

import http.ApiResponse;

import java.io.File;
import java.io.IOException;

import platform.Platform;
import platform.Platform.ContentTypeSelection;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class SendFax {

	File file;
	Platform platform;

	public SendFax(Platform platform, File file) {
		this.platform = platform;
		this.file = file;
	}

	public Response createFax(String filename, String toNumber,
			String faxResolution, MediaType faxContentType) throws IOException {

		String payload = "{\"to\":[{\"phoneNumber\":\"15856234212\"}]}";

		System.out.println("Payload :" + payload);

		RequestBody body = RequestBody.create(
				MediaType.parse("application/json"),
				payload.getBytes());
		RequestBody requestBody = new MultipartBuilder()
				.type(MultipartBuilder.MIXED)
				.addPart(body)
				.addFormDataPart("image", file.getName(),
						RequestBody.create(faxContentType, file)).build();
		


		Response response = platform.sendRequest("post",
				"/restapi/v1.0/account/~/extension/~/fax", requestBody, null);
		
		return response;

	}

	 void send(){
	 SendFax fax= new SendFax(null, new File("/Users/vyshakh.babji/Desktop/1098-T.pdf"));
	
	 try {
	
	
	 Response response = fax.createFax("hi.pdf", "15856234120", "high",
	 MediaType.parse("text/plain"));
	
	 //System.out.println(response.());
	 //System.out.println(response.headers());
	 System.out.println(response.body().string());
	
	
	 } catch (IOException e) {
	
	 e.printStackTrace();
	 }
	
	 }

}

//package http;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map.Entry;
//
//import platform.Auth;
//import platform.Platform;
//
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.RequestBody;
//import com.squareup.okhttp.Response;
//import com.squareup.okhttp.Request.Builder;
//
//public class Client {
//
//	Request request;
//	Response response;
//	protected Platform p;
//	protected Auth auth;
//	
//	public Client(Platform platform, Auth auth){
//		this.auth= auth;
//		this.p=platform;
//	}
//	
//	
//	
//	protected String getAuthHeader() {
//		return this.auth.tokenType() + " " + p.getAccessToken();
//	}
//	
//	protected Builder requestBuilder(HashMap<String, String> hm) {
//
//		if (hm == null) {
//			hm = new HashMap<String, String>();
//		}
//		hm.put("Authorization", getAuthHeader());
//		
//
//		Builder requestBuilder = new Request.Builder();
//		for (Entry<String, String> entry : hm.entrySet()) {
//			requestBuilder.addHeader(entry.getKey(), entry.getValue());
//		}
//		return requestBuilder;
//	}
//	
//	public APIResponse send(String method, String apiURL, RequestBody body,
//			HashMap<String, String> headerMap) throws IOException {
//
//		// this.isAuthorized();
//		String URL = server.value + apiURL;
//		OkHttpClient client = new OkHttpClient();
//
//		try {
//			System.out.println(getAuthHeader());
//			if (method.equalsIgnoreCase("get")) {
//				request = requestBuilder(headerMap).url(URL).build();
//			} else if (method.equalsIgnoreCase("delete")) {
//				request = requestBuilder(headerMap).url(URL).delete().build();
//			} else {
//				if (method.equalsIgnoreCase("post")) {
//					request = requestBuilder(headerMap).url(URL).post(body)
//							.build();
//				} else if (method.equalsIgnoreCase("put")) {
//					request = requestBuilder(headerMap).url(URL).put(body)
//							.build();
//				}
//			}
//
//		} catch (Exception e) {
//			System.err.print("Failed APICall. Exception occured in Class:  "
//					+ this.getClass().getName() + ": " + e.getMessage()
//					+ l.getClassName() + "/" + l.getMethodName() + ":"
//					+ l.getLineNumber());
//		}
//		response = client.newCall(request).execute();
//		return new APIResponse(response);
//	}
//}

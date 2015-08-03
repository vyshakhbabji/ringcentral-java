package http;

import java.util.HashMap;

public class RCRequest {
	HashMap<String, String> body;
	String method;
	String query;
	public RCHeaders RCHeaders;
	String url;

	public RCRequest(HashMap<String, String> body,
			HashMap<String, String> headerMap) {
		RCHeaders = new RCHeaders();
		this.method = headerMap.get("method");
		this.url = headerMap.get("url");
		if (headerMap.containsKey("query")) {
			this.query = headerMap.get("query");
		} else {
			this.query = "";
		}
		if (headerMap.containsKey("Content-Type")) {
			this.RCHeaders.setContentType(headerMap.get("Content-Type"));
		}
		if (body != null) {
			this.body = body;
		} else {
			this.body = null;
		}
	}

}

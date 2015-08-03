package http;

import java.util.HashMap;
import java.util.Map;

public class RCHeaders {

	public static String CONTENT_TYPE = "Content-Type";

	public static final String JSON_CONTENT_TYPE = "application/json";
	public static final String MULTIPART_CONTENT_TYPE = "multipart/mixed";
	public static final String URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";
	HashMap<String, String> hmHeader;

	public RCHeaders() {
		this.hmHeader = new HashMap<>();
	}

	public RCHeaders(HashMap<String, String> headers) {
		this.hmHeader = headers;
	}

	public String getContentType() {
		return hasHeader(CONTENT_TYPE) ? getHeader(CONTENT_TYPE) : "";
	}

	public String getHeader(String key) {
		return this.hmHeader.get(key);
	}

	public HashMap<String, String> getHeaders() {
		return hmHeader;
	}

	public String[] getHeadersArray() {
		String[] array = new String[this.hmHeader.size()];
		int count = 0;
		for (Map.Entry<String, String> entry : this.hmHeader.entrySet()) {
			array[count] = entry.getKey() + ":" + entry.getValue();
			count++;
		}
		return array;
	}

	public boolean hasHeader(String key) {
		return this.hmHeader.containsKey(key);
	}

	public boolean isContentType(String contentType) {
		return (this.hmHeader.get(CONTENT_TYPE).contains(contentType));
	}

	public boolean isJson() {
		return isContentType(JSON_CONTENT_TYPE);
	}

	public boolean isMultipart() {
		return isContentType(MULTIPART_CONTENT_TYPE);
	}

	public boolean isURLEncoded() {
		return isContentType(URL_ENCODED_CONTENT_TYPE);
	}

	public void setContentType(String contentType) {
		this.hmHeader.put(CONTENT_TYPE, contentType);
	}

	public void setHeader(String key, String val) {
		this.hmHeader.put(key, val);
	}

	public void setHeaders(HashMap<String, String> headers) {
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			setHeader(entry.getKey(), entry.getValue());
		}
	}
}

package http;


import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import platform.Platform.ContentTypeSelection;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;


public class APIResponse {

    protected Request request;
    protected Response response;

    public APIResponse(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

 
    public boolean ok() {
        int status = this.response.code();
        return (status >= 200 && status < 300);
    }

    public ResponseBody raw() {
        return this.body();
    }

    public ResponseBody body() {
        return this.response.body();
    }
    
    public String text() throws IOException {

        String responseAsText = "";
        try {
            responseAsText = response.body().string();
            return responseAsText;
        } catch (IOException e) {
        	throw e;
        	//  System.err.print("IOException occured while converting the HTTP response to string in Class:  " + this.getClass().getName() + ": " + e.getMessage());
        }
     
    }
    
    //json_dict not necessary
    
    public JSONObject json() {
        JSONObject object = new JSONObject();
        try {
            object = new JSONObject(response.body().string());
            throw new IOException();
        } catch (JSONException e) {
            System.err.print("JSONException occured while converting the HTTP response to JSON in Class:  " + this.getClass().getName() + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.print("IOException occured while converting the HTTP response to JSON in Class:  " + this.getClass().getName() + ": " + e.getMessage());
        }
        return object;
    }
    
//    public APIResponse multipart() throws Exception{
//    	
//    	if(this.isContentType(ContentTypeSelection.MULTIPART_TYPE_MARKDOWN.value.toString())){
//    		throw new Exception("Exception occured.Response is not Batch (Multipart) ");
//    	}
//    	
//    	
//    	
//    }
    
    
    

    @SuppressWarnings("finally")
	public String error() {
        if (this.response == null || this.ok()) {
            return null;
        }

        String message = "HTTP" + this.response().code();

        JSONObject data;


        try {
            data = this.json();
            if (data.getString("message") != null) message = message + data.getString("message");
            else if (data.getString("error_description") != null)
                message = message + data.getString("error_description");
            else if (data.getString("description") != null)
                message = message + data.getString("description");

        } catch (JSONException e) {
            message = message + "JSONException occured in Class:  " + this.getClass().getName() + ": " + e.getMessage();
            System.err.print("JSONException occured in Class:  " + this.getClass().getName() + ": " + e.getMessage());
        } finally {
            return message;
        }
    }
    
    public Request request() {
        return this.response.request();
    }

    public Response response() {
        return this.response;
    }


    protected String getContentType() {
        return this.response.headers().get("Content-Type");
    }

    protected boolean isContentType(String contentType) {
        return this.response().body().contentType().toString().equalsIgnoreCase(contentType);
    }


    //todo: multipart def
    //todo: break_into_parts

}
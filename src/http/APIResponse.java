package http;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;


public class APIResponse {

    protected Response response;
    protected Request request;

    public APIResponse(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

    public Request request() {
        return this.response.request();
    }

    public Response response() {
        return this.response;
    }

    public boolean ok() {
        int status = this.response.code();
        return (status >= 200 && status < 300);
    }

    public Response raw() {
        return response();
    }

    public ResponseBody body() {
        return this.response.body();
    }

    public String text() {

        String responseAsText = "";
        try {
            responseAsText = response.body().string();
            throw new IOException();
        } catch (IOException e) {
            System.err.print("IOException occured while converting the HTTP response to string in Class:  " + this.getClass().getName() + ": " + e.getMessage());
        }

        return responseAsText;
    }

    public JSONObject json_dict() {

        JSONObject jObject = new JSONObject();
        try {
            if (isContentType("application/json"))
                jObject = new JSONObject(text());
            else {
                throw new IOException();
            }
        } catch (JSONException e) {
            System.err.print("JSONException occured while converting the HTTP response to JSON Dictonary in Class:  " + this.getClass().getName() + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.print("IOException occured while converting the HTTP response to JSON Dictonary in Class:  " + this.getClass().getName() + ": " + e.getMessage());
        }
        return jObject;
    }


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

    public HashMap hashMap() throws IOException {
        Gson gson = new Gson();
        Type mapType = new TypeToken<HashMap<String, String>>() {
        }.getType();
        HashMap<String, String> jsonMap = new HashMap<>();
        try {
            jsonMap = gson.fromJson(this.text(), mapType);
            throw new IOException();
        } catch (IOException e) {
            System.err.print("IOException occured while converting the HTTP response to JSON in Class:  " + this.getClass().getName() + ": " + e.getMessage());
        }
        return jsonMap;
    }


    public boolean isContentType(String contentType) {
        return this.response().body().contentType().toString().equalsIgnoreCase(contentType);
    }

    public String getContentType() {
        return this.response.headers().get("Content-Type");
    }

    @SuppressWarnings("finally")
	public String error() {
        if (this.response == null || this.ok()) {
            return null;
        }

        String message = "HTTP" + this.response().code();

        JSONObject data;


        try {
            data = this.json_dict();
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

    //todo: multipart def
    //todo: break_into_parts


}
package subscription;


import com.google.gson.JsonObject;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import platform.Platform;

import org.json.JSONException;
import org.json.JSONObject;

import http.APIResponse;

import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.tls.CipherSuite;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

public class Subscription{

	
    public Pubnub pubnub;
    ArrayList<String> eventFilters = new ArrayList<>();
    String expirationTime = "";
    int expiresIn = 0;
    public IDeliveryMode deliveryMode = new IDeliveryMode();
    public String id = "";
    String creationTime = "";
    String status = "";
    String uri = "";
    
    Platform platform;

    
    public class IDeliveryMode {
        public String transportType = "Pubnub";
        public boolean encryption = false;
        public String address = "";
        public String subscriberKey = "";
        public String secretKey = "";
        public String encryptionKey = "";
    }

    public Subscription(Platform platform){
    	
    	this.platform = platform;
    }

    public void updateSubscription(JSONObject responseJson) throws JSONException{
        id = responseJson.getString("id");
        JSONObject deliveryMode = responseJson.getJSONObject("deliveryMode");
        this.deliveryMode.encryptionKey = deliveryMode.getString("encryptionKey");
        this.deliveryMode.address = deliveryMode.getString("address");
        this.deliveryMode.subscriberKey = deliveryMode.getString("subscriberKey");
        this.deliveryMode.secretKey = "sec-c-ZDNlYjY0OWMtMWFmOC00OTg2LWJjMTMtYjBkMzgzOWRmMzUz";//deliveryMode.getString("secretKey");
    }



    public Pubnub getPubnub() {
        return pubnub;
    }

    public void subscribe(JSONObject subscriptionResponse, Callback c) {
        try {
            updateSubscription(subscriptionResponse);
            pubnub = new Pubnub("", deliveryMode.subscriberKey, deliveryMode.secretKey);
            pubnub.subscribe(this.deliveryMode.address, c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addEvents(String[] events) {
        for(String event:events){
            this.eventFilters.add(event);
        }
    }

    public void setEvents(String[] events){
        this.eventFilters = new ArrayList<String>(Arrays.asList(events));
    }

    private ArrayList getFullEventFilters(){
        return this.eventFilters;
    }

    boolean isSubscribed(){
        return !(this.deliveryMode.subscriberKey.equals("") && this.deliveryMode.address.equals(""));
    }

    public void unsubscribe() {
        if((this.pubnub != null) && this.isSubscribed())
            this.pubnub.unsubscribe(deliveryMode.address);
    }

    public String notify(String message, String encryptionKey){
    	Security.addProvider(new BouncyCastleProvider());
    	System.out.println(message);
        byte[] key = Base64.decode(encryptionKey);
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        byte[] data = Base64.decode(message);
        String decryptedString = "";
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] decrypted = cipher.doFinal(data);
            decryptedString = new String(decrypted);
        } catch(Exception e){
            e.printStackTrace();
        }
        System.out.println(decryptedString);
        return decryptedString;
    }
   
}

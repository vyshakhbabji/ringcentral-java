package subscription;

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
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import platform.Platform;
import subscription.SubscriptionPayload.DeliveryMode;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
//import com.pubnub.api.Pubnub;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import core.RingCentralException;

public class Subscription {

	public class IDeliveryMode {
		public String address = "";
		public boolean encryption = false;
		public String encryptionKey = "";
		public String secretKey = "sec-c-ZDNlYjY0OWMtMWFmOC00OTg2LWJjMTMtYjBkMzgzOWRmMzUz";
		public String subscriberKey = "";
		public String transportType = "Pubnub";
	}

	ObjectMapper mapper = new ObjectMapper();

	String creationTime = "";
	public IDeliveryMode deliveryMode = new IDeliveryMode();
	ArrayList<String> eventFilters = new ArrayList<>();
	ScheduledExecutorService exec = Executors
			.newSingleThreadScheduledExecutor();
	String expirationTime = "";
	int expiresIn = 0;
	Future future = null;
	public String id = "";
	Platform platform;
	public Pubnub pubnubObj;
	String status = "";
	Subscription subscription;

	String SUBSCRIPTION_URL = "/restapi/v1.0/subscription/";

	String uri = "";

	public Subscription(Platform platform) {

		this.platform = platform;
		this.subscription = this;
	}

	public void addEvents(String[] events) {
		for (String event : events) {
			this.eventFilters.add(event);
		}
	}

	public String decrypt(String message) {
		Security.addProvider(new BouncyCastleProvider());
		byte[] key = Base64.decode(subscription.deliveryMode.encryptionKey);
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		byte[] data = Base64.decode(message);
		String decryptedString = "";
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] decrypted = cipher.doFinal(data);
			decryptedString = new String(decrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decryptedString;
	}

	public ArrayList<String> getFullEventFilters() {
		return this.eventFilters;
	}

	public Pubnub getPubnub() {
		return pubnubObj;
	}

	public boolean isSubscribed() {
		return !(this.deliveryMode.subscriberKey.equals("") && this.deliveryMode.address
				.equals(""));
	}

	public void remove() {
		String url = SUBSCRIPTION_URL + subscription.id;
		Response r = platform.sendRequest("delete", url, null, null);

		if (r.isSuccessful()) {
			unsubscribe();
		} else {
			System.out.println("Subscription Delete Failed");
		}

	}

	private void renew() throws IOException {

		System.out.println("Renewing Subscription");

		if (!subscription.isSubscribed())
			throw new Error("No subscription");
		if (subscription.getFullEventFilters().size() == 0)
			throw new Error("Events are undefined");
		SubscriptionPayload subscriptionPayload = new SubscriptionPayload(
				subscription.getFullEventFilters().toArray(new String[0]),
				new DeliveryMode("PubNub", "false"));
		String payload = new GsonBuilder().create().toJson(subscriptionPayload);
		RequestBody body = RequestBody.create(MediaType
				.parse("application/json"), payload.toString().getBytes());

		Response r = platform.sendRequest("put", SUBSCRIPTION_URL + this.id,
				body, null);
		if (r.isSuccessful()) {
			String responseBody = r.body().string();
			updateSubscription(new JSONObject(responseBody));
			subscribeToPubnub();
		} else {
			reset();
			System.out.println("Subscription renewal failed");
		}
	}

	private void renewSubscription() {
		if (future != null && !future.isDone()) {
			future.cancel(true);
			return;
		}

		future = exec.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							renew();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		}, this.expiresIn - 60, this.expiresIn - 30, TimeUnit.SECONDS);
	}

	public void reset() {
		exec.shutdown();
		if (this.isSubscribed())
			this.unsubscribe();
	}

	private void setEvents(String[] events) {
		this.eventFilters = new ArrayList<String>(Arrays.asList(events));
	}


	public Response createSubscription(){
		SubscriptionPayload subscriptionPayload = new SubscriptionPayload(
				subscription.getFullEventFilters().toArray(new String[0]),
				new DeliveryMode("PubNub", "false"));
		String payload = new GsonBuilder().create().toJson(subscriptionPayload);
		RequestBody body = RequestBody.create(MediaType
				.parse("application/json"), payload.toString().getBytes());
		return platform.sendRequest("post", SUBSCRIPTION_URL, body, null);
	}

	public void subscribe() throws IOException {

		Response r = createSubscription();

		if (r.isSuccessful()) {
			String responseBody = r.body().string();
			System.out.println(responseBody);
			updateSubscription(new JSONObject(responseBody));
			renewSubscription();
			subscribeToPubnub();
		} else {
			String responseBody = r.body().string();
			System.out.println(responseBody);
			System.out.println("Subscription Failed");
		}
	}

	private void subscribeToPubnub() {

		pubnubObj = new Pubnub("", subscription.deliveryMode.subscriberKey,
				deliveryMode.secretKey);
		try {
			pubnubObj.subscribe(deliveryMode.address,
					new com.pubnub.api.Callback() {
				@Override
				public void connectCallback(String channel,
						Object message) {
					System.out.println(channel + " : "
							+ message.toString());

				}

				@Override
				public void disconnectCallback(String channel,
						Object message) {
					System.out.println("Disconnect" + channel + " : "
							+ message.toString());
				}

				@Override
				public void errorCallback(String channel,
						PubnubError error) {
					System.out.println("Error " + channel + " : "
							+ error.getErrorString());
				}

				@Override
				public void reconnectCallback(String channel,
						Object message) {
					System.out.println(" Reconnect " + channel + " : "
							+ message.toString());
				}

				@Override
				public void successCallback(String channel,
						Object message) {
					String decryptedString = subscription
							.decrypt(message.toString());

					try {
						Object json = mapper.readValue(decryptedString, Object.class);
						String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
						System.out.println("SUCCESS: " + indented
								+ " , " + channel);
						System.out.println("SUCCESS: " + decryptedString
								+ " , " + channel);
					} catch (JsonParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} catch (PubnubException e) {
			throw new RingCentralException(e);
		}
	}

	public void unsubscribe() {
		if ((this.pubnubObj != null) && this.isSubscribed()) {
			exec.shutdown();
			this.pubnubObj.unsubscribe(deliveryMode.address);
		}
	}

	private void updateSubscription(JSONObject responseJson)
			throws JSONException {
		id = responseJson.getString("id");
		JSONObject deliveryMode = responseJson.getJSONObject("deliveryMode");
		this.expiresIn = (int) responseJson.get("expiresIn");
		this.deliveryMode.encryptionKey = deliveryMode
				.getString("encryptionKey");
		this.deliveryMode.address = deliveryMode.getString("address");
		this.deliveryMode.subscriberKey = deliveryMode
				.getString("subscriberKey");
	}

}

package utils;

import java.awt.Desktop;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.JSONObject;

import platform.Platform;
import platform.Platform.Server;
import subscription.Subscription;

import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import core.SDK;
import java.awt.event.*;




public class Main {

public static void main(String[] args) {

		SDK sdk = new
				SDK("","",
						Server.PRODUCTION);
		

		Platform platform;
		platform = sdk.platform();
		//		utils.Server.startServer();

		try {

			Response r2 = platform.login("","","");

			/*******************************************************************************/
			/*******************************************************************************/

			Response rGet = platform.sendRequest("get", "/restapi/v1.0/account/~/extension/~/answering-rule", null, null);//dateFrom=2019-02-12&view=Detailed
			System.out.println(rGet.headers());
			System.out.println(rGet.body().string());
			System.out.println(rGet.code());


			/*******************************************************************************/
			/*******************************************************************************/

			//									Subscribe subscribe = new Subscribe();
			//									Subscription s = subscribe.subscribe(platform);
			//									PubNub p = s.getPubnub();
			//			
			//									p.addListener(new SubscribeCallback() {
			//										@Override
			//										public void status(PubNub pubnub, PNStatus status) {
			//											System.out.println(status);
			//										}
			//							
			//										@Override
			//										public void message(PubNub pubnub, PNMessageResult message) {
			//							
			//											System.out.println("Key = "+s.deliveryMode.encryptionKey);
			//											System.out.println(message.getMessage());
			//											if(message.getChannel()!=null) {
			//												System.out.println(message.getMessage());
			//												System.out.println(message.getChannel());
			//											}
			//										}
			//							
			//										@Override
			//										public void presence(PubNub pubnub, PNPresenceEventResult presence) {
			//													System.out.println();
			//										}
			//									});
			/*******************************************************************************/


			//						SendFax f =  new SendFax(platform);


			/*******************************************************************************/




		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}




package bot;

import http.ApiException;

import java.io.IOException;

import org.json.JSONObject;

import platform.Platform;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class Posts {

	Platform platform;

	public Posts(Platform platform) {
		super();
		this.platform = platform;
	}


	public byte[] createPostMessage(String groupId , String message) {
		JSONObject postMsg = new JSONObject();
		postMsg.put("groupId", groupId);
		postMsg.put("text", message);
		return postMsg.toString().getBytes();
	}


	public Response sendMessage( String groupId, String message) {
		Response res= null;
		try {
			RequestBody body = RequestBody.create(
					MediaType.parse("application/json"), createPostMessage(groupId,message));
			res = platform.sendRequest("post","/restapi/v1.0/glip/posts", body, null);
			if(res.code()==200) 
				System.out.println("Message Posted Successfully");
			else

				System.out.println("Error with post with error code:"+res.code()+" with message: "+
						res.body().string());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public Response getPosts( String groupId,String pageToken, String recordCount) {
		Response res= null;
		if(groupId.equals("") || groupId == null) {
			throw new ApiException("GroupID cannot be null");
		}
		String append = "";
		append+= "groupId="+groupId;
		append+="&";
		append+= pageToken.equals("") ?"pageToken=":"pageToken="+pageToken;
		append+="&";
		append+=recordCount.equals("")?"recordCount=":"recordCount="+recordCount;
		res = platform.sendRequest("get","/restapi/v1.0/glip/posts?"+append, null, null);
		return res;
	}

	public Response getPostById(String postId) {
		if(postId.equals("") || postId == null) {
			throw new ApiException("GroupID cannot be null");
		}
		Response res = platform.sendRequest("get","/restapi/v1.0/glip/posts/"+postId, null, null);
		return res;
	}
}
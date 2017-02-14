package bot;

import http.ApiException;
import platform.Platform;

import com.squareup.okhttp.Response;

public class Person {

	Platform platform;
	
	public Person(Platform platform) {
		super();
		this.platform = platform;
	}

	public Response getPersonById(String personId) {
		if(personId.equals("") || personId == null) {
			throw new ApiException("GroupID cannot be null");
		}
		Response res = platform.sendRequest("get","/restapi/v1.0/glip/persons/"+personId, null, null);
		return res;
	}
}

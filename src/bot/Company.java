package bot;

import http.ApiException;
import platform.Platform;

import com.squareup.okhttp.Response;

public class Company {

	Platform platform;

	public Company(Platform platform) {
		super();
		this.platform = platform;
	}

	public Response getCompaniesById(String companyId) {
		if(companyId.equals("") || companyId == null) {
			throw new ApiException("GroupID cannot be null");
		}
		Response res = platform.sendRequest("get","/restapi/v1.0/glip/companies/"+companyId, null, null);
		return res;
	}
}

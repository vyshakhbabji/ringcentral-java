package platform;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class Auth {

	protected String access_token;
	protected Date expire_time;
	protected String expires_in;
	protected String owner_id;
	protected String refresh_token;
	protected Date refresh_token_expire_time;
	protected String refresh_token_expires_in;
	protected String remember;
	protected String scope;
	protected String token_type;

	public Auth() {
		this.reset();
	}

	public String accessToken() {
		return this.access_token;
	}

	public boolean accessTokenValid() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(this.expire_time);
		return isTokenDateValid(cal);
	}

	public String getRefreshToken() {
		return this.refresh_token;
	}

	protected boolean isTokenDateValid(GregorianCalendar token_date) {
		return (token_date.compareTo(new GregorianCalendar()) > 0);
	}

	public String refreshToken() {
		return this.refresh_token;
	}

	public boolean refreshTokenValid(){
		GregorianCalendar cal = new GregorianCalendar();
		if (this.refresh_token_expire_time != null)
			cal.setTime(this.refresh_token_expire_time);
		return this.isTokenDateValid(cal);
	}


	public void reset() {
		this.token_type = "";
		this.remember = "";

		this.access_token = "";
		this.expires_in = "";
		this.expire_time = null;

		this.refresh_token = "";
		this.refresh_token_expires_in = "";
		this.refresh_token_expire_time = null;

		this.scope = "";
		this.owner_id = "";
	}

	public Auth setData(Map<String, String> authData) {
		
		if(authData==null)
			return this;

		if (authData.containsKey("remember"))
			this.remember = authData.get("remember");

		if (authData.containsKey("token_type")) {
			this.token_type = authData.get("token_type");
		}
		if (authData.containsKey("scope")) {
			this.scope = authData.get("scope");
		}
		if (authData.containsKey("owner_id")) {
			this.owner_id = authData.get("owner_id");
		}
		if (authData.containsKey("access_token")) {
			this.access_token = authData.get("access_token");
		}
		if (authData.containsKey("expires_in")) {
			this.expires_in = authData.get("expires_in");
		}
		if (!authData.containsKey("expire_time")
				&& authData.containsKey("expires_in")) {
			int expiresIn = Integer.parseInt(authData.get("expires_in"));
			Calendar calendar = new GregorianCalendar();
			calendar.add(Calendar.SECOND, expiresIn);
			this.expire_time = calendar.getTime();

		}
		
		//refresh token
		
		if (authData.containsKey("refresh_token")) {
			this.refresh_token = authData.get("refresh_token");
		}
		
		
		if (authData.containsKey("refresh_token_expires_in")) {
			this.refresh_token_expires_in = authData
					.get("refresh_token_expires_in");
		}
		if (!authData.containsKey("refresh_token_expire_time")
				&& authData.containsKey("refresh_token_expires_in")) {
			int expiresIn = Integer.parseInt(authData
					.get("refresh_token_expires_in"));
			Calendar calendar = new GregorianCalendar();
			calendar.add(Calendar.SECOND, expiresIn);
			this.refresh_token_expire_time = calendar.getTime();
		}
		
		return this;
	}

	public String tokenType() {
		return this.token_type;
	}
}

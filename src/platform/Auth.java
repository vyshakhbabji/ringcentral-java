package platform;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

public class Auth {

	String access_token;
	Date expire_time;
	String expires_in;
	String owner_id;
	String refresh_token;
	Date refresh_token_expire_time;
	String refresh_token_expires_in;
	String scope;
	String token_type;

//	public Auth() {
//		token_type = "";
//		access_token = "";
//		expires_in = "";
//		expire_time = null;
//		refresh_token = "";
//		refresh_token_expires_in = "";
//		refresh_token_expire_time = null;
//		scope = "";
//		owner_id = "";
//	}

	public String getAccessToken() {
		return this.access_token;
	}

	public Auth getData() {
		return this;
	}

	public String getRefreshToken() {
		return this.refresh_token;
	}

	public String getTokenType() {
		return this.token_type;
	}

	public boolean isAccessTokenValid() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(this.expire_time);
		return isTokenDateValid(cal);
	}

	public boolean isRefreshTokenValid() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(this.refresh_token_expire_time);
		return isTokenDateValid(cal);
	}

	public boolean isTokenDateValid(GregorianCalendar token_date) {
		return (token_date.compareTo(new GregorianCalendar()) > 0);
	}

	public void setData(Map<String, String> authData) {

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
		
//		System.out.println(this.expire_time);
//		System.out.println(this.expires_in);
//		System.out.println(this.refresh_token_expires_in);
//		System.out.println(this.refresh_token_expire_time);
	}
}

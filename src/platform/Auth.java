package platform;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
	String remember;



	public Auth() {
		this.token_type = "";
		this.remember =  "";

		this.access_token = "";
		this.expires_in = "";
		this.expire_time = null;

		this.refresh_token = "";
		this.refresh_token_expires_in = "";
		this.refresh_token_expire_time = null;

		this.scope = "";
		this.owner_id = "";
	}


	public void reset() {
		this.token_type = "";
		this.remember =  "";

		this.access_token = "";
		this.expires_in = "";
		this.expire_time = null;

		this.refresh_token = "";
		this.refresh_token_expires_in = "";
		this.refresh_token_expire_time = null;

		this.scope = "";
		this.owner_id = "";
	}


	public void setData(Map<String, String> authData) {

		//none

		if(authData.containsKey("remember"))
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



	public String accessToken() {
		return this.access_token;
	}

	//	public boolean accessTokenValid() {
	//		GregorianCalendar cal = new GregorianCalendar();
	//		cal.setTime(this.expire_time);
	//		return isTokenDateValid(cal);
	//	}

	public HashMap<String, String> data() {
		//hashmap and return 

		HashMap<String, String> authData= new HashMap<String, String>();

		authData.put("remember", this.remember);


		authData.put("token_type", this.token_type);

		authData.put("access_token", this.access_token);
		authData.put("expires_in", this.expires_in);
		authData.put("expire_time", this.expire_time.toString());

		authData.put("refresh_token", this.refresh_token);
		authData.put("refresh_token_expires_in", this.refresh_token_expires_in);
		authData.put("refresh_token_expire_time", this.refresh_token_expire_time.toString());

		authData.put("scope", this.scope);
		authData.put("owner_id", this.owner_id);


		return authData;
	}

	protected boolean isTokenDateValid(GregorianCalendar token_date) {
		return (token_date.compareTo(new GregorianCalendar()) > 0);
	}

	public String refreshToken() {
		return this.refresh_token;
	}

//	public boolean refreshTokenValid() {
//		GregorianCalendar cal = new GregorianCalendar();
//		cal.setTime(this.refresh_token_expire_time);
//		return isTokenDateValid(cal);
//	}


	public String tokenType() {
		return this.token_type;
	}

	public boolean accessTokenValid() throws ParseException{

		DateFormat format = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" );
		Date       date = format.parse(this.data().get( "expire_time"));
		GregorianCalendar   calendar = new GregorianCalendar();

		calendar.setTime( date );

		return this.isTokenDateValid(calendar);
	}

	public boolean refreshTokenValid() throws ParseException{

		DateFormat format = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" );
		Date       date = format.parse(this.data().get( "refresh_token_expire_time"));
		GregorianCalendar   calendar = new GregorianCalendar();

		calendar.setTime( date );

		return this.isTokenDateValid(calendar);
	}
}

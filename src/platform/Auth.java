package platform;

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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

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

	public boolean refreshTokenValid() {
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

	public Auth setData(HashMap<String, String> authData) {

		if (authData == null)
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

		// refresh token

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

# ringcentral-java
[![Build Status](https://travis-ci.org/vyshakhbabji/ringcentral-java.svg?branch=master)](https://travis-ci.org/vyshakhbabji/ringcentral-java)
[![Coverage Status](https://coveralls.io/repos/vyshakhbabji/ringcentral-java/badge.svg?branch=master&service=github)](https://coveralls.io/github/vyshakhbabji/ringcentral-java?branch=master)


## Overview

This is a Java SDK for the RingCentral for Developers Platform REST API (https://developers.ringcentral.com).

The core SDK objects follow the general design of the [official RingCentral SDKs](https://github.com/ringcentral). 
This SDK is an early stage library and subject to breaking changes.

### Included

* Authorization via `OAuth2` with authorization code and password grant flows including token refresh
* Subscriptions via `Pubnub` with auto-decryption

### Instantiate the RingCentral object

The SDK is represented by the global RingCentral constructor. Your application must create an instance of this object:

In order to bootstrap the RingCentral JavaScript SDK, you have to first get a reference to the Platform singleton and then configure it. Before you can do anything using the Platform singleton, you need to configure it with the server URL (this tells the SDK which server to connect to) and your unique API key (this is provided by RingCentral's developer relations team).

```java

	SDK sdk = new SDK("`appKey`", "`appSecret`",`Server.SANDBOX` or `Server.Production`);

```

### Get the Platform singleton

Now that you have your platform singleton and SDK has been configured with the correct server URL and API key, your application can log in so that it can access the features of the API.

```java
		
		Platform platform = sdk.platform();
		try {
			Response r = platform.login("`rcPhoneNumber`", "`extension`", "`password`");
		} catch (Exception e) {
			e.printStackTrace();
		}
```


### Making api calls

```java
		
		...
			 platform.login("`rcPhoneNumber`", "`extension`", "`password`");
		...
			Response response = platform.sendRequest(<HTTP Method as string - "get" or "post" or "put" or "delete">, <apiURL endpoint as string - "/restapi/v1.0/account/~/extension/~/call-log"> , <com.squareup.okhttp.ResponseBody body>, <Headers as <String,String> hashmap Eg. hm.put("Authorization","hrifeigjaiereanreowrjewpojr==") >)
			
			//responseObject can be consumed as below
			
			response.code();
			response.headers();
			response.body().string();
			response.isSuccessful();
		
```



### Server-side Subscriptions
Subscriptions are a convenient way to receive updates on server-side events, such as new messages or presence changes.
Subscriptions are created by calling the `subscribe()` method of the RingCentral instance created earlier on.

```java
	
	void subscribe(Platform p) {
		final Subscription sub = new Subscription(p);

		String[] s = { "/restapi/v1.0/account/~/extension/~/presence",
				"/restapi/v1.0/account/~/extension/~/message-store" };

		sub.addEvents(s);

		try {
			sub.subscribe();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

```

### Removing Subscriptions from server

Once a subscription has been created, the SDK takes care of renewing it automatically. To cancel a subscription, you can call the subscription instance's `remove()` method:

```java
	
		...
		final Subscription sub = new Subscription(p);
		
		...
			sub.remove();
		...

```

### Subscription reset

To revert subscription instance to it's persistant state you can use its reset() and off() methods, this will close PUBNUB channel, remove all timers, subscription data and all bindings:

```java
	
		...
		final Subscription sub = new Subscription(p);
		
		...
			sub.reset();
		...

```

### To Do

---

## Documentation

Refer to the official RingCentral guides for
more information on individual API calls:

1. [API Developer and Reference Guide](https://developers.ringcentral.com/api-docs/latest/index.html) for information on specific APIs.
1. [API Explorer](http://ringcentral.github.io/api-explorer/)
1. [Dev Tutorial](http://ringcentral.github.io/tutorial/)


